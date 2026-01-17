package DAO;
import Utill.DBConn;
import Bean.Bank;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankDAO {

    public boolean insertBank(Bank bank) throws SQLException {
        String sql = "INSERT INTO Bank (bankName, bankAddress, bankPhone) VALUES (?, ?, ?)";
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bank.getBankName());
            ps.setString(2, bank.getBankAddress());
            ps.setString(3, bank.getBankPhone());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Bank> getAllBanks() throws SQLException {
        List<Bank> banks = new ArrayList<>();
        String sql = "SELECT * FROM Bank ORDER BY bankId DESC";
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                banks.add(new Bank(rs.getInt("bankId"), rs.getString("bankName"), 
                          rs.getString("bankAddress"), rs.getString("bankPhone")));
            }
        }
        return banks;
    }

    public Bank getBankById(int id) throws SQLException {
        String sql = "SELECT * FROM Bank WHERE bankId = ?";
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Bank(rs.getInt("bankId"), rs.getString("bankName"), 
                                    rs.getString("bankAddress"), rs.getString("bankPhone"));
                }
            }
        }
        return null;
    }

    public boolean updateBank(Bank bank) throws SQLException {
        String sql = "UPDATE Bank SET bankAddress = ?, bankPhone = ? WHERE bankId = ?";
        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bank.getBankAddress());
            ps.setString(2, bank.getBankPhone());
            ps.setInt(3, bank.getBankId());
            return ps.executeUpdate() > 0;
        }
    }
}