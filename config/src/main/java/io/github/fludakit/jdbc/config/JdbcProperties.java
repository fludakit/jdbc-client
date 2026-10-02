package io.github.fludakit.jdbc.config;

import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.Dependent;

/**
 * Holds the {@code fluda.jdbc.*} MicroProfile Config properties for JDBC client configuration.
 */
@Dependent
@ConfigProperties(prefix = "fluda.jdbc")
public class JdbcProperties {

    @ConfigProperty(name = "placeholder", defaultValue = "?")
    private String placeholder;

    @ConfigProperty(name = "query-timeout", defaultValue = "0")
    private int queryTimeout;

    @ConfigProperty(name = "fetch-size", defaultValue = "0")
    private int fetchSize;

    public String placeholder() {
        return placeholder;
    }

    public int queryTimeout() {
        return queryTimeout;
    }

    public int fetchSize() {
        return fetchSize;
    }
}
