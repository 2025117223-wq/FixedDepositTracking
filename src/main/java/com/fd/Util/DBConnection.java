package com.fd.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database credentials
    private static final String URL  = "jdbc:postgresql://cee3ebbhveeoab.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/dbu39dr34ej0dq";
    private static final String USER = "u375elp7att1k5";
    private static final String PASS = "p7dd455ccb3b6240ac52d8059200db4a112c7942d8084f4753cbff9186626a833";

    // Method to establish and return a connection
    public static Connection getConnection() throws SQLException {
        try {
            // Load PostgreSQL JDBC driver (optional for some environments)
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found.", e);
        }

        // Return the connection using the provided credentials
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
