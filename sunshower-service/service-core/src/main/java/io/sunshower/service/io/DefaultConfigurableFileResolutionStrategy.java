package io.sunshower.service.io;

import io.sunshower.common.Identifier;
import io.sunshower.model.core.auth.Credential;
import io.sunshower.model.core.auth.Tenant;
import io.sunshower.model.core.auth.User;
import io.sunshower.service.model.io.FileResolutionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * Created by haswell on 5/25/17.
 */
public class DefaultConfigurableFileResolutionStrategy implements FileResolutionStrategy {

    static final Logger logger = LoggerFactory.getLogger(DefaultConfigurableFileResolutionStrategy.class);

    @Value("${sunshower.storage.root:default}")
    private String storageRoot;



    @Override
    public File resolve(Tenant tenant, User user, Credential credential) {
        if(storageRoot == null || "default".equals(storageRoot)) {
            return configureDefault(tenant, user, credential);
        }
        logger.info("Storage root set to '{}'", storageRoot);
        final File file = new File(storageRoot, String.format("storage/%s/workspace", user.getId()));
        if(!file.exists()) {
            logger.info("File {} does not exist.  Attempting to create it", file.getAbsolutePath());
            if(!file.mkdirs()) {
                throw new IllegalStateException(String.format("Attempt to " +
                        "create '%s' failed.  " +
                        "I cannot continue",
                        file.getAbsolutePath())
                );
            }
        }
        return file;
    }

    private File configureDefault(Tenant tenant, User user, Credential credential) {
        logger.info("Configuration parameter 'sunshower.storage.root' not set.  checking HASLI_HOME...");
        String root = System.getenv("HASLI_HOME");
        logger.info("HASLI_HOME: {}", root == null || root.trim().equals("") ? "<unset>" : root);
        if(root != null) {
            File f = new File(root);
            if(f.exists() && f.isDirectory()) {
                logger.info("Configuring storage from HASLI_HOME at: {}", f);
                return f;
            }
        }

        String property = System.getProperty("user.dir");
        logger.info("Failed to configure from HASLI_HOME.  " +
                "Either the value did not exist or was not a directory.  " +
                "Trying in user's directory: {}", property
        );

        final File file = new File(property,
                String.format(
                "sunshower/storage/%s/workspace",
                user.getId())
        );
        logger.info("Attempting to use: {}", file.getAbsolutePath());
        if(!file.exists()) {
            logger.info("File does not exist.  Attempting to create...");
            if(file.mkdirs()) {
                logger.info("Successfully created {}", file.getAbsolutePath());
            } else {
                throw new IllegalStateException("Attempts to resolve default " +
                        "storage directory failed.  Cannot create user " +
                        "storage and so cannot continue" +
                        "");
            }
        }
        return file;


    }
}
