package DAO;

import Bean.Staff;
import Util.DBConn;

import java.sql.*;

public class StaffDAO {

    // Check if the email already exists in the database
    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM staff WHERE LOWER(TRIM(staffemail)) = ? LIMIT 1";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email == null ? "" : email.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Insert a new staff record
    public boolean insertStaff(Staff s) throws SQLException {
        String sql =
                "INSERT INTO staff " +
                "(staffname, staffphone, staffaddress, staffemail, password, staffpicture, staffrole, staffstatus, managerid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConn.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, s.getStaffName());
                ps.setString(2, s.getStaffPhone());
                ps.setString(3, s.getStaffAddress());
                ps.setString(4, (s.getStaffEmail() == null ? null : s.getStaffEmail().trim().toLowerCase()));
                ps.setString(5, s.getPassword());

                // staff_picture (PostgreSQL BYTEA) - Staff bean stores byte[]
                byte[] pic = s.getStaffPicture();
                if (pic != null && pic.length > 0) {
                    ps.setBytes(6, pic);
                } else {
                    ps.setNull(6, Types.BINARY);
                }

                ps.setString(7, s.getStaffRole());

                // Default staff_status is 'ACTIVE' if not provided
                String status = s.getStaffStatus();
                if (status == null || status.isBlank()) status = "ACTIVE";
                ps.setString(8, status);

                // manager_id (nullable, now Integer for INT)
                Integer mid = s.getManagerID();  // Use Integer for managerID
                if (mid != null && mid > 0) {
                    ps.setInt(9, mid);  // Use setInt for Integer value (INT)
                } else {
                    ps.setNull(9, Types.INTEGER);
                }

                int affected = ps.executeUpdate();
                conn.commit();
                return affected > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    // Login check for active staff
    public Staff login(String email, String password) throws SQLException {
        String sql =
                "SELECT staffid, staffname, staffemail, staffrole, staffstatus, managerid " +
                "FROM staff " +
                "WHERE LOWER(TRIM(staffemail)) = ? AND password = ? AND staffstatus = 'ACTIVE'";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email == null ? "" : email.trim().toLowerCase());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Staff s = new Staff();
                    s.setStaffID(rs.getInt("staffid"));  // Use getInt for staffid
                    s.setStaffName(rs.getString("staffname"));
                    s.setStaffEmail(rs.getString("staffemail"));
                    s.setStaffRole(rs.getString("staffrole"));
                    s.setStaffStatus(rs.getString("staffstatus"));

                    Integer mid = rs.getInt("managerid");  // Use getInt for managerid
                    if (!rs.wasNull()) s.setManagerID(mid);

                    return s;
                }
            }
        }
        return null;
    }

    // Retrieve the staff picture by ID
    public byte[] getStaffPictureById(int staffID) throws SQLException {
        String sql = "SELECT staffpicture FROM staff WHERE staffid = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffID);  // Use setInt for staffID

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("staffpicture");  // staffpicture is BYTEA in Postgres
                }
            }
        }
        return null;
    }
}
