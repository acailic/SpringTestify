package io.github.springtestify.core.enums;

/**
 * Enumeration of supported in-memory database types.
 * <p>
 * Each type represents a different in-memory database with specific dialect and configuration.
 */
public enum DbType {
    /**
     * H2 database with default configuration.
     */
    H2,

    /**
     * H2 database with MySQL compatibility mode.
     */
    MYSQL_COMPATIBLE,

    /**
     * H2 database with PostgreSQL compatibility mode.
     */
    POSTGRES_COMPATIBLE,

    /**
     * HSQLDB database.
     */
    HSQLDB,

    /**
     * Apache Derby database.
     */
    DERBY,

    /**
     * MongoDB database.
     */
    MONGODB;
}
