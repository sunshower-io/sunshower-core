package io.sunshower.service.git;

import io.sunshower.common.io.Files;
import io.sunshower.service.revision.model.Revision;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by haswell on 5/22/17.
 */
public class JGitRepositoryUnitTest extends GitRepositoryTestCase {


    @Test
    public void ensureCheckingOutDirectoryCreatesGitDirectory() throws IOException {
        File r = Paths.get(root.getAbsolutePath(), "repo/local").toFile();
        assertThat(r.isDirectory(), is(true));
    }

    @Test
    public void ensureCheckingOutDirectoryCreatesGitFile() throws IOException {
        File r = Paths.get(root.getAbsolutePath(), "repo/local").toFile();
        assertThat(r.exists(), is(true));
        assertThat(new File(r, ".git").exists(), is(true));

    }

    @Test
    public void ensureAddingFileWorks() throws IOException {
        repository.open();
        StringReader reader = new StringReader("coolbeans");
        File write = repository.write("test.txt", reader);
        assertThat(write.exists(), is(true));
        assertThat(write.isFile(), is(true));

        String data = new String(Files.read(write));
        assertThat(data, is("coolbeans"));
    }


    @Test
    public void ensureAddingAndCommittingFileResultsInReadableRevisionAvailableInPreviousRevision() throws IOException {
        repository.open();
        StringReader reader = new StringReader("helloworld");
        repository.write("test.txt", reader);
        Revision commit = repository.commit();
        reader = new StringReader("lolol");
        repository.write("test.txt", reader);
        repository.commit();
        String d = new String(Files.read(repository.read("test.txt")));
        assertThat(d, is("lolol"));
        repository.checkout(commit);
        d = new String(Files.read(repository.read("test.txt")));
        assertThat(d, is("helloworld"));
    }

    @Test
    public void ensureAddingAndCommittingFileResultsInReadableRevision() throws IOException {
        repository.open();
        StringReader reader = new StringReader("coolbeans");
        File write = repository.write("test.txt", reader);
        Revision commit = repository.commit();
        repository.checkout(commit);
        InputStream read = repository.read("test.txt");
        String result = new String(Files.read(read));
        assertThat(result, is("coolbeans"));
    }

    @Test
    public void ensureAddingAndCommittingFileProducesRevisions() throws IOException {
        repository.open();
        StringReader reader = new StringReader("coolbeans");
        File write = repository.write("test.txt", reader);
        Revision commit = repository.commit();
        assertThat(ObjectId.isId(commit.getRevision()), is(true));
    }

}