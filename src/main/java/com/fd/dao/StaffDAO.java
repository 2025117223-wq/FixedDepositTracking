package com.fd.dao;

import com.fd.model.Staff;
import com.fd.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StaffDAO {

    // Login method to verify staff credentials
    public Staff login(String email, String password) {
        String query = "SELECT * FROM STAFF WHERE LOWER(STAFFEMAIL) = LOWER(?) AND PASSWORD = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Staff staff = new Staff();
                staff.setStaffId(rs.getInt("STAFFID"));
                staff.setName(rs.getString("STAFFNAME"));
                staff.setEmail(rs.getString("STAFFEMAIL"));
                staff.setRole(rs.getString("STAFFROLE"));
                staff.setStatus(rs.getString("STAFFSTATUS"));
                staff.setManagerId(rs.getInt("MANAGERID"));
                return staff;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieve all staff
    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        
        // Add STAFFID_PREFIX to the query
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
                staff.setStaffId(rs.getInt("STAFFID"));
                staff.setStaffIdPrefix(rs.getString("STAFFID_PREFIX")); // Add this line
                staff.setName(rs.getString("STAFFNAME"));
                staff.setPhone(rs.getString("STAFFPHONE"));
                staff.setAddress(rs.getString("STAFFADDRESS"));
                staff.setEmail(rs.getString("STAFFEMAIL"));
                staff.setRole(rs.getString("STAFFROLE"));
                staff.setStatus(rs.getString("STAFFSTATUS"));
                staff.setReason(rs.getString("REASON"));
                staff.setManagerId(rs.getInt("MANAGERID"));
                staff.setManagerName(rs.getString("MANAGER_NAME"));
                
                staffList.add(staff);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading staff: " + e.getMessage());
            e.printStackTrace();
        }
        
        return staffList;
    }

    // Update staff information
    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE FD.STAFF SET STAFFROLE = ?, STAFFSTATUS = ?, REASON = ? WHERE STAFFID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, staff.getRole());
            ps.setString(2, staff.getStatus());
            
            if ("Inactive".equalsIgnoreCase(staff.getStatus())) {
                ps.setString(3, staff.getReason());
            } else {
                ps.setString(3, null);
            }
            
            ps.setInt(4, staff.getStaffId());

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
    
    // Get a single staff by ID
    public Staff getStaffById(int staffId) {
        Staff staff = null;
        String query = "SELECT STAFFID, STAFFID_PREFIX, STAFFNAME, STAFFEMAIL, STAFFPHONE, " +
                       "STAFFADDRESS, PASSWORD, STAFFPICTURE, STAFFROLE, STAFFSTATUS, REASON, MANAGERID " +
                       "FROM STAFF WHERE STAFFID = ?";
        
        System.out.println("========================================");
        System.out.println("🔍 getStaffById called for ID: " + staffId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, staffId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    staff = new Staff();
                    
                    // Debug: Print what we get from database
                    System.out.println("📋 Database values:");
                    System.out.println("   STAFFID: " + rs.getInt("STAFFID"));
                    System.out.println("   STAFFID_PREFIX: " + rs.getString("STAFFID_PREFIX"));
                    System.out.println("   STAFFNAME: " + rs.getString("STAFFNAME"));
                    System.out.println("   STAFFEMAIL: " + rs.getString("STAFFEMAIL"));
                    System.out.println("   STAFFPHONE: " + rs.getString("STAFFPHONE"));
                    System.out.println("   STAFFADDRESS: " + rs.getString("STAFFADDRESS"));
                    System.out.println("   PASSWORD: " + rs.getString("PASSWORD"));
                    System.out.println("   STAFFROLE: " + rs.getString("STAFFROLE"));
                    
                    staff.setStaffId(rs.getInt("STAFFID"));
                    staff.setStaffIdPrefix(rs.getString("STAFFID_PREFIX"));
                    staff.setName(rs.getString("STAFFNAME"));
                    staff.setEmail(rs.getString("STAFFEMAIL"));
                    staff.setPhone(rs.getString("STAFFPHONE"));
                    staff.setAddress(rs.getString("STAFFADDRESS"));
                    staff.setPassword(rs.getString("PASSWORD"));
                    staff.setProfilePicture(rs.getBytes("STAFFPICTURE"));
                    staff.setRole(rs.getString("STAFFROLE"));
                    staff.setStatus(rs.getString("STAFFSTATUS"));
                    staff.setReason(rs.getString("REASON"));
                    staff.setManagerId(rs.getInt("MANAGERID"));
                    
                    // Debug: Print what we set in Staff object
                    System.out.println("✅ Staff object created:");
                    System.out.println("   Name: " + staff.getName());
                    System.out.println("   Email: " + staff.getEmail());
                    System.out.println("   Phone: " + staff.getPhone());
                    System.out.println("   Address: " + staff.getAddress());
                    System.out.println("   Role: " + staff.getRole());
                    System.out.println("   Has Picture: " + (staff.getProfilePicture() != null && staff.getProfilePicture().length > 0));
                    System.out.println("========================================");
                    
                } else {
                    System.out.println("❌ No staff found with ID: " + staffId);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return staff;
    }

    // Update staff profile (name, phone, address, password, profile picture)
    public boolean updateStaffProfile(Staff staff) {
        String query = "UPDATE STAFF SET STAFFNAME = ?, STAFFPHONE = ?, STAFFADDRESS = ?, PASSWORD = ?, STAFFPICTURE = ? " +
                       "WHERE STAFFID = ?";
        
        System.out.println("========================================");
        System.out.println("💾 StaffDAO: Updating profile");
        System.out.println("   Staff ID: " + staff.getStaffId());
        System.out.println("   Name: " + staff.getName());
        System.out.println("   Has Picture: " + (staff.getProfilePicture() != null && staff.getProfilePicture().length > 0));
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getPhone());
            pstmt.setString(3, staff.getAddress());
            pstmt.setString(4, staff.getPassword());
            
            // Handle profile picture
            if (staff.getProfilePicture() != null && staff.getProfilePicture().length > 0) {
                pstmt.setBytes(5, staff.getProfilePicture());
            } else {
                pstmt.setNull(5, java.sql.Types.BLOB);
            }
            
            pstmt.setInt(6, staff.getStaffId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Profile updated successfully");
                System.out.println("   Rows affected: " + rowsAffected);
                System.out.println("========================================");
                return true;
            } else {
                System.err.println("❌ No rows updated - Staff ID not found?");
                System.out.println("========================================");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating profile: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            return false;
        }
    }

    // Check if email already exists in the system
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM STAFF WHERE LOWER(STAFFEMAIL) = LOWER(?)";
        
        System.out.println("========================================");
        System.out.println("🔍 StaffDAO: Checking email existence");
        System.out.println("   Email: " + email);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    boolean exists = count > 0;
                    System.out.println(exists ? "❌ Email exists" : "✅ Email available");
                    System.out.println("========================================");
                    return exists;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking email: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("========================================");
        return false;
    }

    // Register a new staff member
    public boolean registerStaff(Staff staff) {
        String query = "INSERT INTO STAFF " +
                       "(STAFFID_PREFIX, STAFFNAME, STAFFPHONE, STAFFADDRESS, STAFFEMAIL, PASSWORD, " +
                       "STAFFROLE, STAFFSTATUS, MANAGERID, STAFFPICTURE) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("========================================");
        System.out.println("💾 StaffDAO: Registering new staff");
        System.out.println("   Name: " + staff.getName());
        System.out.println("   Email: " + staff.getEmail());
        System.out.println("   Role: " + staff.getRole());
        System.out.println("   Prefix: " + staff.getStaffIdPrefix());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, staff.getStaffIdPrefix() != null ? staff.getStaffIdPrefix() : "Staff");
            pstmt.setString(2, staff.getName());
            pstmt.setString(3, staff.getPhone());
            pstmt.setString(4, staff.getAddress());
            pstmt.setString(5, staff.getEmail().toLowerCase());
            pstmt.setString(6, staff.getPassword());
            pstmt.setString(7, staff.getRole());
            pstmt.setString(8, staff.getStatus() != null ? staff.getStatus() : "ACTIVE");
            
            // Manager ID
            if (staff.getManagerId() > 0) {
                pstmt.setInt(9, staff.getManagerId());
            } else {
                pstmt.setNull(9, java.sql.Types.INTEGER);
            }
            
            // Profile Picture
            if (staff.getProfilePicture() != null && staff.getProfilePicture().length > 0) {
                pstmt.setBytes(10, staff.getProfilePicture());
            } else {
                pstmt.setNull(10, java.sql.Types.BLOB);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Staff registered successfully");
                System.out.println("   Rows affected: " + rowsAffected);
                System.out.println("========================================");
                return true;
            } else {
                System.err.println("❌ No rows inserted");
                System.out.println("========================================");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error registering staff: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            return false;
        }
    }
}
