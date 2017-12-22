package io.sunshower.service.revision.model;

import io.sunshower.common.io.Files;
import io.sunshower.model.core.io.File;

import java.io.IOException;

/**
 * Created by haswell on 5/30/17.
 */
public class Repositories {

    public static boolean delete(Repository repository) {

        final Local local = repository.getLocal();
        if (local == null) {
            return false;
        }
        File file = local.getFile();
        if (file == null) {
            return false;
        }
        String path = file.getPath();
        if (path == null) {
            return false;
        }

        final java.io.File physicalFile = new java.io.File(path);
        try {
            Files.delete(physicalFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
