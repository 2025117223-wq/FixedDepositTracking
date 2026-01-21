package com.fd.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Utility Class for PostgreSQL Database
 * Provides connection to PostgreSQL database for the FD Tracking System
 */
public class DBConnection {
    
    // PostgreSQL Database Connection Parameters
    // UPDATE THESE VALUES TO MATCH YOUR DATABASE
    private static final String DB_URL = "jdbc:postgresql://cee3ebbhveeoab.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/dbu39dr34ej0dq";  // <<< Change this to your PostgreSQL DB URL
    private static final String DB_USER = "u375elp7att1k5";     
    private static final String DB_PASSWORD = "p7dd455ccb3b6240ac52d8059200db4a112c7942d8084f4753cbff9186626a833";  // <<< CHANGE THIS
    
    // JDBC Driver
    private static final String JDBC_DRIVER = "org.postgresql.Driver";  
    
    // Static block to load the driver
    static {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("PostgreSQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading PostgreSQL JDBC Driver: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            return conn;
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Close database connection
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
