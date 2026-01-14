package DAO;

import Bean.Staff;
import Utill.DBConn;

import java.io.InputStream;
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

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, s.getStaffName());
                ps.setString(2, s.getStaffPhone());
                ps.setString(3, s.getStaffAddress());
                ps.setString(4, s.getStaffEmail().trim().toLowerCase());
                ps.setString(5, s.getStaffRole());
                ps.setString(6, s.getPassword());
                
                Blob staffPic = s.getStaffPicture();
                if (staffPic != null) {
                    ps.setBlob(7, staffPic); // Set the image blob
                } else {
                    ps.setNull(7, java.sql.Types.BLOB); // Set null if no image is uploaded
                }

                // Execute the update
                int affectedRows = ps.executeUpdate();

                // Commit the transaction if successful
                conn.commit();

                return affectedRows > 0;
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                conn.rollback();
                throw e;
            } finally {
                // Restore the auto-commit mode
                conn.setAutoCommit(true);
            }
        }
    }

    // Staff login - authenticate with email and password
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

    // Get staff picture by ID (returns bytes)
    public byte[] getStaffPictureById(int staffID) throws SQLException {
        String sql = "SELECT staffpicture FROM staff WHERE staffid = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Blob blob = rs.getBlob("staffPicture");
                    if (blob != null) {
                        return blob.getBytes(1, (int) blob.length());
                    }
                }
            }
        }
        return null;
    }
}
