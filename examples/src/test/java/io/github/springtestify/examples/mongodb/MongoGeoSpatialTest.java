package io.github.springtestify.examples.mongodb;

import io.github.springtestify.core.annotation.DataSetup;
import io.github.springtestify.core.annotation.InMemoryDb;
import io.github.springtestify.core.annotation.PerformanceTest;
import io.github.springtestify.core.annotation.SpringTestify;
import io.github.springtestify.core.enums.DbType;
import io.github.springtestify.examples.document.Location;
import io.github.springtestify.examples.document.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringTestify
@InMemoryDb(type = DbType.MONGODB)
@DataSetup(value = "test-data/locations.json", dataType = "json", collection = "locations")
public class MongoGeoSpatialTest {

    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @BeforeEach
    void setUp() {
        // Create a text index for text search functionality
        TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("name")
                .onField("description")
                .build();
        mongoTemplate.indexOps(Location.class).ensureIndex(textIndex);
    }
    
    @Test
    void shouldFindLocationsByType() {
        // When
        List<Location> parks = locationRepository.findByType("PARK");
        List<Location> landmarks = locationRepository.findByType("LANDMARK");
        List<Location> museums = locationRepository.findByType("MUSEUM");
        List<Location> transports = locationRepository.findByType("TRANSPORT");
        
        // Then
        assertThat(parks).hasSize(3);
        assertThat(landmarks).hasSize(3);
        assertThat(museums).hasSize(2);
        assertThat(transports).hasSize(2);
    }
    
    @Test
    void shouldFindLocationsNearPoint() {
        // Given
        // Times Square location
        Point timesSquare = new Point(-73.9855, 40.7580);
        Distance oneKilometer = new Distance(1, Metrics.KILOMETERS);
        
        // When
        List<Location> nearbyLocations = locationRepository.findByPositionNear(timesSquare, oneKilometer);
        
        // Then
        assertThat(nearbyLocations).hasSizeGreaterThanOrEqualTo(3);
        // Should include Times Square itself, Bryant Park, and possibly Empire State Building
        assertThat(nearbyLocations).extracting("name")
                .contains("Times Square", "Bryant Park");
    }
    
    @Test
    void shouldFindLocationsWithinMaxDistance() {
        // Given
        // Central Park coordinates
        double longitude = -73.9654;
        double latitude = 40.7829;
        double maxDistanceInMeters = 2000; // 2 km radius
        
        // When
        List<Location> nearbyLocations = locationRepository.findNearbyLocations(longitude, latitude, maxDistanceInMeters);
        
        // Then
        assertThat(nearbyLocations).isNotEmpty();
        // Central Park should be first as it's the center point
        assertThat(nearbyLocations.get(0).getName()).isEqualTo("Central Park");
        
        // Metropolitan Museum of Art and American Museum of Natural History should be within 2km
        List<String> nearbyNames = nearbyLocations.stream()
                .map(Location::getName)
                .toList();
                
        assertThat(nearbyNames).contains(
                "Central Park",
                "Metropolitan Museum of Art", 
                "American Museum of Natural History"
        );
    }
    
    @Test
    void shouldFindLocationsWithinPolygon() {
        // Given
        // Define a polygon that encompasses Midtown Manhattan
        List<List<Double>> midtownPolygon = Arrays.asList(
                Arrays.asList(-73.9932, 40.7614), // Northwest corner
                Arrays.asList(-73.9932, 40.7450), // Southwest corner
                Arrays.asList(-73.9754, 40.7450), // Southeast corner
                Arrays.asList(-73.9754, 40.7614), // Northeast corner
                Arrays.asList(-73.9932, 40.7614)  // Back to Northwest to close the polygon
        );
        
        // When
        List<Location> locationsInMidtown = locationRepository.findWithinPolygon(midtownPolygon);
        
        // Then
        assertThat(locationsInMidtown).isNotEmpty();
        assertThat(locationsInMidtown).extracting("name")
                .contains("Times Square", "Bryant Park");
    }
    
    @Test
    void shouldFindLocationsByTextSearch() {
        // When
        List<Location> museumResults = locationRepository.findByTextSearch("museum");
        List<Location> parkResults = locationRepository.findByTextSearch("park");
        List<Location> terminalResults = locationRepository.findByTextSearch("terminal");
        
        // Then
        assertThat(museumResults).hasSize(2);
        assertThat(parkResults).hasSize(3);
        assertThat(terminalResults).hasSize(1);
        
        assertThat(museumResults).extracting("name")
                .contains("Metropolitan Museum of Art", "American Museum of Natural History");
        assertThat(terminalResults).extracting("name")
                .contains("Grand Central Terminal");
    }
    
    @Test
    @PerformanceTest(threshold = "100ms")
    void shouldPerformEfficientGeoQueries() {
        // Given - A specific location (Times Square)
        Point timesSquare = new Point(-73.9855, 40.7580);
        double radiusInKm = 1.0;
        
        // When - Run a geo query through MongoTemplate
        Query geoQuery = new Query(
                Criteria.where("position")
                        .nearSphere(timesSquare)
                        .maxDistance(radiusInKm / 6371.0) // Convert km to radians (Earth's radius ~6371km)
        );
        
        List<Location> result = mongoTemplate.find(geoQuery, Location.class);
        
        // Then - Should be efficient with proper indices
        assertThat(result).isNotEmpty();
    }
}