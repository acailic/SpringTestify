package io.github.springtestify.generator;

import com.github.javafaker.Faker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generic entity generator for creating test data.
 * Provides utilities for generating single entities, lists, and pages of entities.
 */
public class EntityGenerator {
    private static final Map<String, Faker> FAKER_CACHE = new ConcurrentHashMap<>();
    private final Map<Class<?>, Supplier<?>> generators = new HashMap<>();

    private String defaultLocale = "en";
    private int defaultBatchSize = 10;
    private int maxBatchSize = 100;
    private boolean enableFakerCache = true;

    public EntityGenerator() {
        registerDefaultGenerators();
    }

    private void registerDefaultGenerators() {
        // Register primitive type generators
        generators.put(String.class, () -> getFaker().lorem().word());
        generators.put(Integer.class, () -> getFaker().number().numberBetween(1, 1000));
        generators.put(Long.class, () -> getFaker().number().numberBetween(1L, 1000L));
        generators.put(Double.class, () -> getFaker().number().randomDouble(2, 1, 1000));
        generators.put(Boolean.class, () -> getFaker().bool().bool());
        generators.put(Date.class, () -> getFaker().date().past(365, java.util.concurrent.TimeUnit.DAYS));
    }

    private Faker getFaker() {
        if (enableFakerCache) {
            return FAKER_CACHE.computeIfAbsent(defaultLocale, locale -> new Faker(new Locale(locale)));
        }
        return new Faker(new Locale(defaultLocale));
    }

    /**
     * Register a custom generator for a specific type
     */
    public <T> void registerGenerator(Class<T> type, Supplier<T> generator) {
        generators.put(type, generator);
    }

    /**
     * Generate a single entity using a custom generator function
     */
    public <T> T generateEntity(Class<T> entityClass, Function<Integer, T> generator) {
        return generator.apply(1);
    }

    /**
     * Generate a list of entities using a custom generator function
     */
    public <T> List<T> generateEntities(Class<T> entityClass, int count, Function<Integer, T> generator) {
        int actualCount = Math.min(count, maxBatchSize);
        return IntStream.range(0, actualCount)
                .mapToObj(generator::apply)
                .collect(Collectors.toList());
    }

    /**
     * Generate a page of entities using a custom generator function
     */
    public <T> Page<T> generatePage(Class<T> entityClass, Pageable pageable, int totalElements, Function<Integer, T> generator) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalElements);

        List<T> content = IntStream.range(start, end)
                .mapToObj(generator::apply)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalElements);
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public void setDefaultBatchSize(int defaultBatchSize) {
        this.defaultBatchSize = defaultBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public void setEnableFakerCache(boolean enableFakerCache) {
        this.enableFakerCache = enableFakerCache;
    }

    // Common data generators
    public static class CommonGenerators {
        public static String email() {
            return new Faker().internet().emailAddress();
        }

        public static String name() {
            return new Faker().name().fullName();
        }

        public static String username() {
            return new Faker().name().username();
        }

        public static String phoneNumber() {
            return new Faker().phoneNumber().cellPhone();
        }

        public static String companyName() {
            return new Faker().company().name();
        }

        public static String text(int minLength, int maxLength) {
            return new Faker().lorem().characters(minLength, maxLength);
        }

        public static UUID uuid() {
            return UUID.randomUUID();
        }

        public static <E extends Enum<?>> E randomEnum(Class<E> enumClass) {
            E[] values = enumClass.getEnumConstants();
            return values[new Random().nextInt(values.length)];
        }
    }
}
