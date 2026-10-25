package io.github.fludakit.jdbc.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConverterRegistryTest {

    public record Email(String value) {
    }

    /** A class-based converter: generics resolvable from the interface. */
    public static class StringToEmail implements Converter<String, Email> {
        @Override
        public Email convert(String source) {
            return new Email(source);
        }
    }

    @Test
    void explicitTypeRegistrationAndLookup() {
        ConverterRegistry registry = new ConverterRegistry()
                .register(String.class, Email.class, new StringToEmail());

        assertEquals(new Email("a@b.c"), registry.getConverter(String.class, Email.class).convert("a@b.c"));
        assertNull(registry.getConverter(Email.class, String.class));
        assertNull(registry.getConverter(Number.class, Email.class));
    }

    @Test
    void genericInterfaceResolution() {
        ConverterRegistry registry = new ConverterRegistry().register(new StringToEmail());

        assertEquals(new Email("x@y.z"), registry.getConverter(String.class, Email.class).convert("x@y.z"));
    }

    @Test
    void laterRegistrationReplacesEarlier() {
        ConverterRegistry registry = new ConverterRegistry()
                .register(String.class, Email.class, s -> new Email("first:" + s))
                .register(String.class, Email.class, s -> new Email("second:" + s));

        assertEquals(new Email("second:dup"), registry.getConverter(String.class, Email.class).convert("dup"));
    }

    @Test
    void lambdaRegistrationFailsLoudly() {
        ConverterRegistry registry = new ConverterRegistry();
        // a lambda's generics are erased - the registry cannot know its types, and silently
        // dropping it (the old behavior) surfaced much later as missing conversions
        Converter<String, Email> lambda = s -> new Email(s);

        assertThrows(IllegalArgumentException.class, () -> registry.register(lambda));
    }

    @Test
    void nullArgumentsAreRejected() {
        ConverterRegistry registry = new ConverterRegistry();

        assertThrows(NullPointerException.class, () -> registry.register(null, Email.class, new StringToEmail()));
        assertThrows(NullPointerException.class, () -> registry.register(String.class, null, new StringToEmail()));
        assertThrows(NullPointerException.class, () -> registry.register(String.class, Email.class, null));
        assertThrows(NullPointerException.class, () -> registry.register((Converter<?, ?>) null));
    }
}
