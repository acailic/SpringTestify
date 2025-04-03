package io.github.springtestify.examples.document;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a GeoJSON Point for MongoDB geospatial indexing and queries.
 * Follows the GeoJSON format required by MongoDB.
 */
public class GeoJsonPoint {
    
    private String type = "Point";
    private double[] coordinates;
    
    public GeoJsonPoint() {
        this.coordinates = new double[2];
    }
    
    public GeoJsonPoint(double longitude, double latitude) {
        this.coordinates = new double[]{longitude, latitude};
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public double[] getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
    
    public double getLongitude() {
        return coordinates[0];
    }
    
    public double getLatitude() {
        return coordinates[1];
    }
    
    public void setLongitude(double longitude) {
        if (coordinates == null) {
            coordinates = new double[2];
        }
        coordinates[0] = longitude;
    }
    
    public void setLatitude(double latitude) {
        if (coordinates == null) {
            coordinates = new double[2];
        }
        coordinates[1] = latitude;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoJsonPoint that = (GeoJsonPoint) o;
        return Arrays.equals(coordinates, that.coordinates) &&
               Objects.equals(type, that.type);
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(coordinates);
        return result;
    }
    
    @Override
    public String toString() {
        return "GeoJsonPoint{" +
                "type='" + type + '\'' +
                ", coordinates=[" + coordinates[0] + ", " + coordinates[1] + ']' +
                '}';
    }
}