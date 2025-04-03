package io.github.springtestify.all;

import io.github.springtestify.data.config.TestDataGenerationConfig;
import io.github.springtestify.db.config.InMemoryDatabaseConfig;
import io.github.springtestify.service.config.ServiceTestContextCustomizerFactory;
import io.github.springtestify.web.config.WebTestConfig;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for SpringTestify that imports all module configurations.
 * <p>
 * This configuration is automatically applied when using the {@code @SpringTestify}
 * annotation and including the springtestify-all dependency.
 */
@Import({
    InMemoryDatabaseConfig.class,
    WebTestConfig.class,
    TestDataGenerationConfig.class
})
public class SpringTestifyAutoConfiguration {
    // This class serves as a container for importing all module configurations
    // No additional beans are defined here
}