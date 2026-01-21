package com.fd.dao;

import com.fd.model.FixedDepositTransaction;
import com.fd.util.DBConnection;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for FIXEDDEPOSITTRANSACTION bridge table
 * PostgreSQL Version - Handles all FD transaction tracking operations
 * Supports CALCTOTALPROFIT and WITHDRAWAMOUNT columns
 */
public class FixedDepositTransactionDAO {

    /**
     * Record a new FD transaction with full support for all columns
     * @param fdID The fixed deposit ID
     * @param staffID The staff who performed the action
     * @param transactionType Type of transaction: CREATE, WITHDRAW, REINVEST, UPDATE
     * @param calcTotalProfit Total profit calculated (for CREATE transactions)
     * @param withdrawAmount Amount withdrawn (for WITHDRAW transactions)
     * @return true if successful, false otherwise
     */
    public boolean recordTransaction(int fdID, int staffID, String transactionType, 
                                    BigDecimal calcTotalProfit, BigDecimal withdrawAmount) {
        String sql = "INSERT INTO FIXEDDEPOSITTRANSACTION " +
                     "(FDID, STAFFID, TRANSACTIONTYPE, CALCTOTALPROFIT, WITHDRAWAMOUNT) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            pstmt.setInt(2, staffID);
            pstmt.setString(3, transactionType);
            
            // Set CALCTOTALPROFIT (NULL for non-CREATE transactions)
            if (calcTotalProfit != null) {
                pstmt.setBigDecimal(4, calcTotalProfit);
            } else {
                pstmt.setNull(4, Types.NUMERIC);
            }
            
            // Set WITHDRAWAMOUNT (NULL for non-WITHDRAW transactions)
            if (withdrawAmount != null) {
                pstmt.setBigDecimal(5, withdrawAmount);
            } else {
                pstmt.setNull(5, Types.NUMERIC);
            }
            
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
     * Record a CREATE transaction with total profit
     * Convenience method for creating FDs
     */
    public boolean recordCreateTransaction(int fdID, int staffID, BigDecimal totalProfit) {
        return recordTransaction(fdID, staffID, "CREATE", totalProfit, null);
    }
    
    /**
     * Record a WITHDRAW transaction with withdraw amount
     * Convenience method for withdrawals
     */
    public boolean recordWithdrawTransaction(int fdID, int staffID, BigDecimal withdrawAmount) {
        return recordTransaction(fdID, staffID, "WITHDRAW", null, withdrawAmount);
    }
    
    /**
     * Record a simple transaction (UPDATE, REINVEST, etc.)
     * Convenience method for transactions without profit/withdraw amounts
     */
    public boolean recordSimpleTransaction(int fdID, int staffID, String transactionType) {
        return recordTransaction(fdID, staffID, transactionType, null, null);
    }

    /**
     * Get all transactions for a specific FD
     * @param fdID The fixed deposit ID
     * @return List of transactions
     */
    public List<FixedDepositTransaction> getTransactionsByFD(int fdID) {
        List<FixedDepositTransaction> transactions = new ArrayList<>();
        String sql = "SELECT TRANSACTIONID, FDID, STAFFID, TRANSACTIONDATE, " +
                     "CALCTOTALPROFIT, TRANSACTIONTYPE, WITHDRAWAMOUNT " +
                     "FROM FIXEDDEPOSITTRANSACTION " +
                     "WHERE FDID = ? " +
                     "ORDER BY TRANSACTIONDATE DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                FixedDepositTransaction transaction = new FixedDepositTransaction(
                    rs.getInt("TRANSACTIONID"),
                    rs.getInt("FDID"),
                    rs.getInt("STAFFID"),
                    rs.getString("TRANSACTIONTYPE"),
                    rs.getTimestamp("TRANSACTIONDATE"),
                    "" // remarks - you can add this column if needed
                );
                
                // Note: If you need CALCTOTALPROFIT and WITHDRAWAMOUNT in your model,
                // add these fields to FixedDepositTransaction.java:
                // BigDecimal calcTotalProfit = rs.getBigDecimal("CALCTOTALPROFIT");
                // BigDecimal withdrawAmount = rs.getBigDecimal("WITHDRAWAMOUNT");
                
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
        String sql = "SELECT TRANSACTIONID, FDID, STAFFID, TRANSACTIONDATE, " +
                     "CALCTOTALPROFIT, TRANSACTIONTYPE, WITHDRAWAMOUNT " +
                     "FROM FIXEDDEPOSITTRANSACTION " +
                     "WHERE STAFFID = ? " +
                     "ORDER BY TRANSACTIONDATE DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, staffID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                FixedDepositTransaction transaction = new FixedDepositTransaction(
                    rs.getInt("TRANSACTIONID"),
                    rs.getInt("FDID"),
                    rs.getInt("STAFFID"),
                    rs.getString("TRANSACTIONTYPE"),
                    rs.getTimestamp("TRANSACTIONDATE"),
                    ""
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
     * PostgreSQL version using LIMIT instead of FETCH FIRST
     * @param fdID The fixed deposit ID
     * @return staffID of creator, or -1 if not found
     */
    public int getCreatorStaffID(int fdID) {
        String sql = "SELECT STAFFID FROM FIXEDDEPOSITTRANSACTION " +
                     "WHERE FDID = ? AND TRANSACTIONTYPE = 'CREATE' " +
                     "ORDER BY TRANSACTIONDATE ASC LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("STAFFID");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting creator for FD" + fdID + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1; // Not found
    }

    /**
     * Get transaction history with staff names (JOIN query)
     * PostgreSQL version with correct column names and profit/withdraw info
     * @param fdID The fixed deposit ID
     * @return List of transaction details with staff names
     */
    public List<String> getTransactionHistoryWithStaffNames(int fdID) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT ft.TRANSACTIONTYPE, ft.TRANSACTIONDATE, " +
                     "ft.CALCTOTALPROFIT, ft.WITHDRAWAMOUNT, s.STAFFNAME " +
                     "FROM FIXEDDEPOSITTRANSACTION ft " +
                     "JOIN STAFF s ON ft.STAFFID = s.STAFFID " +
                     "WHERE ft.FDID = ? " +
                     "ORDER BY ft.TRANSACTIONDATE DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                StringBuilder entry = new StringBuilder();
                entry.append(rs.getTimestamp("TRANSACTIONDATE")).append(" - ");
                entry.append(rs.getString("TRANSACTIONTYPE")).append(" by ");
                entry.append(rs.getString("STAFFNAME"));
                
                // Add profit info for CREATE transactions
                BigDecimal profit = rs.getBigDecimal("CALCTOTALPROFIT");
                if (profit != null) {
                    entry.append(" (Total Profit: RM ").append(String.format("%,.2f", profit)).append(")");
                }
                
                // Add withdraw info for WITHDRAW transactions
                BigDecimal withdraw = rs.getBigDecimal("WITHDRAWAMOUNT");
                if (withdraw != null) {
                    entry.append(" (Withdrawn: RM ").append(String.format("%,.2f", withdraw)).append(")");
                }
                
                history.add(entry.toString());
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
        String sql = "SELECT COUNT(*) as count FROM FIXEDDEPOSITTRANSACTION " +
                     "WHERE FDID = ? AND TRANSACTIONTYPE = 'WITHDRAW'";
        
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
        String sql = "SELECT COUNT(*) as count FROM FIXEDDEPOSITTRANSACTION " +
                     "WHERE FDID = ? AND TRANSACTIONTYPE = 'REINVEST'";
        
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
    
    /**
     * Get total profit for an FD from CREATE transaction
     * @param fdID The fixed deposit ID
     * @return Total profit or null if not found
     */
    public BigDecimal getTotalProfitForFD(int fdID) {
        String sql = "SELECT CALCTOTALPROFIT FROM FIXEDDEPOSITTRANSACTION " +
                     "WHERE FDID = ? AND TRANSACTIONTYPE = 'CREATE' LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal("CALCTOTALPROFIT");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting total profit: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get total withdrawn amount for an FD
     * @param fdID The fixed deposit ID
     * @return Total withdrawn amount or 0 if none
     */
    public BigDecimal getTotalWithdrawnForFD(int fdID) {
        String sql = "SELECT SUM(WITHDRAWAMOUNT) as total FROM FIXEDDEPOSITTRANSACTION " +
                     "WHERE FDID = ? AND TRANSACTIONTYPE = 'WITHDRAW'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return (total != null) ? total : BigDecimal.ZERO;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting total withdrawn: " + e.getMessage());
            e.printStackTrace();
        }
        
        return BigDecimal.ZERO;
    }
}
