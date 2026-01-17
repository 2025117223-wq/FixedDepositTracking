package Bean;

public class Staff {

    private Long staffID;        // Changed to Long
    private String staffName;
    private String staffPhone;
    private String staffAddress;
    private String staffEmail;
    private String staffRole;
    private String password;
    private byte[] staffPicture;  
    private String staffStatus;
    private Long managerID;      // Changed to Long

    public Staff() {}

    public Long getStaffID() {
        return staffID;  // Changed to return Long
    }

    public void setStaffID(Long staffID) {  // Changed to accept Long
        this.staffID = staffID;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffPhone() {
        return staffPhone;
    }

    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }

    public String getStaffAddress() {
        return staffAddress;
    }

    public void setStaffAddress(String staffAddress) {
        this.staffAddress = staffAddress;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }

    public String getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(String staffRole) {
        this.staffRole = staffRole;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getStaffPicture() {
        return staffPicture;
    }

    public void setStaffPicture(byte[] staffPicture) {
        this.staffPicture = staffPicture;
    }

    public String getStaffStatus() {
        return staffStatus;
    }

    public void setStaffStatus(String staffStatus) {
        this.staffStatus = staffStatus;
    }

    public Long getManagerID() {   // Changed to return Long
        return managerID;
    }

    public void setManagerID(Long managerID) {  // Changed to accept Long
        this.managerID = managerID;
    }
}
