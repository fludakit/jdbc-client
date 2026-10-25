package io.github.fludakit.jdbc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The no-arg constructor exists for CDI client proxies; a client created through it and never
 * initialized must fail fast at the first operation instead of NPE-ing deep inside SQL parsing.
 */
class JdbcClientInitializationTest {

    @Test
    void uninitializedClientFailsFastWithGuidance() {
        JdbcClient uninitialized = new JdbcClient();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> uninitialized.sql("SELECT 1"));

        assertTrue(exception.getMessage().contains("JdbcClient(DataSource)"),
                "the message must point at the initializing constructors");
    }
}
