package DAO;

import Util.DBConn;
import Bean.Bank;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankDAO {

    // Insert a new bank record
    public boolean insertBank(Bank bank) throws SQLException {
        String sql = "INSERT INTO bank (bankname, bankaddress, bankphone) VALUES (?, ?, ?)";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bank.getBankName());
            ps.setString(2, bank.getBankAddress());
            ps.setString(3, bank.getBankPhone());

            return ps.executeUpdate() > 0; // Returns true if the row is inserted
        }
    }

    // Retrieve all bank records
    public List<Bank> getAllBanks() throws SQLException {
        List<Bank> banks = new ArrayList<>();
        String sql = "SELECT bankid, bankname, bankaddress, bankphone FROM bank ORDER BY bankid DESC";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                banks.add(new Bank(
                        rs.getLong("bankid"),        // Change to getLong for BIGINT
                        rs.getString("bankname"),
                        rs.getString("bankaddress"),
                        rs.getString("bankphone")
                ));
            }
        }
        return banks;
    }

    // Retrieve a bank by its ID
    public Bank getBankById(long id) throws SQLException {
        String sql = "SELECT bankid, bankname, bankaddress, bankphone FROM bank WHERE bankid = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);  // Change to setLong for BIGINT

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Bank(
                            rs.getLong("bankid"),    // Change to getLong for BIGINT
                            rs.getString("bankname"),
                            rs.getString("bankaddress"),
                            rs.getString("bankphone")
                    );
                }
            }
        }
        return null; // Return null if no bank is found with the given ID
    }

    // Update an existing bank record
    public boolean updateBank(Bank bank) throws SQLException {
        String sql = "UPDATE bank SET bankaddress = ?, bankphone = ? WHERE bankid = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bank.getBankAddress());
            ps.setString(2, bank.getBankPhone());
            ps.setLong(3, bank.getBankId());  // Change to setLong for BIGINT

            return ps.executeUpdate() > 0; // Returns true if the row is updated
        }
    }
}
