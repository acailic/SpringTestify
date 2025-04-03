package io.github.springtestify.examples.mongodb;

import io.github.springtestify.core.annotation.GenerateTestData;
import io.github.springtestify.core.annotation.InMemoryDb;
import io.github.springtestify.core.annotation.PerformanceTest;
import io.github.springtestify.core.annotation.SpringTestify;
import io.github.springtestify.core.enums.DbType;
import io.github.springtestify.data.util.TestDataRegistry;
import io.github.springtestify.examples.document.Product;
import io.github.springtestify.examples.document.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringTestify
@InMemoryDb(type = DbType.MONGODB)
@GenerateTestData(
    entity = Product.class,
    count = 100,
    properties = {
        "category=ELECTRONICS:40,BOOKS:30,CLOTHING:30",
        "price=9.99:20,19.99:30,49.99:30,99.99:20",
        "inStock=true:80,false:20"
    }
)
public class MongoPerformanceTest {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private TestDataRegistry testData;
    
    @BeforeEach
    void setupIndices() {
        // Create indices for performance testing
        mongoTemplate.indexOps(Product.class)
                .ensureIndex(new Index().on("category", Sort.Direction.ASC));
        mongoTemplate.indexOps(Product.class)
                .ensureIndex(new Index().on("price", Sort.Direction.ASC));
        mongoTemplate.indexOps(Product.class)
                .ensureIndex(new Index().on("in_stock", Sort.Direction.ASC));
    }
    
    @Test
    @PerformanceTest(threshold = "50ms")
    void shouldPerformFastQueriesWithIndices() {
        // When
        List<Product> products = productRepository.findByCategory("ELECTRONICS");
        
        // Then
        assertThat(products).hasSize(40);
    }
    
    @Test
    @PerformanceTest(threshold = "150ms")
    void shouldPaginateResults() {
        // When
        Page<Product> firstPage = productRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "price")));
        
        // Then
        assertThat(firstPage.getContent()).hasSize(10);
        assertThat(firstPage.getTotalElements()).isEqualTo(100);
        assertThat(firstPage.getTotalPages()).isEqualTo(10);
    }
    
    @Test
    @PerformanceTest(threshold = "500ms")
    void shouldHandleBulkOperations() {
        // Given
        List<Product> productsToUpdate = testData.findAll(Product.class, 
                p -> "ELECTRONICS".equals(p.getCategory()));
        
        // When - Apply price increase to all electronics
        for (Product product : productsToUpdate) {
            BigDecimal currentPrice = product.getPrice();
            product.setPrice(currentPrice.multiply(new BigDecimal("1.10"))); // 10% price increase
            product.addTag("price-updated");
        }
        
        // Then - Save all and verify performance
        List<Product> savedProducts = productRepository.saveAll(productsToUpdate);
        assertThat(savedProducts).hasSize(40);
        
        // Verify changes
        List<Product> updatedProducts = productRepository.findByTagName("price-updated");
        assertThat(updatedProducts).hasSize(40);
    }
    
    @Test
    @PerformanceTest(threshold = "800ms")
    void shouldPerformComplexAggregationQueries() {
        // Given
        mongoTemplate.indexOps(Product.class)
                .ensureIndex(new Index().on("tags", Sort.Direction.ASC));
        
        List<String> tags = List.of("premium", "featured", "sale", "new-arrival", "best-seller");
        
        // Add random tags to products
        List<Product> allProducts = productRepository.findAll();
        for (int i = 0; i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            
            // Add 1-3 random tags
            int tagCount = (i % 3) + 1;
            for (int j = 0; j < tagCount; j++) {
                String tag = tags.get((i + j) % tags.size());
                product.addTag(tag);
            }
        }
        productRepository.saveAll(allProducts);
        
        // When - Execute multiple tag queries in sequence
        long startTime = System.nanoTime();
        
        List<Product> results = new ArrayList<>();
        for (String tag : tags) {
            List<Product> taggedProducts = productRepository.findByTagName(tag);
            results.addAll(taggedProducts);
        }
        
        // Then
        long executionTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        System.out.println("Complex query execution time: " + executionTime + " ms");
        
        assertThat(results).isNotEmpty();
        assertThat(executionTime).isLessThan(800); // Should complete under our threshold
    }
    
    @Test
    void shouldQueryWithMongoTemplate() {
        // Given
        Query query = new Query(Criteria.where("price").lt(new BigDecimal("50.00"))
                .and("in_stock").is(true)
                .and("category").is("ELECTRONICS"));
        
        // When
        List<Product> affordableElectronics = mongoTemplate.find(query, Product.class);
        
        // Then
        assertThat(affordableElectronics).isNotEmpty();
        assertThat(affordableElectronics).allMatch(
                p -> p.getPrice().compareTo(new BigDecimal("50.00")) < 0 &&
                     p.isInStock() &&
                     "ELECTRONICS".equals(p.getCategory())
        );
    }
}