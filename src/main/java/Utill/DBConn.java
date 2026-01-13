package Utill;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

    public static Connection getConnection() {
        try {
            // Heroku standard: DATABASE_URL = postgres://user:pass@host:port/dbname
            String dbUrl = System.getenv("DATABASE_URL");

            if (dbUrl == null || dbUrl.isBlank()) {
                throw new RuntimeException("DATABASE_URL is not set in environment variables.");
            }

            URI uri = new URI(dbUrl);

            String userInfo = uri.getUserInfo(); // user:pass
            String username = userInfo.split(":")[0];
            String password = userInfo.split(":")[1];

            String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + uri.getPort() + uri.getPath();

            return DriverManager.getConnection(jdbcUrl, username, password);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
