package io.github.springtestify.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@EnableConfigurationProperties(SpringTestifyProperties.class)
@ConditionalOnProperty(prefix = "spring.testify", name = "enable-auto-configuration", havingValue = "true", matchIfMissing = true)
public class SpringTestifyAutoConfiguration {

    private final SpringTestifyProperties properties;

    public SpringTestifyAutoConfiguration(SpringTestifyProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.testify.test", name = "enable-auto-mock-mvc", havingValue = "true", matchIfMissing = true)
    public MockMvc mockMvc(WebApplicationContext webApplicationContext) {
        return MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.testify.test", name = "enable-auto-object-mapper", havingValue = "true", matchIfMissing = true)
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
