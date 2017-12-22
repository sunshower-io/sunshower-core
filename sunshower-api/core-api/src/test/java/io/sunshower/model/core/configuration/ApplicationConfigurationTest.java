package io.sunshower.model.core.configuration;


import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

/**
 * Created by haswell on 10/26/16.
 */
@RunWith(JUnitPlatform.class)
public class ApplicationConfigurationTest extends SerializationTestCase {

    public ApplicationConfigurationTest() {
        super(SerializationAware.Format.JSON, ApplicationConfiguration.class);
    }

    @Test
    public void ensureWhatever() {
        
    }
    
}