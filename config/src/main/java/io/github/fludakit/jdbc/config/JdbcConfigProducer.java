package io.github.fludakit.jdbc.config;

import io.github.fludakit.jdbc.JdbcConfig;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperties;

/**
 * Produces the {@code @ApplicationScoped} {@link JdbcConfig} bean from the {@code fluda.jdbc.*}
 * MicroProfile Config properties. This is the optional integration point consumed by the {@code cdi}
 * module.
 */
@ApplicationScoped
public class JdbcConfigProducer {

    @Inject @ConfigProperties
    private JdbcProperties properties;

    @Produces
    @ApplicationScoped
    public JdbcConfig produce() {
        return new JdbcConfig(properties.placeholder(), properties.queryTimeout(), properties.fetchSize());
    }
}
