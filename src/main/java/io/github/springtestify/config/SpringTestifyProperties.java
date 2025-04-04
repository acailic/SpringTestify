package io.github.springtestify.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.testify")
public class SpringTestifyProperties {
    private boolean enableAutoConfiguration = true;
    private boolean enableSmartContextCaching = true;
    private String defaultLocale = "en";
    private TestConfiguration test = new TestConfiguration();
    private MockConfiguration mock = new MockConfiguration();

    public static class TestConfiguration {
        private boolean enableAutoMockMvc = true;
        private boolean enableAutoObjectMapper = true;
        private boolean enableMethodOrdering = true;
        private String methodOrderer = "org.junit.jupiter.api.MethodOrderer$OrderAnnotation";

        public boolean isEnableAutoMockMvc() {
            return enableAutoMockMvc;
        }

        public void setEnableAutoMockMvc(boolean enableAutoMockMvc) {
            this.enableAutoMockMvc = enableAutoMockMvc;
        }

        public boolean isEnableAutoObjectMapper() {
            return enableAutoObjectMapper;
        }

        public void setEnableAutoObjectMapper(boolean enableAutoObjectMapper) {
            this.enableAutoObjectMapper = enableAutoObjectMapper;
        }

        public boolean isEnableMethodOrdering() {
            return enableMethodOrdering;
        }

        public void setEnableMethodOrdering(boolean enableMethodOrdering) {
            this.enableMethodOrdering = enableMethodOrdering;
        }

        public String getMethodOrderer() {
            return methodOrderer;
        }

        public void setMethodOrderer(String methodOrderer) {
            this.methodOrderer = methodOrderer;
        }
    }

    public static class MockConfiguration {
        private boolean resetBeforeEachTest = true;
        private boolean strictMode = true;

        public boolean isResetBeforeEachTest() {
            return resetBeforeEachTest;
        }

        public void setResetBeforeEachTest(boolean resetBeforeEachTest) {
            this.resetBeforeEachTest = resetBeforeEachTest;
        }

        public boolean isStrictMode() {
            return strictMode;
        }

        public void setStrictMode(boolean strictMode) {
            this.strictMode = strictMode;
        }
    }

    public boolean isEnableAutoConfiguration() {
        return enableAutoConfiguration;
    }

    public void setEnableAutoConfiguration(boolean enableAutoConfiguration) {
        this.enableAutoConfiguration = enableAutoConfiguration;
    }

    public boolean isEnableSmartContextCaching() {
        return enableSmartContextCaching;
    }

    public void setEnableSmartContextCaching(boolean enableSmartContextCaching) {
        this.enableSmartContextCaching = enableSmartContextCaching;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public TestConfiguration getTest() {
        return test;
    }

    public void setTest(TestConfiguration test) {
        this.test = test;
    }

    public MockConfiguration getMock() {
        return mock;
    }

    public void setMock(MockConfiguration mock) {
        this.mock = mock;
    }
}
