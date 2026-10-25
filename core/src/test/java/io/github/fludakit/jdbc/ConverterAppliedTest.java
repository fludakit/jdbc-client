package io.github.fludakit.jdbc;

import io.github.fludakit.jdbc.converter.Converter;
import io.github.fludakit.jdbc.converter.ConverterRegistry;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Proves the Converter SPI is actually consulted during result mapping: a registered converter
 * turns the VARCHAR column into the record's value-type component - without it the same mapping
 * fails, because the raw String cannot satisfy the component.
 */
class ConverterAppliedTest {

    public record Email(String value) {
    }

    public record Customer(String name, Email email) {
    }

    /** Class-based so the registry can resolve the generics. */
    public static class StringToEmail implements Converter<String, Email> {
        @Override
        public Email convert(String source) {
            return new Email(source.trim().toLowerCase());
        }
    }

    private static DataSource dataSource;
    private static JdbcClient convertingClient;
    private static JdbcClient plainClient;

    @BeforeAll
    static void createClients() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:conv;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        dataSource = ds;

        convertingClient = JdbcClient.builder(ds)
                .converters(new ConverterRegistry().register(new StringToEmail()))
                .build();
        plainClient = new JdbcClient(ds);
    }

    @AfterAll
    static void shutdown() throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SHUTDOWN");
        }
    }

    @BeforeEach
    void setupDatabase() throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS customers");
            stmt.execute("CREATE TABLE customers (name VARCHAR(255), email VARCHAR(255))");
            stmt.execute("INSERT INTO customers VALUES ('Duke', '  Duke@Example.COM  ')");
        }
    }

    @Test
    void registeredConverterIsAppliedDuringMapping() {
        List<Customer> all = convertingClient.sql("SELECT name, email FROM customers")
                .query(Customer.class).list();

        assertEquals(1, all.size());
        assertEquals(new Email("duke@example.com"), all.getFirst().email());
    }

    @Test
    void withoutRegistrationTheMappingFails() {
        // the fallback has no String->Email rule: the raw String cannot satisfy the component,
        // so mapping fails instead of silently surfacing the wrong type
        assertThrows(JdbcClientException.class, () ->
                plainClient.sql("SELECT name, email FROM customers").query(Customer.class).list());
    }

}
