package com.fd.dao;
public class StaffDAO {

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

    public List<Staff> getAllStaff() {
        List<Staff> staffList = new ArrayList<>();
        
        // Query to get all staff along with manager names
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
                staff.setStaffId(rs.getLong("STAFFID"));  // Use long for staffId (BIGINT in DB)
                staff.setStaffIdPrefix(rs.getString("STAFFID_PREFIX"));
                staff.setName(rs.getString("STAFFNAME"));
                staff.setPhone(rs.getString("STAFFPHONE"));
                staff.setAddress(rs.getString("STAFFADDRESS"));
                staff.setEmail(rs.getString("STAFFEMAIL"));
                staff.setRole(rs.getString("STAFFROLE"));
                staff.setStatus(rs.getString("STAFFSTATUS"));
                staff.setReason(rs.getString("REASON"));
                staff.setManagerId(rs.getLong("MANAGERID"));  // Use long for managerId (BIGINT in DB)
                staff.setManagerName(rs.getString("MANAGER_NAME"));
                
                staffList.add(staff);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading staff: " + e.getMessage());
            e.printStackTrace();
        }
        
        return staffList;
    }

    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE FD.STAFF SET STAFFROLE = ?, STAFFSTATUS = ?, REASON = ? WHERE STAFFID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, staff.getRole());
            ps.setString(2, staff.getStatus());
            
            // Set the reason only if status is 'Inactive'
            if ("Inactive".equalsIgnoreCase(staff.getStatus())) {
                ps.setString(3, staff.getReason());
            } else {
                ps.setString(3, null);
            }
            
            ps.setLong(4, staff.getStaffId());  // Use long for staffId (BIGINT in DB)

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
    
    public Staff getStaffById(long staffId) {  // Use long for staffId (BIGINT in DB)
        Staff staff = null;
        String query = "SELECT STAFFID, STAFFID_PREFIX, STAFFNAME, STAFFEMAIL, STAFFPHONE, " +
                       "STAFFADDRESS, PASSWORD, STAFFROLE, STAFFSTATUS, REASON, MANAGERID " +
                       "FROM STAFF WHERE STAFFID = ?";
        
        System.out.println("========================================");
        System.out.println("🔍 getStaffById called for ID: " + staffId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, staffId);  // Use long for staffId (BIGINT in DB)
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    staff = new Staff();
                    staff.setStaffId(rs.getLong("STAFFID"));  // Use long here
                    staff.setStaffIdPrefix(rs.getString("STAFFID_PREFIX"));
                    staff.setName(rs.getString("STAFFNAME"));
                    staff.setEmail(rs.getString("STAFFEMAIL"));
                    staff.setPhone(rs.getString("STAFFPHONE"));
                    staff.setAddress(rs.getString("STAFFADDRESS"));
                    staff.setPassword(rs.getString("PASSWORD"));
                    staff.setRole(rs.getString("STAFFROLE"));
                    staff.setStatus(rs.getString("STAFFSTATUS"));
                    staff.setReason(rs.getString("REASON"));
                    staff.setManagerId(rs.getLong("MANAGERID"));  // Use long for managerId (BIGINT in DB)
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

    public boolean registerStaff(Staff staff) {
        // Use STAFF_SEQ.NEXTVAL for auto-incrementing STAFFID
        String query = "INSERT INTO STAFF " +
                       "(STAFFID, STAFFNAME, STAFFPHONE, STAFFADDRESS, STAFFEMAIL, PASSWORD, " +
                       "STAFFROLE, STAFFSTATUS, MANAGERID, STAFFPICTURE) " +
                       "VALUES (STAFF_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("========================================");
        System.out.println("💾 StaffDAO: Registering new staff");
        System.out.println("   Name: " + staff.getName());
        System.out.println("   Email: " + staff.getEmail());
        System.out.println("   Role: " + staff.getRole());
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Note: STAFFID is auto-generated by STAFF_SEQ.NEXTVAL
            pstmt.setString(1, staff.getName());           // STAFFNAME
            pstmt.setString(2, staff.getPhone());          // STAFFPHONE
            pstmt.setString(3, staff.getAddress());        // STAFFADDRESS
            pstmt.setString(4, staff.getEmail().toLowerCase()); // STAFFEMAIL
            pstmt.setString(5, staff.getPassword());       // PASSWORD
            pstmt.setString(6, staff.getRole());           // STAFFROLE
            pstmt.setString(7, staff.getStatus() != null ? staff.getStatus() : "ACTIVE"); // STAFFSTATUS
            
            // MANAGERID (nullable)
            if (staff.getManagerId() > 0) {
                pstmt.setLong(8, staff.getManagerId());  // Use long for managerId
            } else {
                pstmt.setNull(8, java.sql.Types.INTEGER);
            }
            
            // STAFFPICTURE (BLOB)
            if (staff.getProfilePicture() != null && staff.getProfilePicture().length > 0) {
                pstmt.setBytes(9, staff.getProfilePicture());
            } else {
                pstmt.setNull(9, java.sql.Types.BLOB);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Staff registered successfully");
                System.out.println("   Rows affected: " + rowsAffected);
                System.out.println("   STAFFID auto-generated by sequence");
                return true;
            } else {
                System.err.println("❌ No rows inserted");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error registering staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
        public boolean updateStaffProfile(Staff staff) {
        String query = "UPDATE STAFF SET STAFFNAME = ?, STAFFPHONE = ?, STAFFADDRESS = ?, PASSWORD = ? WHERE STAFFID = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, staff.getName());
            pstmt.setString(2, staff.getPhone());
            pstmt.setString(3, staff.getAddress());
            pstmt.setString(4, staff.getPassword());
            pstmt.setLong(5, staff.getStaffId());  // Assuming staffId is of type long
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating staff profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
        public String getPasswordByStaffId(int staffId) {
        String query = "SELECT PASSWORD FROM STAFF WHERE STAFFID = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, staffId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("PASSWORD");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
        public boolean updatePasswordByStaffId(int staffId, String newPassword) {
        String query = "UPDATE STAFF SET PASSWORD = ? WHERE STAFFID = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, staffId);
            
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
 }
}
