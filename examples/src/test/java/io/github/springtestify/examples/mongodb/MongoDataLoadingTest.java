package io.github.springtestify.examples.mongodb;

import io.github.springtestify.core.annotation.DataSetup;
import io.github.springtestify.core.annotation.InMemoryDb;
import io.github.springtestify.core.annotation.SpringTestify;
import io.github.springtestify.core.enums.DbType;
import io.github.springtestify.examples.document.Product;
import io.github.springtestify.examples.document.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringTestify
@InMemoryDb(type = DbType.MONGODB)
@DataSetup(value = "test-data/products.json", dataType = "json", collection = "products")
public class MongoDataLoadingTest {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Test
    void shouldLoadProductsFromJsonFile() {
        // When
        List<Product> allProducts = productRepository.findAll();
        
        // Then
        assertThat(allProducts).hasSize(7); // We have 7 products in the JSON file
        
        // Verify specific product was loaded
        Optional<Product> smartTV = allProducts.stream()
                .filter(p -> p.getName().contains("Smart TV"))
                .findFirst();
                
        assertThat(smartTV).isPresent();
        assertThat(smartTV.get().getCategory()).isEqualTo("ELECTRONICS");
        assertThat(smartTV.get().getPrice()).isEqualTo(new BigDecimal("599.99"));
        assertThat(smartTV.get().isInStock()).isTrue();
    }
    
    @Test
    void shouldFindProductsByCategory() {
        // When
        List<Product> electronicsProducts = productRepository.findByCategory("ELECTRONICS");
        List<Product> booksProducts = productRepository.findByCategory("BOOKS");
        List<Product> clothingProducts = productRepository.findByCategory("CLOTHING");
        
        // Then
        assertThat(electronicsProducts).hasSize(3);
        assertThat(booksProducts).hasSize(2);
        assertThat(clothingProducts).hasSize(2);
    }
    
    @Test
    void shouldFindProductsByTags() {
        // When
        List<Product> educationProducts = productRepository.findByTagName("education");
        
        // Then
        assertThat(educationProducts).hasSize(2);
        assertThat(educationProducts)
                .extracting(Product::getCategory)
                .containsOnly("BOOKS");
    }
    
    @Test
    void shouldFindCheapProductsInStock() {
        // When
        List<Product> cheapProducts = productRepository.findCheapProductsInStock(new BigDecimal("100.00"));
        
        // Then
        assertThat(cheapProducts).hasSize(2);
        assertThat(cheapProducts)
                .extracting(Product::getName)
                .contains("Programming Java: Complete Guide", "Cloud Computing Fundamentals");
    }
    
    @Test
    void shouldUseMongoTemplateForCustomQueries() {
        // When
        Query query = new Query(Criteria.where("tags").in("wireless", "bluetooth")
                .and("price").gt(new BigDecimal("200.00")));
                
        List<Product> expensiveWirelessProducts = mongoTemplate.find(query, Product.class);
        
        // Then
        assertThat(expensiveWirelessProducts).hasSize(1);
        assertThat(expensiveWirelessProducts.get(0).getName()).contains("Noise-Cancelling Headphones");
    }
}