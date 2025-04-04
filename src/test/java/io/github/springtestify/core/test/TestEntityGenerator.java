package io.github.springtestify.core.test;

import com.github.javafaker.Faker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestEntityGenerator {
    private static final Faker faker = new Faker();

    public static <T> T generateEntity(Class<T> entityClass, Function<Integer, T> generator) {
        return generator.apply(1);
    }

    public static <T> List<T> generateEntities(Class<T> entityClass, int count, Function<Integer, T> generator) {
        return IntStream.range(0, count)
                .mapToObj(generator::apply)
                .collect(Collectors.toList());
    }

    public static <T> Page<T> generatePage(Class<T> entityClass, Pageable pageable, int totalElements, Function<Integer, T> generator) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalElements);

        List<T> content = IntStream.range(start, end)
                .mapToObj(generator::apply)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalElements);
    }

    public static String randomEmail() {
        return faker.internet().emailAddress();
    }

    public static String randomName() {
        return faker.name().fullName();
    }

    public static String randomUsername() {
        return faker.name().username();
    }

    public static String randomPhoneNumber() {
        return faker.phoneNumber().cellPhone();
    }

    public static String randomCompanyName() {
        return faker.company().name();
    }

    public static String randomText(int minLength, int maxLength) {
        return faker.lorem().characters(minLength, maxLength);
    }
}
