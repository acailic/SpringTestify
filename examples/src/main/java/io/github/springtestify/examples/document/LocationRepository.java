package io.github.springtestify.examples.document;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {
    
    List<Location> findByType(String type);
    
    // Find locations near a point within a certain distance
    List<Location> findByPositionNear(Point point, Distance distance);
    
    // Find locations within a polygon defined by points
    @Query("{'position': {$geoWithin: {$geometry: {type: 'Polygon', coordinates: [?0]}}}}")
    List<Location> findWithinPolygon(List<List<Double>> polygonCoordinates);
    
    // Find locations within a maximum distance, ordered by distance
    @Query("{'position': {$near: {$geometry: {type: 'Point', coordinates: [?0, ?1]}, $maxDistance: ?2}}}")
    List<Location> findNearbyLocations(double longitude, double latitude, double maxDistanceInMeters);
    
    // Find locations by text search on name or description
    @Query("{'$text': {'$search': ?0}}")
    List<Location> findByTextSearch(String searchText);
}