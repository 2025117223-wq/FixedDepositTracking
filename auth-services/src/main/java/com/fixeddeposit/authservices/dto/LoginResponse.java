package c.f.authservice.dto;

public class LoginResponse {
    public boolean success;
    public Long staffId;
    public String staffName;
    public String staffEmail;
    public String staffRole;
    public String staffStatus;
    public Long managerId;
    public String message;

    public static LoginResponse ok(Long id, String name, String email, String role, String status, Long managerId) {
        LoginResponse r = new LoginResponse();
        r.success = true;
        r.staffId = id;
        r.staffName = name;
        r.staffEmail = email;
        r.staffRole = role;
        r.staffStatus = status;
        r.managerId = managerId;
        return r;
    }

    public static LoginResponse fail(String msg) {
        LoginResponse r = new LoginResponse();
        r.success = false;
        r.message = msg;
        return r;
    }
}
