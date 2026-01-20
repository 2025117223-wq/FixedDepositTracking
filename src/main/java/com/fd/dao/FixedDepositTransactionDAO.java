package com.fd.dao;

import com.fd.model.FixedDepositTransaction;
import com.fd.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for FixedDepositTransaction bridge table
 * Handles all FD transaction tracking operations
 */
public class FixedDepositTransactionDAO {

    /**
     * Record a new FD transaction (CREATE, WITHDRAW, REINVEST, UPDATE)
     * @param fdID The fixed deposit ID
     * @param staffID The staff who performed the action
     * @param transactionType Type of transaction: CREATE, WITHDRAW, REINVEST, UPDATE
     * @param remarks Optional remarks/notes
     * @return true if successful, false otherwise
     */
    public boolean recordTransaction(int fdID, int staffID, String transactionType, String remarks) {
        String sql = "INSERT INTO FixedDepositTransaction (fdID, staffID, transactionType, remarks) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            pstmt.setInt(2, staffID);
            pstmt.setString(3, transactionType);
            pstmt.setString(4, remarks);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Transaction recorded: FD" + fdID + " - " + transactionType + " by Staff" + staffID);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error recording FD transaction: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Get all transactions for a specific FD
     * @param fdID The fixed deposit ID
     * @return List of transactions
     */
    public List<FixedDepositTransaction> getTransactionsByFD(int fdID) {
        List<FixedDepositTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM FixedDepositTransaction WHERE fdID = ? ORDER BY transactionDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                FixedDepositTransaction transaction = new FixedDepositTransaction(
                    rs.getInt("transactionID"),
                    rs.getInt("fdID"),
                    rs.getInt("staffID"),
                    rs.getString("transactionType"),
                    rs.getTimestamp("transactionDate"),
                    rs.getString("remarks")
                );
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching transactions for FD" + fdID + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }

    /**
     * Get all transactions performed by a specific staff member
     * @param staffID The staff ID
     * @return List of transactions
     */
    public List<FixedDepositTransaction> getTransactionsByStaff(int staffID) {
        List<FixedDepositTransaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM FixedDepositTransaction WHERE staffID = ? ORDER BY transactionDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                FixedDepositTransaction transaction = new FixedDepositTransaction(
                    rs.getInt("transactionID"),
                    rs.getInt("fdID"),
                    rs.getInt("staffID"),
                    rs.getString("transactionType"),
                    rs.getTimestamp("transactionDate"),
                    rs.getString("remarks")
                );
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching transactions for Staff" + staffID + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }

    /**
     * Get the staff who created a specific FD
     * @param fdID The fixed deposit ID
     * @return staffID of creator, or -1 if not found
     */
    public int getCreatorStaffID(int fdID) {
        String sql = "SELECT staffID FROM FixedDepositTransaction " +
                     "WHERE fdID = ? AND transactionType = 'CREATE' " +
                     "ORDER BY transactionDate ASC FETCH FIRST 1 ROW ONLY";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("staffID");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting creator for FD" + fdID + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1; // Not found
    }

    /**
     * Get transaction history with staff names (JOIN query)
     * @param fdID The fixed deposit ID
     * @return List of transaction details with staff names
     */
    public List<String> getTransactionHistoryWithStaffNames(int fdID) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT ft.transactionType, ft.transactionDate, ft.remarks, " +
                     "s.staffName " +
                     "FROM FixedDepositTransaction ft " +
                     "JOIN Staff s ON ft.staffID = s.staffID " +
                     "WHERE ft.fdID = ? " +
                     "ORDER BY ft.transactionDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String entry = String.format("%s - %s by %s (%s)",
                    rs.getTimestamp("transactionDate"),
                    rs.getString("transactionType"),
                    rs.getString("staffName"),
                    rs.getString("remarks") != null ? rs.getString("remarks") : "No remarks"
                );
                history.add(entry);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting transaction history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return history;
    }

    /**
     * Check if an FD has been withdrawn
     * @param fdID The fixed deposit ID
     * @return true if withdrawn, false otherwise
     */
    public boolean isWithdrawn(int fdID) {
        String sql = "SELECT COUNT(*) as count FROM FixedDepositTransaction " +
                     "WHERE fdID = ? AND transactionType = 'WITHDRAW'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking withdrawal status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Check if an FD has been reinvested
     * @param fdID The fixed deposit ID
     * @return true if reinvested, false otherwise
     */
    public boolean isReinvested(int fdID) {
        String sql = "SELECT COUNT(*) as count FROM FixedDepositTransaction " +
                     "WHERE fdID = ? AND transactionType = 'REINVEST'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking reinvestment status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
