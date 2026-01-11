package DAO;

import Bean.Staff;
import Utill.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Blob;

public class StaffDAO {

    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM Staff WHERE staffEmail = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean insertStaff(Staff s) throws SQLException {
        String sql = "INSERT INTO Staff (staffName, staffPhone, staffAddress, staffEmail, staffRole, password, staffPicture) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getStaffName());
            ps.setString(2, s.getStaffPhone());
            ps.setString(3, s.getStaffAddress());
            ps.setString(4, s.getStaffEmail().trim().toLowerCase());
            ps.setString(5, s.getStaffRole());
            ps.setString(6, s.getPassword());
            ps.setBlob(7, s.getStaffPicture());

            return ps.executeUpdate() > 0;
        }
    }

    public Staff login(String email, String password) throws SQLException {
        String sql = "SELECT staffID, staffName, staffEmail, staffRole, staffStatus "
                   + "FROM Staff WHERE staffEmail = ? AND password = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Staff s = new Staff();
                    s.setStaffID(rs.getInt("staffID"));
                    s.setStaffName(rs.getString("staffName"));
                    s.setStaffEmail(rs.getString("staffEmail"));
                    s.setStaffRole(rs.getString("staffRole"));
                    s.setStaffStatus(rs.getString("staffStatus"));
                    return s;
                }
            }
        }
        return null;
    }

    public byte[] getStaffPictureById(int staffID) throws SQLException {
        String sql = "SELECT staffPicture FROM Staff WHERE staffID = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Blob blob = rs.getBlob("staffPicture");
                    if (blob != null) {
                        return blob.getBytes(1, (int) blob.length());
                    }
                }
            }
        }
        return null;
    }
}
package DAO;

import Bean.Staff;
import Utill.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Blob;

public class StaffDAO {

    public boolean isEmailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM Staff WHERE staffEmail = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean insertStaff(Staff s) throws SQLException {
        String sql = "INSERT INTO Staff (staffName, staffPhone, staffAddress, staffEmail, staffRole, password, staffPicture) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getStaffName());
            ps.setString(2, s.getStaffPhone());
            ps.setString(3, s.getStaffAddress());
            ps.setString(4, s.getStaffEmail().trim().toLowerCase());
            ps.setString(5, s.getStaffRole());
            ps.setString(6, s.getPassword());
            ps.setBlob(7, s.getStaffPicture());

            return ps.executeUpdate() > 0;
        }
    }

    public Staff login(String email, String password) throws SQLException {
        String sql = "SELECT staffID, staffName, staffEmail, staffRole, staffStatus "
                   + "FROM Staff WHERE staffEmail = ? AND password = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email.trim().toLowerCase());
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Staff s = new Staff();
                    s.setStaffID(rs.getInt("staffID"));
                    s.setStaffName(rs.getString("staffName"));
                    s.setStaffEmail(rs.getString("staffEmail"));
                    s.setStaffRole(rs.getString("staffRole"));
                    s.setStaffStatus(rs.getString("staffStatus"));
                    return s;
                }
            }
        }
        return null;
    }

    public byte[] getStaffPictureById(int staffID) throws SQLException {
        String sql = "SELECT staffPicture FROM Staff WHERE staffID = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, staffID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Blob blob = rs.getBlob("staffPicture");
                    if (blob != null) {
                        return blob.getBytes(1, (int) blob.length());
                    }
                }
            }
        }
        return null;
    }
}
