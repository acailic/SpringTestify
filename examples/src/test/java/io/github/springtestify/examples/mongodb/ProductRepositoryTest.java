package io.github.springtestify.examples.mongodb;

import io.github.springtestify.core.annotation.InMemoryDb;
import io.github.springtestify.core.annotation.RepositoryTest;
import io.github.springtestify.core.enums.DbType;
import io.github.springtestify.examples.document.Product;
import io.github.springtestify.examples.document.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@InMemoryDb(type = DbType.MONGODB)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        // Clear the collection before each test
        mongoTemplate.dropCollection(Product.class);
    }

    @Test
    void shouldSaveAndRetrieveProduct() {
        // Given
        Product product = new Product(
                "Wireless Headphones",
                "High-quality wireless headphones with noise cancellation",
                new BigDecimal("149.99"),
                "ELECTRONICS"
        );
        
        // When
        Product savedProduct = productRepository.save(product);
        
        // Then
        assertThat(savedProduct.getId()).isNotNull();
        
        Optional<Product> retrievedProduct = productRepository.findById(savedProduct.getId());
        assertThat(retrievedProduct).isPresent();
        assertThat(retrievedProduct.get().getName()).isEqualTo("Wireless Headphones");
        assertThat(retrievedProduct.get().getCategory()).isEqualTo("ELECTRONICS");
    }
    
    @Test
    void shouldFindProductsByCategory() {
        // Given
        Product headphones = new Product(
                "Wireless Headphones", 
                "Description", 
                new BigDecimal("149.99"), 
                "ELECTRONICS"
        );
        
        Product laptop = new Product(
                "Laptop", 
                "Description", 
                new BigDecimal("999.99"), 
                "ELECTRONICS"
        );
        
        Product book = new Product(
                "Java Programming", 
                "Description", 
                new BigDecimal("39.99"), 
                "BOOKS"
        );
        
        productRepository.saveAll(Arrays.asList(headphones, laptop, book));
        
        // When
        List<Product> electronicsProducts = productRepository.findByCategory("ELECTRONICS");
        
        // Then
        assertThat(electronicsProducts).hasSize(2);
        assertThat(electronicsProducts)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Wireless Headphones", "Laptop");
    }
    
    @Test
    void shouldFindProductsByPriceRange() {
        // Given
        Product headphones = new Product(
                "Wireless Headphones", 
                "Description", 
                new BigDecimal("149.99"), 
                "ELECTRONICS"
        );
        
        Product laptop = new Product(
                "Laptop", 
                "Description", 
                new BigDecimal("999.99"), 
                "ELECTRONICS"
        );
        
        Product book = new Product(
                "Java Programming", 
                "Description", 
                new BigDecimal("39.99"), 
                "BOOKS"
        );
        
        productRepository.saveAll(Arrays.asList(headphones, laptop, book));
        
        // When
        List<Product> midRangeProducts = productRepository.findByPriceBetween(
                new BigDecimal("30.00"), 
                new BigDecimal("200.00")
        );
        
        // Then
        assertThat(midRangeProducts).hasSize(2);
        assertThat(midRangeProducts)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Wireless Headphones", "Java Programming");
    }
    
    @Test
    void shouldFindProductsByTag() {
        // Given
        Product headphones = new Product(
                "Wireless Headphones", 
                "Description", 
                new BigDecimal("149.99"), 
                "ELECTRONICS"
        );
        headphones.setTags(Arrays.asList("wireless", "audio", "bluetooth"));
        
        Product wiredHeadphones = new Product(
                "Wired Headphones", 
                "Description", 
                new BigDecimal("79.99"), 
                "ELECTRONICS"
        );
        wiredHeadphones.setTags(Arrays.asList("wired", "audio"));
        
        productRepository.saveAll(Arrays.asList(headphones, wiredHeadphones));
        
        // When
        List<Product> wirelessProducts = productRepository.findByTagName("wireless");
        List<Product> audioProducts = productRepository.findByTagName("audio");
        
        // Then
        assertThat(wirelessProducts).hasSize(1);
        assertThat(wirelessProducts.get(0).getName()).isEqualTo("Wireless Headphones");
        
        assertThat(audioProducts).hasSize(2);
        assertThat(audioProducts)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Wireless Headphones", "Wired Headphones");
    }
    
    @Test
    void shouldFindCheapProductsInStock() {
        // Given
        Product headphones = new Product(
                "Wireless Headphones", 
                "Description", 
                new BigDecimal("149.99"), 
                "ELECTRONICS"
        );
        
        Product cheapHeadphones = new Product(
                "Basic Headphones", 
                "Description", 
                new BigDecimal("19.99"), 
                "ELECTRONICS"
        );
        
        Product outOfStockHeadphones = new Product(
                "Discount Headphones", 
                "Description", 
                new BigDecimal("9.99"), 
                "ELECTRONICS"
        );
        outOfStockHeadphones.setInStock(false);
        
        productRepository.saveAll(Arrays.asList(headphones, cheapHeadphones, outOfStockHeadphones));
        
        // When
        List<Product> cheapProducts = productRepository.findCheapProductsInStock(new BigDecimal("50.00"));
        
        // Then
        assertThat(cheapProducts).hasSize(1);
        assertThat(cheapProducts.get(0).getName()).isEqualTo("Basic Headphones");
    }
}