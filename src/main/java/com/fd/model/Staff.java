package com.fd.model;

import java.io.Serializable;

public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;

    private long staffId; // Changed to long for consistency with BIGSERIAL in PostgreSQL
    private String name;
    private String phone;
    private String address;
    private String email;
    private String password;
    private byte[] profilePicture; 
    private String role;
    private String status;
    private String reason; // For inactive status
    private long managerId; // Changed to long for consistency with BIGSERIAL
    private String staffIdPrefix;
    
    // Extra field for display (fetched via JOIN)
    private String managerName;

    public Staff() {}

    // Getters and Setters
    public long getStaffId() { return staffId; }
    public void setStaffId(long staffId) { this.staffId = staffId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public byte[] getProfilePicture() { return profilePicture; }
    public void setProfilePicture(byte[] profilePicture) { this.profilePicture = profilePicture; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public long getManagerId() { return managerId; }
    public void setManagerId(long managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    
    public String getStaffIdPrefix() {
        return staffIdPrefix;
    }
    
    public void setStaffIdPrefix(String staffIdPrefix) {
        this.staffIdPrefix = staffIdPrefix;
    }

    // Method to return formatted staff ID with prefix
    public String getFormattedStaffId() {
        if (staffIdPrefix != null && !staffIdPrefix.isEmpty()) {
            return String.format("%s%02d", staffIdPrefix, staffId);
        }
        return String.valueOf(staffId);
    }
}
