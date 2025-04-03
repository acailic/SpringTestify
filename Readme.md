# SpringTestify

[![Maven Central](https://img.shields.io/maven-central/v/io.github.acailic/SpringTestify.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.acailic%22%20AND%20a:%22SpringTestify%22)
[![License](https://img.shields.io/github/license/acailic/SpringTestify)](LICENSE)

SpringTestify is a powerful testing library that simplifies Spring Boot testing with declarative annotations, in-memory database support, and test data generation.

## Features

- ✨ **Simplified testing** - Reduce boilerplate code with custom annotations
- 💾 **In-memory database** - One-click setup for H2, HSQLDB, Derby
- 🍃 **MongoDB support** - Embedded MongoDB for document-based testing
- 🌐 **Controller testing** - Streamlined REST API testing
- 🔧 **Service layer testing** - Automatic mocking for dependencies
- 📊 **Test data generation** - Generate data with real-world characteristics
- 🏗️ **Modular design** - Use only what you need
- ⚡ **Smart context caching** - Powered by [spring-test-smart-context](https://github.com/seregamorph/spring-test-smart-context) for optimized test execution

## Installation

### Maven

```xml
<!-- All modules - single dependency -->
<dependency>
    <groupId>io.github.acailic</groupId>
    <artifactId>springtestify-all</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>

<!-- Or include individual modules -->
<dependency>
    <groupId>io.github.acailic</groupId>
    <artifactId>springtestify-core</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```groovy
// All modules - single dependency
testImplementation 'io.github.acailic:springtestify-all:1.0-SNAPSHOT'

// Or include individual modules
testImplementation 'io.github.acailic:springtestify-core:1.0-SNAPSHOT'
```

## Quick Start

SpringTestify makes testing Spring applications much simpler:

```java
@SpringTestify
@InMemoryDb(type = DbType.H2)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldCreateUser() {
        User user = new User("john.doe@example.com", "John Doe");
        User createdUser = userService.createUser(user);

        assertThat(createdUser.getId()).isNotNull();
    }
}
```

## Core Modules

### 1. Core Module (`springtestify-core`)

Contains fundamental annotations:

- `@SpringTestify` - Main annotation that enables SpringTestify features
- `@InMemoryDb` - Configures in-memory database
- `@ControllerTest` - Simplifies controller testing
- `@ServiceTest` - Simplifies service testing
- `@RepositoryTest` - Simplifies repository testing
- `@DataSetup` - Loads data from files
- `@GenerateTestData` - Generates test data automatically
- `@PerformanceTest` - Validates performance requirements

### 2. Database Module (`springtestify-db`)

Provides in-memory database support with:

- Multiple database types (H2, HSQLDB, Derby)
- MongoDB support with embedded MongoDB
- Automatic schema migration
- SQL script execution
- Data loading from JSON, CSV, etc.

```java
@SpringTestify
@InMemoryDb(type = DbType.POSTGRES_COMPATIBLE, migrate = true)
@DataSetup(value = "initial-data.sql")
class OrderRepositoryTest {
    // Test with PostgreSQL-compatible in-memory database
}
```

#### MongoDB Support

Easily test MongoDB applications with embedded MongoDB:

```java
@SpringTestify
@InMemoryDb(type = DbType.MONGODB)
class ProductRepositoryTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldStoreAndRetrieveProduct() {
        // Test with embedded MongoDB
    }
}
```

### 3. Web Module (`springtestify-web`)

Makes testing REST controllers easy:

```java
@ControllerTest(path = "/api/users")
class UserControllerTest extends AbstractControllerTest {

    @Test
    void shouldGetUsersList() throws Exception {
        assertThat(performGet("/"))
            .isSuccessful()
            .hasJsonPath("$[0].email");
    }
}
```

### 4. Service Module (`springtestify-service`)

Provides tools for testing service layer with automatic mock creation:

```java
@ServiceTest(UserService.class)
class UserServiceTest extends AbstractServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // Automatically mocked

    @Test
    void shouldFindUserById() {
        // Given
        String userId = "1";
        User user = new User(userId, "test@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        User foundUser = userService.getUserById(userId);

        // Then
        assertThat(foundUser).isEqualTo(user);
        verifyMock(UserRepository.class).findById(userId);
    }
}
```

### 5. Data Module (`springtestify-data`)

Generates realistic test data:

```java
@SpringTestify
@GenerateTestData(entity = User.class, count = 10, properties = {"role=ADMIN:2,USER:8"})
class UserServiceIntegrationTest {

    @Autowired
    private TestDataRegistry testData;

    @Test
    void shouldFindAdminUsers() {
        // Access the generated data
        List<User> admins = testData.findAll(User.class,
            user -> "ADMIN".equals(user.getRole()));

        assertThat(admins).hasSize(2);
    }
}
```

## Advanced Features

### Property Builder for Test Data

Use the fluent API for more complex data generation:

```java
@GenerateTestData(
    entity = Product.class,
    count = 20,
    properties = {
        PropertyValueBuilder.property("category")
            .value("ELECTRONICS").count(10)
            .value("BOOKS").count(5)
            .value("CLOTHING").count(5)
            .build(),
        PropertyValueBuilder.property("price").range(9.99, 199.99).build()
    }
)
```

### Combining Annotations

Annotations can be combined for powerful testing capabilities:

```java
@SpringTestify
@InMemoryDb(type = DbType.H2)
@GenerateTestData(entity = User.class, count = 5)
@GenerateTestData(entity = Order.class, count = 10)
@PerformanceTest(threshold = "100ms")
class ComplexIntegrationTest {
    // Your tests with in-memory database and generated data
}
```

### Working with MongoDB

For MongoDB applications, SpringTestify provides specialized support:

```java
@SpringTestify
@InMemoryDb(type = DbType.MONGODB)
@GenerateTestData(
    entity = Product.class,
    count = 15,
    properties = {
        PropertyValueBuilder.property("category")
            .value("ELECTRONICS").count(5)
            .value("BOOKS").count(5)
            .value("CLOTHING").count(5)
            .build()
    }
)
class MongoDBIntegrationTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldQueryProductsByCategory() {
        // Test MongoDB query methods with pre-generated data
    }
}
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
