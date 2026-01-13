import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

    public static Connection getConnection() {
        // Set up database connection details
        String url = "jdbc:postgresql://cee3ebbhveeoab.cluster-czrs8kj4isg7.us-east-1.rds.amazonaws.com:5432/dbu39dr34ej0dq"; // Replace with your Heroku host
        String user = "u375elp7att1k5"; // Replace with your Heroku username
        String password = "p7dd455ccb3b6240ac52d8059200db4a112c7942d8084f4753cbff9186626a833"; // Replace with your Heroku password
        
        try {
            // Load PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish and return connection
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }


