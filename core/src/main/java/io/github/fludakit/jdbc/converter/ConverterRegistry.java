package io.github.fludakit.jdbc.converter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A plain (non-CDI) registry of {@link Converter} implementations. Register converters manually, or
 * (from the CDI layer) register discovered converter beans and let their generic type arguments be
 * detected automatically.
 */
public class ConverterRegistry {

    private final Map<ConversionKey, Converter<?, ?>> matrix = new HashMap<>();

    public <S, T> ConverterRegistry register(Class<S> sourceType, Class<T> targetType, Converter<S, T> converter) {
        Objects.requireNonNull(sourceType, "sourceType must not be null");
        Objects.requireNonNull(targetType, "targetType must not be null");
        Objects.requireNonNull(converter, "converter must not be null");
        matrix.put(new ConversionKey(sourceType, targetType), converter);
        return this;
    }

    /**
     * Registers a converter, resolving its source/target types from its {@code Converter<S, T>} generic interface.
     *
     * @throws IllegalArgumentException if the source/target types cannot be resolved — for example a
     *                                  lambda, whose generics are erased at runtime. Use
     *                                  {@link #register(Class, Class, Converter)} for those.
     */
    public ConverterRegistry register(Converter<?, ?> converter) {
        Objects.requireNonNull(converter, "converter must not be null");
        Type[] args = Converters.typeArguments(converter.getClass());
        if (args == null || args.length != 2) {
            throw new IllegalArgumentException(
                    "Cannot resolve the <S, T> type arguments of " + converter.getClass().getName()
                            + "; lambdas and type-erased implementations cannot be auto-detected."
                            + " Use register(Class<S>, Class<T>, Converter<S, T>) instead.");
        }
        Class<?> sourceType = Converters.getClassFromType(args[0]);
        Class<?> targetType = Converters.getClassFromType(args[1]);
        if (sourceType == null || targetType == null) {
            throw new IllegalArgumentException(
                    "Cannot resolve the <S, T> type arguments of " + converter.getClass().getName()
                            + " to concrete classes. Use register(Class<S>, Class<T>, Converter<S, T>) instead.");
        }
        matrix.put(new ConversionKey(sourceType, targetType), converter);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <S, T> Converter<S, T> getConverter(Class<S> source, Class<T> target) {
        return (Converter<S, T>) matrix.get(new ConversionKey(source, target));
    }

    public record ConversionKey(Class<?> sourceType, Class<?> targetType) {
    }
}
