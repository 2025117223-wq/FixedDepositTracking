package Bean;

import java.io.InputStream;

public class Staff {
    private int staffID;
    private String staffName;
    private String staffPhone;
    private String staffAddress;
    private String staffEmail;
    private String staffRole;
    private String password;
    private InputStream staffPicture;

    private String staffStatus;

    public Staff() {}

    public int getStaffID() { return staffID; }
    public void setStaffID(int staffID) { this.staffID = staffID; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getStaffPhone() { return staffPhone; }
    public void setStaffPhone(String staffPhone) { this.staffPhone = staffPhone; }

    public String getStaffAddress() { return staffAddress; }
    public void setStaffAddress(String staffAddress) { this.staffAddress = staffAddress; }

    public String getStaffEmail() { return staffEmail; }
    public void setStaffEmail(String staffEmail) { this.staffEmail = staffEmail; }

    public String getStaffRole() { return staffRole; }
    public void setStaffRole(String staffRole) { this.staffRole = staffRole; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public InputStream getStaffPicture() { return staffPicture; }
    public void setStaffPicture(InputStream staffPicture) { this.staffPicture = staffPicture; }

    public String getStaffStatus() { return staffStatus; }
    public void setStaffStatus(String staffStatus) { this.staffStatus = staffStatus; }
}
