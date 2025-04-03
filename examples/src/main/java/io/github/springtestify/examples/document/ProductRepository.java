package io.github.springtestify.examples.document;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    List<Product> findByCategory(String category);
    
    List<Product> findByCategoryAndInStockIsTrue(String category);
    
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    @Query("{'tags': ?0}")
    List<Product> findByTagName(String tag);
    
    @Query("{'price': {$lt: ?0}, 'in_stock': true}")
    List<Product> findCheapProductsInStock(BigDecimal maxPrice);
    
    long countByCategory(String category);
}