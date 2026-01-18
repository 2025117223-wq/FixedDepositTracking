package com.fixeddeposit.authservice.model;

import jakarta.persistence.*;

@Entity
@Table(name="staff")
public class Staff {
    @Id
    @Column(name="staffid")
    private Integer staffid;

    @Column(name="staffemail")
    private String staffemail;

    @Column(name="password")
    private String password;

    @Column(name="staffstatus")
    private String staffstatus;

    public Integer getStaffid() { return staffid; }
    public String getStaffemail() { return staffemail; }
    public String getPassword() { return password; }
    public String getStaffstatus() { return staffstatus; }
}
