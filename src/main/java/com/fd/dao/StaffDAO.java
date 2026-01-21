package com.fd.dao;

import com.fd.model.Staff;
import com.fd.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class StaffDAO {

	public List<STAFF> getAllStaff() {
	    List<STAFF> staffList = new ArrayList<>();
	    
	    // ADD STAFFID_PREFIX to the query
	    String query = "SELECT s.STAFFID, s.STAFFID_PREFIX, s.STAFFNAME, s.STAFFPHONE, " +
	                   "s.STAFFADDRESS, s.STAFFEMAIL, s.STAFFROLE, s.STAFFSTATUS, " +
	                   "s.REASON, s.MANAGERID, m.STAFFNAME AS MANAGER_NAME " +
	                   "FROM STAFF s " +
	                   "LEFT JOIN STAFF m ON s.MANAGERID = m.STAFFID";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {
	        
	        while (rs.next()) {
	            STAFF staff = new STAFF();
	            staff.setStaffId(rs.getInt("STAFFID"));
	            staff.setStaffIdPrefix(rs.getString("STAFFID_PREFIX")); // ← ADD THIS LINE
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

    public boolean updateStaff(STAFF staff) {
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
    
    public STAFF getStaffById(int staffId) {
        STAFF staff = null;
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
                    staff = new STAFF();
                    
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
                    staff.setProfilePicture(rs.getBytes("STAFFPICTURE")); // ✅ ADD THIS LINE
                    staff.setRole(rs.getString("STAFFROLE"));
                    staff.setStatus(rs.getString("STAFFSTATUS"));
                    staff.setReason(rs.getString("REASON"));
                    staff.setManagerId(rs.getInt("MANAGERID"));
                    
                    // Debug: Print what we set in STAFF object
                    System.out.println("✅ STAFF object created:");
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

    /**
     * Update staff profile (name, phone, address, password, profile picture) - UPDATED
     */
    public boolean updateStaffProfile(STAFF staff) {
        // Include STAFFPICTURE in the update
        String query = "UPDATE STAFF SET STAFFNAME = ?, STAFFPHONE = ?, STAFFADDRESS = ?, PASSWORD = ?, STAFFPICTURE = ? " +
                       "WHERE STAFFID = ?";
        
        System.out.println("========================================");
        System.out.println("💾 StaffDAO: Updating profile");
        System.out.println("   STAFF ID: " + staff.getStaffId());
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
                System.err.println("❌ No rows updated - STAFF ID not found?");
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

    /**
     * Register new staff member
     */
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM STAFF WHERE STAFFEMAIL ILIKE ?";
        
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

    /**
     * Register new staff member
     * UPDATED VERSION - Includes STAFFID_PREFIX
     */
    public boolean registerStaff(STAFF staff) {
        // Use STAFF_SEQ.NEXTVAL for auto-incrementing STAFFID
        String query = "INSERT INTO STAFF " +
                       "(STAFFID, STAFFID_PREFIX, STAFFNAME, STAFFPHONE, STAFFADDRESS, STAFFEMAIL, PASSWORD, " +
                       "STAFFROLE, STAFFSTATUS, MANAGERID, STAFFPICTURE) " +
                       "VALUES (STAFF_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("========================================");
        System.out.println("💾 StaffDAO: Registering new staff");
        System.out.println("   Name: " + staff.getName());
        System.out.println("   Email: " + staff.getEmail());
        System.out.println("   Role: " + staff.getRole());
        System.out.println("   Prefix: " + staff.getStaffIdPrefix());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Note: STAFFID is auto-generated by STAFF_SEQ.NEXTVAL
            pstmt.setString(1, staff.getStaffIdPrefix() != null ? staff.getStaffIdPrefix() : "STAFF"); // STAFFID_PREFIX
            pstmt.setString(2, staff.getName());           // STAFFNAME
            pstmt.setString(3, staff.getPhone());          // STAFFPHONE
            pstmt.setString(4, staff.getAddress());        // STAFFADDRESS
            pstmt.setString(5, staff.getEmail().toLowerCase()); // STAFFEMAIL
            pstmt.setString(6, staff.getPassword());       // PASSWORD
            pstmt.setString(7, staff.getRole());           // STAFFROLE
            pstmt.setString(8, staff.getStatus() != null ? staff.getStatus() : "ACTIVE"); // STAFFSTATUS
            
            // MANAGERID (nullable)
            if (staff.getManagerId() > 0) {
                pstmt.setInt(9, staff.getManagerId());
            } else {
                pstmt.setNull(9, java.sql.Types.INTEGER);
            }
            
            // STAFFPICTURE (BLOB)
            if (staff.getProfilePicture() != null && staff.getProfilePicture().length > 0) {
                pstmt.setBytes(10, staff.getProfilePicture());
            } else {
                pstmt.setNull(10, java.sql.Types.BLOB);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ STAFF registered successfully");
                System.out.println("   Rows affected: " + rowsAffected);
                System.out.println("   STAFFID auto-generated by sequence");
                System.out.println("   STAFFID_PREFIX: " + staff.getStaffIdPrefix());
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
    
    public STAFF login(String email, String password) {
        String query = "SELECT STAFFID, STAFFNAME, STAFFEMAIL, STAFFROLE, STAFFSTATUS, MANAGERID " +
                       "FROM STAFF " +
                       "WHERE STAFFEMAIL ILIKE ? AND PASSWORD = ?";
        
        System.out.println("========================================");
        System.out.println("🔍 StaffDAO: Authenticating user");
        System.out.println("   Email: " + email);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email.trim());
            pstmt.setString(2, password);  // In production, this should be hashed!
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    STAFF staff = new STAFF();
                    staff.setStaffId(rs.getInt("STAFFID"));
                    staff.setName(rs.getString("STAFFNAME"));
                    staff.setEmail(rs.getString("STAFFEMAIL"));
                    staff.setRole(rs.getString("STAFFROLE"));
                    staff.setStatus(rs.getString("STAFFSTATUS"));
                    
                    int managerId = rs.getInt("MANAGERID");
                    if (!rs.wasNull()) {
                        staff.setManagerId(managerId);
                    } else {
                        staff.setManagerId(0);
                    }
                    
                    System.out.println("✅ User found");
                    System.out.println("   STAFF ID: " + staff.getStaffId());
                    System.out.println("   Name: " + staff.getName());
                    System.out.println("   Role: " + staff.getRole());
                    System.out.println("   Status: " + staff.getStatus());
                    System.out.println("========================================");
                    
                    return staff;
                } else {
                    System.err.println("❌ No user found with these credentials");
                    System.out.println("========================================");
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error during login: " + e.getMessage());
            e.printStackTrace();
            System.out.println("========================================");
            return null;
        }
    }
    
    public Integer getStaffIdByEmailAndStatus(String email, String status) {
        String query = "SELECT STAFFID FROM STAFF WHERE STAFFEMAIL ILIKE ? AND STAFFSTATUS = ?";
        
        System.out.println("🔍 StaffDAO: Getting staff ID by email and status");
        System.out.println("   Email: " + email);
        System.out.println("   Status: " + status);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, email.trim());
            pstmt.setString(2, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int staffId = rs.getInt("STAFFID");
                    System.out.println("✅ STAFF found - ID: " + staffId);
                    return staffId;
                } else {
                    System.out.println("❌ No staff found");
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get password by STAFF ID
     * Used to check if new password is same as old password
     */
    public String getPasswordByStaffId(int staffId) {
        String query = "SELECT PASSWORD FROM STAFF WHERE STAFFID = ?";
        
        System.out.println("🔍 StaffDAO: Getting password for STAFF ID: " + staffId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, staffId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✅ Password retrieved");
                    return rs.getString("PASSWORD");
                } else {
                    System.out.println("❌ No staff found");
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update password by STAFF ID
     * Used in password reset flow
     */
    public boolean updatePasswordByStaffId(int staffId, String newPassword) {
        String query = "UPDATE STAFF SET PASSWORD = ? WHERE STAFFID = ?";
        
        System.out.println("💾 StaffDAO: Updating password for STAFF ID: " + staffId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, staffId);
            
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("✅ Password updated successfully");
                return true;
            } else {
                System.out.println("❌ No rows updated");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public String formatStaffId(String prefix, int staffId) {
        return String.format("%s%02d", prefix, staffId); // ← Change %04d to %02d
    }
    
    public int getNextStaffIdNumber(String prefix) {
        int nextNumber = 1;
        String query = "SELECT MAX(STAFFID) AS MAX_ID FROM STAFF WHERE STAFFID_PREFIX = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, prefix);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int maxId = rs.getInt("MAX_ID");
                    if (!rs.wasNull()) {
                        nextNumber = maxId + 1;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting next staff ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("📋 Next STAFF ID for prefix '" + prefix + "': " + nextNumber);
        return nextNumber;
    }

    /**
     * Get staff ID prefix based on role
     * Updated prefix format without "0" suffix
     */
    public String getStaffIdPrefix(String role) {
        if (role == null) return "STAFF";
        
        if (role.equals("Finance Executive")) {
            return "FinanceE";
        } else if (role.equals("Senior Finance Manager")) {
            return "FinanceM";
        } else if (role.equals("Finance Manager")) {
            return "FinanceM";
        }
        
        return "STAFF"; // Default prefix
    }

}
