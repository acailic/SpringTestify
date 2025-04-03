package io.github.springtestify.examples.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a location with geospatial coordinates.
 * Demonstrates using SpringTestify with MongoDB geospatial capabilities.
 */
@Document(collection = "locations")
public class Location {
    
    @Id
    private String id;
    
    private String name;
    
    private String type; // restaurant, cafe, park, etc.
    
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint position;
    
    private String address;
    
    public Location() {
    }
    
    public Location(String name, String type, GeoJsonPoint position, String address) {
        this.name = name;
        this.type = type;
        this.position = position;
        this.address = address;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public GeoJsonPoint getPosition() {
        return position;
    }
    
    public void setPosition(GeoJsonPoint position) {
        this.position = position;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String toString() {
        return "Location{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", position=" + position +
                ", address='" + address + '\'' +
                '}';
    }
}