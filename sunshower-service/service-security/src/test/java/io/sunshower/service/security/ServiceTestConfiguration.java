package io.sunshower.service.security;

import io.sunshower.persistence.Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wabbus on 5/9/17.
 */
@Configuration
public class ServiceTestConfiguration {

    @Bean
    public Dialect databaseDialect() {
        return Dialect.Postgres;
    }
}
