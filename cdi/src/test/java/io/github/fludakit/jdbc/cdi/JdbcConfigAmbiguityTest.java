package io.github.fludakit.jdbc.cdi;

import io.github.fludakit.jdbc.JdbcClient;
import io.github.fludakit.jdbc.JdbcConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The cdi <-> config seam: when more than one {@link JdbcConfig} bean is resolvable (a user bean
 * alongside the config module's produced bean), {@code Instance.isResolvable()} is false and
 * {@link JdbcClientProducer} silently falls back to {@link JdbcConfig#DEFAULT}. This test pins
 * that documented behavior: the DEFAULT {@code ?} placeholder is used even though custom beans
 * (with {@code @} and {@code :} placeholders) are present - a named query against H2 succeeds,
 * which it would not under the custom placeholders.
 */
@ExtendWith(WeldJunit5Extension.class)
class JdbcConfigAmbiguityTest {

    @WeldSetup
    WeldInitiator setup = WeldInitiator
            .from(JdbcClientProducer.class, ConverterRegistryProducer.class, TestDataSourceProducer.class,
                    AmbiguousConfigProducer.class)
            .build();

    @Inject
    JdbcClient client;

    @BeforeEach
    void createProbeTable() throws Exception {
        try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS probe");
            stmt.execute("CREATE TABLE probe (id INT PRIMARY KEY)");
            stmt.execute("INSERT INTO probe VALUES (1)");
        }
    }

    @Inject
    javax.sql.DataSource dataSource;

    @Test
    void ambiguousConfigBeansFallBackToDefault() {
        // under the custom '@' or ':' placeholders the named parameter would survive rewriting
        // and H2 would reject the statement; success proves DEFAULT ('?') was used
        Integer one = client.sql("SELECT id FROM probe WHERE id = :one").param("one", 1)
                .singleValue(Integer.class);

        assertEquals(1, one);
    }

    @ApplicationScoped
    public static class AmbiguousConfigProducer {

        @Produces
        @ApplicationScoped
        public JdbcConfig customAtConfig() {
            return new JdbcConfig("@", 10, 10);
        }

        @Produces
        @ApplicationScoped
        public JdbcConfig otherColonConfig() {
            return new JdbcConfig(":", 5, 5);
        }
    }
}
