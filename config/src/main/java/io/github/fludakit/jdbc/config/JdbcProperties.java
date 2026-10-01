package io.github.fludakit.jdbc.config;

import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Holds the {@code fluda.jdbc.*} MicroProfile Config properties for JDBC client configuration.
 */
@ApplicationScoped
@ConfigProperties(prefix = "fluda.jdbc")
public class JdbcProperties {

    private String placeholder = "?";

    @ConfigProperty(name = "query-timeout")
    private int queryTimeout = 0;

    @ConfigProperty(name = "fetch-size")
    private int fetchSize = 0;

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
