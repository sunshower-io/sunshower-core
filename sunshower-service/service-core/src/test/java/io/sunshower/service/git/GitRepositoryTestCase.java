package io.sunshower.service.git;

import io.sunshower.io.Files;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.model.io.FileResolutionStrategy;
import io.sunshower.service.revision.model.Local;
import io.sunshower.service.revision.model.Remote;
import io.sunshower.service.revision.model.Repository;
import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class GitRepositoryTestCase extends SerializationTestCase {
  protected File root;
  protected Local local;
  protected Remote remote;
  protected Repository repo;
  protected GitRepository repository;
  protected FileResolutionStrategy strategy;

  public GitRepositoryTestCase() {
    super(SerializationAware.Format.JSON);
  }

  public GitRepositoryTestCase(SerializationAware.Format format, Class[] bound) {
    super(format, bound);
  }

  public GitRepositoryTestCase(
      SerializationAware.Format format, boolean includeRoot, Class[] bound) {
    super(format, includeRoot, bound);
  }

  @BeforeEach
  public void setUp() {
    strategy = new TestRepositoryResolver();
    root = new File(strategy.resolve(null, null, null), UUID.randomUUID().toString());

    repo = new Repository();
    local = new Local();
    local.setFile(
        new io.sunshower.model.core.io.File(
            Paths.get(root.getAbsolutePath()).resolve("repo/local").toString()));
    remote = new Remote();

    remote.setUri(Paths.get(root.getAbsolutePath()).resolve("repo/remote").toString());

    repo.setLocal(local);
    repo.setRemote(remote);
    final User user = new User();
    user.setUsername("joe");
    user.getDetails().setEmailAddress("joe@whatever.com");
    user.getDetails().setFirstname("frapper");
    user.getDetails().setLastname("dapper");

    repository = new JGitRepository(repo, user);
    repository.initialize();
  }

  @AfterEach
  public void tearDown() throws IOException {
    delete();
  }

  protected void delete() throws IOException {
    try {
      Files.delete(root);
    } catch (NullPointerException ex) {
      // meh
    }
  }

  protected InputStream input(String classpathResource) {
    return ClassLoader.getSystemResourceAsStream(classpathResource);
  }
}
