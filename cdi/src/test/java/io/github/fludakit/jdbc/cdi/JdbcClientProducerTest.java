package io.github.fludakit.jdbc.cdi;

import io.github.fludakit.jdbc.JdbcClient;
import io.github.fludakit.jdbc.converter.ConverterRegistry;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.sql.DataSource;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests CDI producers for JdbcClient and ConverterRegistry.
 * Uses {@link WeldInitiator} to explicitly declare all beans.
 */
@ExtendWith(WeldJunit5Extension.class)
class JdbcClientProducerTest {

    @WeldSetup
    WeldInitiator setup = WeldInitiator
            .from(JdbcClientProducer.class, ConverterRegistryProducer.class, TestDataSourceProducer.class)
            .build();

    @Inject
    JdbcClient client;

    @Inject
    ConverterRegistry registry;

    @Inject
    DataSource dataSource;

    @Test
    void beansExist() {
        assertNotNull(client);
        assertNotNull(registry);
        assertNotNull(dataSource);
    }

    @Test
    void connectionIsUsable() {
        assertEquals(1, client.sql("SELECT 1").singleValue(Integer.class));
    }
}
