package io.github.springtestify.examples.mongodb;

import io.github.springtestify.core.annotation.GenerateTestData;
import io.github.springtestify.core.annotation.InMemoryDb;
import io.github.springtestify.core.annotation.SpringTestify;
import io.github.springtestify.core.enums.DbType;
import io.github.springtestify.data.util.PropertyValueBuilder;
import io.github.springtestify.data.util.TestDataRegistry;
import io.github.springtestify.examples.document.Product;
import io.github.springtestify.examples.document.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringTestify
@InMemoryDb(type = DbType.MONGODB)
@GenerateTestData(
    entity = Product.class,
    count = 15,
    properties = {
        "category=ELECTRONICS:5,BOOKS:5,CLOTHING:5",
        "inStock=true:12,false:3",
        "stockCount=0:3,10:5,100:7"
    }
)
public class MongoDataIntegrationTest {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private TestDataRegistry testData;
    
    @Test
    void shouldGenerateProductsWithSpecifiedDistribution() {
        // When
        List<Product> allProducts = productRepository.findAll();
        
        // Then
        assertThat(allProducts).hasSize(15);
        
        List<Product> electronics = productRepository.findByCategory("ELECTRONICS");
        List<Product> books = productRepository.findByCategory("BOOKS");
        List<Product> clothing = productRepository.findByCategory("CLOTHING");
        
        assertThat(electronics).hasSize(5);
        assertThat(books).hasSize(5);
        assertThat(clothing).hasSize(5);
        
        List<Product> outOfStockProducts = mongoTemplate.find(
                Query.query(Criteria.where("in_stock").is(false)), 
                Product.class);
        assertThat(outOfStockProducts).hasSize(3);
    }
    
    @Test
    void shouldAccessGeneratedDataThroughRegistry() {
        // When
        List<Product> electronics = testData.findAll(Product.class, p -> "ELECTRONICS".equals(p.getCategory()));
        List<Product> inStock = testData.findAll(Product.class, Product::isInStock);
        List<Product> noStock = testData.findAll(Product.class, p -> p.getStockCount() == 0);
        
        // Then
        assertThat(electronics).hasSize(5);
        assertThat(inStock).hasSize(12);
        assertThat(noStock).hasSize(3);
    }
    
    @Test
    void shouldPerformMongoQueries() {
        // Given
        for (Product product : testData.findAll(Product.class, p -> "ELECTRONICS".equals(p.getCategory()))) {
            if (product.isInStock()) {
                product.addTag("popular");
                product.addTag("tech");
                mongoTemplate.save(product);
            }
        }
        
        // When
        List<Product> popularProducts = productRepository.findByTagName("popular");
        
        // Then
        assertThat(popularProducts).hasSizeLessThanOrEqualTo(5); // At most 5 electronics are popular
        assertThat(popularProducts)
                .allMatch(p -> p.getCategory().equals("ELECTRONICS"))
                .allMatch(Product::isInStock);
    }
}