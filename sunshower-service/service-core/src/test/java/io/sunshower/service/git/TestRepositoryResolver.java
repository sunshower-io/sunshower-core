package io.sunshower.service.git;

import io.sunshower.model.core.auth.Credential;
import io.sunshower.model.core.auth.Tenant;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.model.io.FileResolutionStrategy;
import java.io.File;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

/** Created by haswell on 5/22/17. */
public class TestRepositoryResolver implements FileResolutionStrategy {

  @Override
  public File resolve(Tenant tenant, User user, Credential credential) {
    final String path = ClassLoader.getSystemResource(".").getFile();
    File current = Paths.get(path).toFile();
    while (current != null) {
      File[] builds =
          current.listFiles(
              t -> (t.getName().equals("build") || t.getName().equals("out")) && t.isDirectory());
      if (builds != null && builds.length > 0) {
        return builds[0];
      } else {
        current = current.getParentFile();
      }
    }
    throw new NoSuchElementException("Could not find file");
  }
}
