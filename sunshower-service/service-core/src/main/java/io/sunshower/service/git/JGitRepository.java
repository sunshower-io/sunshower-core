package io.sunshower.service.git;

import io.sunshower.io.ReaderInputStream;
import io.sunshower.model.core.auth.Details;
import io.sunshower.model.core.auth.User;
import io.sunshower.model.core.faults.SystemException;
import io.sunshower.service.revision.model.Repository;
import io.sunshower.service.revision.model.Revision;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.LockFile;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by haswell on 5/22/17.
 */
public class JGitRepository implements GitRepository {

    private       Git                   git;
    private       boolean               open;
    private final File                  local;
    private final User                  session;
    private final Repository            repository;
    private final AtomicReference<Lock> lock;

    public JGitRepository(
            final Repository repository,
            final User session
    ) {
        this.session = session;
        this.repository = repository;
        this.local = Paths.get(
                repository.getLocal().getFile().getPath()
        ).toFile();
        this.lock = new AtomicReference<>();
    }

    public File getLocal() {
        return local;
    }

    
    public boolean isLocked(boolean localOnly) {
        final File parent = new File(local, ".git");
        final File index = new File(parent, "index");
        final LockFile lockFile = new LockFile(index);
        boolean lockedSuccessfully;
        try {
            lockedSuccessfully = lockFile.lock();
        } catch (IOException e) {
            lockedSuccessfully = false;
            //// TODO: 11/21/17 log 
        } finally {
            lockFile.unlock();
        }
        return !lockedSuccessfully;
    }

    @Override
    public boolean isLocked() {
        return isLocked(false);
    }

    @Override
    public boolean isClosed() {
        return !open;
    }

    @Override
    public void open() {
        if (open) {
            throw new IllegalStateException("Attempting to open an opened repository");
        }
        try {
            git = Git.open(local);
            open = true;
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Lock lock() {
        check();
        try (
                RandomAccessFile file = new RandomAccessFile(local, "w");
        ) {
            return setLock(file.getChannel());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void unlock() {
        Lock lock = this.lock.get();
        if (lock == null) {
            throw new IllegalStateException("Repository is not locked");
        }
        lock.unlock();
    }

    @Override
    public void close() throws Exception {
        if (!open) {
            throw new IllegalStateException("Attempting to close a closed repository");
        }
        git.close();
        git.getRepository().close();
    }


    @Override
    public File write(String name, Reader reader) {
        return write(name, new ReaderInputStream(reader));
    }

    @Override
    public File write(String name, InputStream inputStream) {

        final File file = new File(local, name);
        try {
            if (file.exists()) {
                Files.copy(
                        inputStream,
                        file.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            } else {
                final File parent = file.getParentFile();
                if (!parent.equals(local)) {
                    if (!(parent.exists() || parent.mkdirs())) {
                        throw new RuntimeException("Couldn't make parent file");
                    }
                }
                try (
                        FileOutputStream fileOutputStream = new FileOutputStream(file)
                ) {
                    io.sunshower.io.Files.copy(inputStream, fileOutputStream);
                    fileOutputStream.flush();
                }
            }
            git.add().addFilepattern(name).call();
            return file;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void initialize() {
        try {
            Git.init().setDirectory(local).call();
        } catch (GitAPIException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Revision commit() {
        return commit(buildMessage());
    }

    private String buildMessage() {
        return "sup";
    }


    @Override
    public Revision commit(String message) {
        check();
        Details details = session.getDetails();
        PersonIdent ident = new PersonIdent(
                details.getFirstname() +
                        " " +
                        details.getLastname(),
                details.getEmailAddress()
        );
        try {
            RevCommit call = git.commit()
                    .setAuthor(ident)
                    .setCommitter(ident)
                    .setMessage(message)
                    .call();
            return RevisionUtils.fromCommit(call);
        } catch (GitAPIException e) {
            throw new RepositoryException(e);
        }

    }

    @Override
    public File read(File p) {
        return null;
    }

    @Override
    public InputStream read(String path) {
        final File file = new File(local, path);
        try {
            return new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void checkout(Revision commit) {
        check();
        try {
            RevCommit revCommit = new RevWalk(git.getRepository())
                    .parseCommit(ObjectId
                            .fromString(commit.getRevision()));
            git.checkout()
                    .setCreateBranch(true)
                    .setName("working")
                    .setStartPoint(revCommit)
                    .call();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean exists(String path) {
        return new File(local, path).exists();
    }


    private void check() {
        if (!open) {
            throw new IllegalStateException("Repository must be opened");
        }
    }

    private Lock setLock(FileChannel channel) {
        final Lock lock = new RepositoryLock(channel);
        this.lock.set(lock);
        return lock;
    }


    private class RepositoryLock implements Lock {

        private FileLock    lock;
        final   FileChannel channel;

        private RepositoryLock(FileChannel channel) {
            this.channel = channel;
        }

        @Override
        public void lock() {
            try {
                lock = channel.tryLock();
            } catch (IOException e) {
                throw new SystemException(e);
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            lock();
        }

        @Override
        public boolean tryLock() {
            try {
                lock();
                return true;
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
            return tryLock();
        }
        

        @Override
        public void unlock() {
            try {
                lock.close();
            } catch (IOException e) {
                throw new SystemException(e);
            }
        }

        @NotNull
        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }

}
