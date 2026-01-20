package DAO;

import Util.DBConn;
import Bean.Bank;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankDAO {

    public boolean insertBank(Bank bank) throws SQLException {
        String sql = "INSERT INTO bank (bankname, bankaddress, bankphone) VALUES (?, ?, ?)";

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
        String sql = "SELECT bankid, bankname, bankaddress, bankphone FROM bank ORDER BY bankid DESC";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                banks.add(new Bank(
                        rs.getInt("bankid"),        // boleh tukar ke getLong
                        rs.getString("bankname"),
                        rs.getString("bankaddress"),
                        rs.getString("bankphone")
                ));
            }
        }
        return banks;
    }

    public Bank getBankById(int id) throws SQLException {
        String sql = "SELECT bankid, bankname, bankaddress, bankphone FROM bank WHERE bankid = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Bank(
                            rs.getInt("bankid"),
                            rs.getString("bankname"),
                            rs.getString("bankaddress"),
                            rs.getString("bankphone")
                    );
                }
            }
        }
        return null;
    }

    public boolean updateBank(Bank bank) throws SQLException {
        String sql = "UPDATE bank SET bankaddress = ?, bankphone = ? WHERE bankid = ?";

        try (Connection conn = DBConn.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bank.getBankAddress());
            ps.setString(2, bank.getBankPhone());
            ps.setInt(3, bank.getBankId());

            return ps.executeUpdate() > 0;
        }
    }
}
