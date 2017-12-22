package io.sunshower.service;

import io.sunshower.persistence.Dialect;
import io.sunshower.persistence.annotations.CacheMode;
import io.sunshower.service.git.MockRepositoryResolutionStrategy;
import io.sunshower.service.model.io.FileResolutionStrategy;
import io.sunshower.service.security.Session;
import io.sunshower.service.serialization.DynamicJaxrsProviders;
import io.sunshower.service.serialization.DynamicResolvingMoxyJsonProvider;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by haswell on 2/17/17.
 */
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        jsr250Enabled = true
)
@Configuration
@CacheMode(CacheMode.Mode.Local)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TestConfiguration {
    

    @Bean
    @Primary
    public FileResolutionStrategy defaultFileResolutionStrategy() {
        return new MockRepositoryResolutionStrategy();
    }

    @Bean
    public DynamicResolvingMoxyJsonProvider moXyJsonProvider(DynamicJaxrsProviders providers) {
        return new DynamicResolvingMoxyJsonProvider(providers);
    }


    @Bean
    public Dialect databaseDialect() {
        return Dialect.Postgres;
    }





    @Bean
    public ExecutorService executorService() {
        return new ForkJoinPool();
    }

    @Bean
    public Session userFacade() {
        return new Session();
    }

}
