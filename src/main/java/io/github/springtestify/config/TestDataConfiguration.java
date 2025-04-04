package io.github.springtestify.config;

import io.github.springtestify.generator.EntityGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.testify.data")
public class TestDataConfiguration {

    private GeneratorConfig generator = new GeneratorConfig();
    private DatabaseConfig database = new DatabaseConfig();

    public static class GeneratorConfig {
        private int defaultBatchSize = 10;
        private int maxBatchSize = 100;
        private String defaultLocale = "en";
        private boolean enableFakerCache = true;

        public int getDefaultBatchSize() {
            return defaultBatchSize;
        }

        public void setDefaultBatchSize(int defaultBatchSize) {
            this.defaultBatchSize = defaultBatchSize;
        }

        public int getMaxBatchSize() {
            return maxBatchSize;
        }

        public void setMaxBatchSize(int maxBatchSize) {
            this.maxBatchSize = maxBatchSize;
        }

        public String getDefaultLocale() {
            return defaultLocale;
        }

        public void setDefaultLocale(String defaultLocale) {
            this.defaultLocale = defaultLocale;
        }

        public boolean isEnableFakerCache() {
            return enableFakerCache;
        }

        public void setEnableFakerCache(boolean enableFakerCache) {
            this.enableFakerCache = enableFakerCache;
        }
    }

    public static class DatabaseConfig {
        private boolean cleanupAfterTest = true;
        private boolean truncateBeforeTest = true;
        private String[] excludedTables = new String[0];

        public boolean isCleanupAfterTest() {
            return cleanupAfterTest;
        }

        public void setCleanupAfterTest(boolean cleanupAfterTest) {
            this.cleanupAfterTest = cleanupAfterTest;
        }

        public boolean isTruncateBeforeTest() {
            return truncateBeforeTest;
        }

        public void setTruncateBeforeTest(boolean truncateBeforeTest) {
            this.truncateBeforeTest = truncateBeforeTest;
        }

        public String[] getExcludedTables() {
            return excludedTables;
        }

        public void setExcludedTables(String[] excludedTables) {
            this.excludedTables = excludedTables;
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public EntityGenerator entityGenerator() {
        EntityGenerator generator = new EntityGenerator();
        generator.setDefaultLocale(this.generator.getDefaultLocale());
        generator.setDefaultBatchSize(this.generator.getDefaultBatchSize());
        generator.setMaxBatchSize(this.generator.getMaxBatchSize());
        generator.setEnableFakerCache(this.generator.isEnableFakerCache());
        return generator;
    }

    public GeneratorConfig getGenerator() {
        return generator;
    }

    public void setGenerator(GeneratorConfig generator) {
        this.generator = generator;
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }
}
