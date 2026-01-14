package DAO;

import Bean.Staff;
import Utill.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StaffDAO {

    // Check if email exists
    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM staff WHERE staffemail = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Insert a new staff member
    public boolean insertStaff(Staff s) throws SQLException {
        String sql = "INSERT INTO staff (staffname, staffphone, staffaddress, staffemail, staffrole, password, staffpicture) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getStaffName());
            ps.setString(2, s.getStaffPhone());
            ps.setString(3, s.getStaffAddress());
            ps.setString(4, s.getStaffEmail().trim().toLowerCase());
            ps.setString(5, s.getStaffRole());
            ps.setString(6, s.getPassword());

            // PostgreSQL BYTEA -> use byte[]
            byte[] picBytes = s.getStaffPictureBytes(); // ✅ buat getter byte[] dalam Bean
            if (picBytes != null && picBytes.length > 0) {
                ps.setBytes(7, picBytes);
            } else {
                ps.setNull(7, java.sql.Types.BINARY);
            }

            return ps.executeUpdate() > 0;
        }
    }

    // Staff login
    public Staff login(String email, String password) throws SQLException {
        String sql = "SELECT staffid, staffname, staffemail, staffrole, staffstatus "
                   + "FROM staff WHERE staffemail = ? AND password = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Staff s = new Staff();
                    s.setStaffID(rs.getInt("staffid"));
                    s.setStaffName(rs.getString("staffname"));
                    s.setStaffEmail(rs.getString("staffemail"));
                    s.setStaffRole(rs.getString("staffrole"));
                    s.setStaffStatus(rs.getString("staffstatus"));
                    return s;
                }
            }
        }
        return null;
    }

    // Get staff picture by ID
    public byte[] getStaffPictureById(int staffID) throws SQLException {
        String sql = "SELECT staffpicture FROM staff WHERE staffid = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("staffpicture"); // ✅ bytea -> bytes
                }
            }
        }
        return null;
    }
}
