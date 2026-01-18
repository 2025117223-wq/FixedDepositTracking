package c.f.authservice.service;

import c.f.authservice.dto.LoginResponse;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Service
public class AuthService {

    private final DataSource dataSource;

    public AuthService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public LoginResponse login(String email, String password) {
        String sql =
                "SELECT staffid, staffname, staffemail, staffrole, staffstatus, managerid " +
                "FROM staff " +
                "WHERE LOWER(TRIM(staffemail)) = ? AND password = ? AND staffstatus = 'ACTIVE'";

        String emailClean = (email == null) ? "" : email.trim().toLowerCase();
        String passClean = (password == null) ? "" : password.trim();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emailClean);
            ps.setString(2, passClean);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Long staffId = rs.getLong("staffid");
                    String staffName = rs.getString("staffname");
                    String staffEmail = rs.getString("staffemail");
                    String staffRole = rs.getString("staffrole");
                    String staffStatus = rs.getString("staffstatus");

                    Long managerId = rs.getLong("managerid");
                    if (rs.wasNull()) managerId = null;

                    return LoginResponse.ok(staffId, staffName, staffEmail, staffRole, staffStatus, managerId);
                }
            }

            return LoginResponse.fail("Invalid email or password.");

        } catch (Exception e) {
            e.printStackTrace();
            return LoginResponse.fail("Server error. Please try again.");
        }
    }
}
