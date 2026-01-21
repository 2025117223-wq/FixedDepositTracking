package com.fd.dao;

import com.fd.model.Staff;
import com.fd.util.DBConnection;
import jakarta.mail.MessagingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    // Check if the email already exists
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM STAFF WHERE STAFFEMAIL = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Login method to validate email and return staff details
    public Staff login(String email) {
        String query = "SELECT * FROM STAFF WHERE STAFFEMAIL = ?";
        Staff staff = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    staff = new Staff();
                    staff.setStaffId(rs.getLong("STAFFID"));
                    staff.setName(rs.getString("STAFFNAME"));
                    staff.setEmail(rs.getString("STAFFEMAIL"));
                    staff.setPassword(rs.getString("PASSWORD"));
                    staff.setRole(rs.getString("STAFFROLE"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staff;
    }

    // Get all staff members along with their manager details
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();

        String query = "SELECT s.STAFFID, s.STAFFID_PREFIX, s.STAFFNAME, s.STAFFPHONE, " +
                "s.STAFFADDRESS, s.STAFFEMAIL, s.STAFFROLE, s.STAFFSTATUS, " +
                "s.REASON, s.MANAGERID, m.STAFFNAME AS MANAGER_NAME " +
                "FROM STAFF s " +
                "LEFT JOIN STAFF m ON s.MANAGERID = m.STAFFID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Staff staff = new Staff();
                staff.setStaffId(rs.getLong("STAFFID"));
                staff.setStaffIdPrefix(rs.getString("STAFFID_PREFIX"));
                staff.setName(rs.getString("STAFFNAME"));
                staff.setPhone(rs.getString("STAFFPHONE"));
                staff.setAddress(rs.getString("STAFFADDRESS"));
                staff.setEmail(rs.getString("STAFFEMAIL"));
                staff.setRole(rs.getString("STAFFROLE"));
                staff.setStatus(rs.getString("STAFFSTATUS"));
                staff.setReason(rs.getString("REASON"));
                staff.setManagerId(rs.getLong("MANAGERID"));
                staff.setManagerName(rs.getString("MANAGER_NAME"));

                staffList.add(staff);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading staff: " + e.getMessage());
            e.printStackTrace();
        }

        return staffList;
    }

    // Update staff details
    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE FD.STAFF SET STAFFNAME = ?, STAFFPHONE = ?, STAFFADDRESS = ?, STAFFEMAIL = ?, STAFFROLE = ?, STAFFSTATUS = ? WHERE STAFFID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staff.getName());
            ps.setString(2, staff.getPhone());
            ps.setString(3, staff.getAddress());
            ps.setString(4, staff.getEmail());
            ps.setString(5, staff.getRole());
            ps.setString(6, staff.getStatus());
            ps.setLong(7, staff.getStaffId());  // Using Long type for staffId

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Updated staff ID " + staff.getStaffId());
            } else {
                System.err.println("⚠️ No rows updated for staff ID " + staff.getStaffId());
            }

            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Update Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get staff ID by email and status
    public Long getStaffIdByEmailAndStatus(String email, String status) {
        String query = "SELECT STAFFID FROM STAFF WHERE STAFFEMAIL = ? AND STAFFSTATUS = ?";
        Long staffId = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    staffId = rs.getLong("STAFFID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return staffId;
    }

    // Register a new staff member
    public boolean registerStaff(Staff staff) {
        String query = "INSERT INTO STAFF " +
                "(STAFFID, STAFFNAME, STAFFPHONE, STAFFADDRESS, STAFFEMAIL, PASSWORD, " +
                "STAFFROLE, STAFFSTATUS, MANAGERID, STAFFPICTURE) " +
                "VALUES (STAFF_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getPhone());
            pstmt.setString(3, staff.getAddress());
            pstmt.setString(4, staff.getEmail().toLowerCase());
            pstmt.setString(5, staff.getPassword());
            pstmt.setString(6, staff.getRole());
            pstmt.setString(7, staff.getStatus() != null ? staff.getStatus() : "ACTIVE");

            if (staff.getManagerId() > 0) {
                pstmt.setLong(8, staff.getManagerId());
            } else {
                pstmt.setNull(8, java.sql.Types.INTEGER);
            }

            if (staff.getProfilePicture() != null && staff.getProfilePicture().length > 0) {
                pstmt.setBytes(9, staff.getProfilePicture());
            } else {
                pstmt.setNull(9, java.sql.Types.BLOB);
            }

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
