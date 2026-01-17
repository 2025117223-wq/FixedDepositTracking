package DAO;

import Bean.Staff;
import Utill.DBConn;

import java.io.InputStream;
import java.sql.*;

public class StaffDAO {

    /**
     * NOTE (PostgreSQL):
     * - Use lowercase / snake_case in your real DB schema if possible.
     * - Example columns used below:
     *   staff_id, staff_name, staff_phone, staff_address, staff_email,
     *   staff_role, password, staff_picture (BYTEA), manager_id, staff_status
     *
     * If your actual table/column names differ, change them accordingly.
     */
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

    /**
     * Insert staff.
     * PostgreSQL image storage:
     * - staff_picture should be BYTEA
     * - Use setBinaryStream (or setBytes) instead of setBlob
     */
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

                // staff_picture (BYTEA)
                InputStream in = s.getStaffPicture();
                if (in != null) {
                    ps.setBinaryStream(7, in);
                } else {
                    ps.setNull(7, Types.BINARY);
                }

                // manager_id
                if (s.getManagerID() > 0) {
                    ps.setInt(8, s.getManagerID());
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

    /**
     * Login only if ACTIVE (recommended).
     * If you don't want status filtering, remove "AND staff_status = 'ACTIVE'".
     */
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

    /**
     * PostgreSQL BYTEA: use getBytes directly (no Blob).
     */
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
