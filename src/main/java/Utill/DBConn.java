package Utill;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/FREEPDB1";
    private static final String DB_USER = "FDDB";
    private static final String DB_PASSWORD = "fddb";

    private DBConn() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
