package io.github.springtestify.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation to specify Faker-based data generation for test entities.
 * Allows mapping of entity fields to Faker providers.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(FakerData.List.class)
public @interface FakerData {
  /**
   * The target entity class for which data will be generated.
   */
  Class<?> entity();

  /**
   * Number of entities to generate.
   */
  int count() default 1;

  /**
   * Field mappings in the format "fieldName=faker.provider.method"
   * Example: {"name=name.fullName", "email=internet.emailAddress"}
   */
  String[] fields() default {};

  /**
   * Optional locale for the Faker instance (e.g., "en", "fr", "de").
   */
  String locale() default "";

  /**
   * Container annotation for repeatable {@link FakerData}.
   */
  @Target({ ElementType.TYPE, ElementType.METHOD })
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @interface List {
    FakerData[] value();
  }
}
