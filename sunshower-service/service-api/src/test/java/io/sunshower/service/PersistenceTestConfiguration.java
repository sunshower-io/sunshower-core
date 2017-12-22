package io.sunshower.service;

import io.sunshower.persistence.Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haswell on 5/9/17.
 */
@Configuration
public class PersistenceTestConfiguration {

    @Bean
    public Dialect databaseDialect() {
        return Dialect.Postgres;
    }
}
