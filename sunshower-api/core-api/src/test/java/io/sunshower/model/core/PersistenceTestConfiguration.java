package io.sunshower.model.core;

import io.sunshower.persistence.Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haswell on 5/3/17.
 */
@Configuration
public class PersistenceTestConfiguration {

    @Bean
    public Dialect databaseDialect() {
        return Dialect.Postgres;
    }
}
