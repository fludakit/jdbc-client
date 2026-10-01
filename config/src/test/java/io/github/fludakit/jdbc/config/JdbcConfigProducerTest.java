package io.github.fludakit.jdbc.config;

import io.github.fludakit.jdbc.JdbcConfig;
import io.smallrye.config.inject.ConfigExtension;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({WeldJunit5Extension.class})
class JdbcConfigProducerTest {

    @WeldSetup
    WeldInitiator weld = WeldInitiator.from(
                    // extensions
                    ConfigExtension.class,

                    // bean classes
                    JdbcConfigProducer.class, JdbcProperties.class
            )
            .build();

    @Inject
    JdbcConfig config;

    @Test
    void mapsConfigProperties() {
        assertNotNull(config);
        assertEquals("$", config.placeholder());
        assertEquals(30, config.queryTimeout());
        assertEquals(100, config.fetchSize());
    }
}
