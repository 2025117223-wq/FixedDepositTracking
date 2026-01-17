package DAO;

import Bean.Staff;
import Util.DBConn;

import java.sql.*;

public class StaffDAO {

    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM staff WHERE LOWER(TRIM(staff_email)) = ? LIMIT 1";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email == null ? "" : email.trim().toLowerCase());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean insertStaff(Staff s) throws SQLException {
        String sql =
                "INSERT INTO staff " +
                "(staff_name, staff_phone, staff_address, staff_email, staff_role, password, staff_picture, manager_id, staff_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConn.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, s.getStaffName());
                ps.setString(2, s.getStaffPhone());
                ps.setString(3, s.getStaffAddress());
                ps.setString(4, (s.getStaffEmail() == null ? null : s.getStaffEmail().trim().toLowerCase()));
                ps.setString(5, s.getStaffRole());
                ps.setString(6, s.getPassword());

                // staff_picture (PostgreSQL BYTEA) - Staff bean stores byte[]
                byte[] pic = s.getStaffPicture();
                if (pic != null && pic.length > 0) {
                    ps.setBytes(7, pic);
                } else {
                    ps.setNull(7, Types.BINARY);
                }

                // manager_id (nullable)
                Integer mid = s.getManagerID();
                if (mid != null && mid > 0) {
                    ps.setInt(8, mid);
                } else {
                    ps.setNull(8, Types.INTEGER);
                }

                // staff_status default
                String status = s.getStaffStatus();
                if (status == null || status.isBlank()) status = "ACTIVE";
                ps.setString(9, status);

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

    public Staff login(String email, String password) throws SQLException {
        String sql =
                "SELECT staff_id, staff_name, staff_email, staff_role, staff_status, manager_id " +
                "FROM staff " +
                "WHERE LOWER(TRIM(staff_email)) = ? AND password = ? AND staff_status = 'ACTIVE'";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email == null ? "" : email.trim().toLowerCase());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Staff s = new Staff();
                    s.setStaffID(rs.getInt("staff_id"));
                    s.setStaffName(rs.getString("staff_name"));
                    s.setStaffEmail(rs.getString("staff_email"));
                    s.setStaffRole(rs.getString("staff_role"));
                    s.setStaffStatus(rs.getString("staff_status"));

                    int mid = rs.getInt("manager_id");
                    if (!rs.wasNull()) s.setManagerID(mid);

                    return s;
                }
            }
        }
        return null;
    }

    public byte[] getStaffPictureById(int staffID) throws SQLException {
        String sql = "SELECT staff_picture FROM staff WHERE staff_id = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBytes("staff_picture");
                }
            }
        }
        return null;
    }
}
