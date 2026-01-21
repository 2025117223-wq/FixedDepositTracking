package com.fd.dao;

import com.fd.util.DBConnection;
import com.fd.model.FixedDepositRecord;

import java.io.InputStream;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FixedDeposit Data Access Object (DAO) Class
 * Handles database operations for FixedDepositRecord, FreeFD, and PledgeFD
 * Updated for Oracle Database with reminder columns in FIXEDDEPOSITRECORD table
 */
public class FixedDepositDAO {
    
    // ==================== READ OPERATIONS ====================
    
    /**
     * Get all Fixed Deposit records with Bank name
     */
    public List<FixedDepositRecord> getAllFixedDeposits() {
        List<FixedDepositRecord> fdList = new ArrayList<>();
        
        String sql = "SELECT f.fdID, f.accNumber, f.referralNumber, f.depositAmount, " +
                     "f.interestRt, f.startDate, f.tenure, f.maturityDate, f.certNo, " +
                     "f.fdType, f.status, f.bankID, b.bankName, " +
                     "f.reminderMaturity, f.reminderIncomplete, f.REMAININGBALANCE, f.TOTALWITHDRAWN " +
                     "FROM FixedDepositRecord f " +
                     "LEFT JOIN Bank b ON f.bankID = b.bankID " +
                     "ORDER BY f.fdID DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                FixedDepositRecord fd = mapResultSetToFD(rs);
                fdList.add(fd);
            }
            
            System.out.println("Retrieved " + fdList.size() + " Fixed Deposit records.");
            
        } catch (SQLException e) {
            System.err.println("Error getting all fixed deposits: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fdList;
    }
    
    /**
     * Get Fixed Deposit by ID
     */
    public FixedDepositRecord getFixedDepositById(int fdID) {
        String sql = "SELECT f.fdID, f.accNumber, f.referralNumber, f.depositAmount, " +
                     "f.interestRt, f.startDate, f.tenure, f.maturityDate, f.certNo, " +
                     "f.fdType, f.status, f.bankID, b.bankName, " +
                     "f.reminderMaturity, f.reminderIncomplete, f.REMAININGBALANCE, f.TOTALWITHDRAWN " +
                     "FROM FixedDepositRecord f " +
                     "LEFT JOIN Bank b ON f.bankID = b.bankID " +
                     "WHERE f.fdID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFD(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting fixed deposit by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get FreeFD data by fdID
     */
    public Map<String, String> getFreeFDData(int fdID) {
        String sql = "SELECT autoRenewalStatus, withdrawableStatus FROM FreeFD WHERE fdID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, String> data = new HashMap<>();
                    String autoRenewal = rs.getString("autoRenewalStatus");
                    String withdrawable = rs.getString("withdrawableStatus");
                    
                    // Convert Y/N to Yes/No for display
                    data.put("autoRenewalStatus", "Y".equals(autoRenewal) ? "Yes" : "No");
                data.put("withdrawableStatus", withdrawable);
                    
                    return data;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting FreeFD data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get PledgeFD data by fdID
     */
    public Map<String, Object> getPledgeFDData(int fdID) {
        String sql = "SELECT collateralStatus, pledgeValue FROM PledgeFD WHERE fdID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> data = new HashMap<>();
                    String collateral = rs.getString("collateralStatus");
                    BigDecimal pledgeValue = rs.getBigDecimal("pledgeValue");
                    
                    // Convert Y/N to Active/Partial for display
                    data.put("collateralStatus", "Y".equals(collateral) ? "Active" : "Partial");
                    data.put("pledgeValue", pledgeValue);
                    
                    return data;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting PledgeFD data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get reminder settings for a Fixed Deposit
     * Note: Reminder settings are stored in FixedDepositRecord table
     */
    public Map<String, String> getReminderSettings(int fdID) {
        String sql = "SELECT reminderMaturity, reminderIncomplete FROM FixedDepositRecord WHERE fdID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, String> data = new HashMap<>();
                    String maturity = rs.getString("reminderMaturity");
                    String incomplete = rs.getString("reminderIncomplete");
                    
                    // Convert Y/N to on/off for checkbox display
                    data.put("reminderMaturity", "Y".equals(maturity) ? "on" : "off");
                    data.put("reminderIncomplete", "Y".equals(incomplete) ? "on" : "off");
                    
                    return data;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting reminder settings: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    // ==================== CREATE OPERATIONS ====================
    
    /**
     * Insert new Fixed Deposit Record
     * Updated to include reminder settings
     */
    public int insertFixedDeposit(String accNumber, String referralNumber, BigDecimal depositAmount,
                                   BigDecimal interestRate, Date startDate, int tenure, Date maturityDate,
                                   InputStream fdCertStream, String certNo, String fdType, int bankID,
                                   String reminderMaturity, String reminderIncomplete) {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedFdID = -1;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String sql = "INSERT INTO FixedDepositRecord (accNumber, referralNumber, depositAmount, " +
                        "interestRt, startDate, tenure, maturityDate, fdCert, certNo, fdType, status, bankID, " +
                        "reminderMaturity, reminderIncomplete, remainingBalance, totalWithdrawn) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', ?, ?, ?, ?, 0)";
            
            pstmt = conn.prepareStatement(sql, new String[]{"fdID"});
            
            pstmt.setString(1, accNumber);
            
            if (referralNumber != null && !referralNumber.trim().isEmpty()) {
                pstmt.setBigDecimal(2, new BigDecimal(referralNumber));
            } else {
                pstmt.setNull(2, Types.NUMERIC);
            }
            
            pstmt.setBigDecimal(3, depositAmount);
            pstmt.setBigDecimal(4, interestRate);
            pstmt.setDate(5, startDate);
            pstmt.setInt(6, tenure);
            pstmt.setDate(7, maturityDate);
            
            if (fdCertStream != null) {
                pstmt.setBlob(8, fdCertStream);
            } else {
                pstmt.setNull(8, Types.BLOB);
            }
            
            if (certNo != null && !certNo.trim().isEmpty()) {
                pstmt.setString(9, certNo);
            } else {
                pstmt.setNull(9, Types.VARCHAR);
            }
            
            pstmt.setString(10, fdType);
            pstmt.setInt(11, bankID);
            
            // Set reminder settings (convert "on" to "Y", anything else to "N")
            pstmt.setString(12, "on".equals(reminderMaturity) ? "Y" : "N");
            pstmt.setString(13, "on".equals(reminderIncomplete) ? "Y" : "N");
            pstmt.setBigDecimal(14, depositAmount);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedFdID = rs.getInt(1);
                }
            }
            
            conn.commit();
            System.out.println("Fixed Deposit inserted successfully with ID: " + generatedFdID);
            System.out.println("Reminder settings - Maturity: " + reminderMaturity + ", Incomplete: " + reminderIncomplete);
            
        } catch (SQLException e) {
            System.err.println("Error inserting fixed deposit: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return generatedFdID;
    }
    
    /**
     * Insert FreeFD record
     */
    public boolean insertFreeFD(int fdID, String autoRenewalStatus, String withdrawableStatus) {
        String sql = "INSERT INTO FreeFD (fdID, autoRenewalStatus, withdrawableStatus) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            pstmt.setString(2, autoRenewalStatus);
            pstmt.setString(3, withdrawableStatus);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("FreeFD record inserted for FD" + fdID);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting FreeFD: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Insert PledgeFD record
     */
    public boolean insertPledgeFD(int fdID, String collateralStatus, BigDecimal pledgeValue) {
        String sql = "INSERT INTO PledgeFD (fdID, collateralStatus, pledgeValue) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            pstmt.setString(2, collateralStatus);
            
            if (pledgeValue != null) {
                pstmt.setBigDecimal(3, pledgeValue);
            } else {
                pstmt.setNull(3, Types.NUMERIC);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("PledgeFD record inserted for FD" + fdID);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting PledgeFD: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== UPDATE OPERATIONS ====================
    
    /**
     * Update Fixed Deposit Record
     * Updated to include reminder settings
     */
    public boolean updateFixedDeposit(int fdID, String accNumber, String referralNumber,
                                      BigDecimal depositAmount, BigDecimal interestRate,
                                      Date startDate, int tenure, Date maturityDate,
                                      InputStream fdCertStream, String certNo, String fdType,
                                      String status, int bankID, String reminderMaturity, 
                                      String reminderIncomplete) {
        
        StringBuilder sql = new StringBuilder("UPDATE FixedDepositRecord SET ");
        sql.append("accNumber = ?, referralNumber = ?, depositAmount = ?, interestRt = ?, ");
        sql.append("startDate = ?, tenure = ?, maturityDate = ?, ");
        
        boolean updateCert = (fdCertStream != null);
        if (updateCert) {
            sql.append("fdCert = ?, ");
        }
        
        sql.append("certNo = ?, fdType = ?, status = ?, bankID = ?, ");
        sql.append("reminderMaturity = ?, reminderIncomplete = ? WHERE fdID = ?");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            pstmt.setString(paramIndex++, accNumber);
            
            if (referralNumber != null && !referralNumber.trim().isEmpty()) {
                pstmt.setBigDecimal(paramIndex++, new BigDecimal(referralNumber));
            } else {
                pstmt.setNull(paramIndex++, Types.NUMERIC);
            }
            
            pstmt.setBigDecimal(paramIndex++, depositAmount);
            pstmt.setBigDecimal(paramIndex++, interestRate);
            pstmt.setDate(paramIndex++, startDate);
            pstmt.setInt(paramIndex++, tenure);
            pstmt.setDate(paramIndex++, maturityDate);
            
            if (updateCert) {
                pstmt.setBlob(paramIndex++, fdCertStream);
            }
            
            if (certNo != null && !certNo.trim().isEmpty()) {
                pstmt.setString(paramIndex++, certNo);
            } else {
                pstmt.setNull(paramIndex++, Types.VARCHAR);
            }
            
            pstmt.setString(paramIndex++, fdType);
            pstmt.setString(paramIndex++, status);
            pstmt.setInt(paramIndex++, bankID);
            
            // Set reminder settings (convert "on" to "Y", anything else to "N")
            pstmt.setString(paramIndex++, "on".equals(reminderMaturity) ? "Y" : "N");
            pstmt.setString(paramIndex++, "on".equals(reminderIncomplete) ? "Y" : "N");
            
            pstmt.setInt(paramIndex, fdID);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Fixed Deposit FD" + fdID + " updated. Rows affected: " + rowsAffected);
            System.out.println("Reminder settings - Maturity: " + reminderMaturity + ", Incomplete: " + reminderIncomplete);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating fixed deposit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update FD balance when status changes to MATURED
     */
    public boolean updateFDBalance(int fdID, BigDecimal remainingBalance) {
        String sql = "UPDATE FixedDepositRecord SET REMAININGBALANCE = ? WHERE fdID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, remainingBalance);
            pstmt.setInt(2, fdID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update FreeFD record
     */
    public boolean updateFreeFD(int fdID, String autoRenewalStatus, String withdrawableStatus) {
        // First check if record exists
        String checkSql = "SELECT COUNT(*) FROM FreeFD WHERE fdID = ?";
        String updateSql = "UPDATE FreeFD SET autoRenewalStatus = ?, withdrawableStatus = ? WHERE fdID = ?";
        String insertSql = "INSERT INTO FreeFD (fdID, autoRenewalStatus, withdrawableStatus) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection()) {
            
            // Check if exists
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, fdID);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        exists = rs.getInt(1) > 0;
                    }
                }
            }
            
            if (exists) {
                // Update existing
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setString(1, autoRenewalStatus);
                    pstmt.setString(2, withdrawableStatus);
                    pstmt.setInt(3, fdID);
                    return pstmt.executeUpdate() > 0;
                }
            } else {
                // Insert new
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, fdID);
                    pstmt.setString(2, autoRenewalStatus);
                    pstmt.setString(3, withdrawableStatus);
                    return pstmt.executeUpdate() > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating FreeFD: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update PledgeFD record
     */
    public boolean updatePledgeFD(int fdID, String collateralStatus, BigDecimal pledgeValue) {
        String checkSql = "SELECT COUNT(*) FROM PledgeFD WHERE fdID = ?";
        String updateSql = "UPDATE PledgeFD SET collateralStatus = ?, pledgeValue = ? WHERE fdID = ?";
        String insertSql = "INSERT INTO PledgeFD (fdID, collateralStatus, pledgeValue) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection()) {
            
            // Check if exists
            boolean exists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, fdID);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        exists = rs.getInt(1) > 0;
                    }
                }
            }
            
            if (exists) {
                // Update existing
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setString(1, collateralStatus);
                    if (pledgeValue != null) {
                        pstmt.setBigDecimal(2, pledgeValue);
                    } else {
                        pstmt.setNull(2, Types.NUMERIC);
                    }
                    pstmt.setInt(3, fdID);
                    return pstmt.executeUpdate() > 0;
                }
            } else {
                // Insert new
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, fdID);
                    pstmt.setString(2, collateralStatus);
                    if (pledgeValue != null) {
                        pstmt.setBigDecimal(3, pledgeValue);
                    } else {
                        pstmt.setNull(3, Types.NUMERIC);
                    }
                    return pstmt.executeUpdate() > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating PledgeFD: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== BANK OPERATIONS ====================
    
    /**
     * Get Bank ID by Bank Name
     */
    public int getBankIDByName(String bankName) {
        String sql = "SELECT bankID FROM Bank WHERE bankName = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bankName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("bankID");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting bank ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Get or create bank
     */
    public int getOrCreateBank(String bankName) {
        int bankID = getBankIDByName(bankName);
        
        if (bankID == -1) {
            bankID = insertBank(bankName);
        }
        
        return bankID;
    }
    
    /**
     * Insert new Bank
     */
    public int insertBank(String bankName) {
        String sql = "INSERT INTO Bank (bankName) VALUES (?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"bankID"})) {
            
            pstmt.setString(1, bankName);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int bankID = rs.getInt(1);
                        System.out.println("Bank inserted successfully with ID: " + bankID);
                        return bankID;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error inserting bank: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Map ResultSet to FixedDepositRecord object
     */
    private FixedDepositRecord mapResultSetToFD(ResultSet rs) throws SQLException {
        FixedDepositRecord fd = new FixedDepositRecord();
        fd.setFdID(rs.getInt("fdID"));
        fd.setAccNumber(rs.getString("accNumber"));
        fd.setReferralNumber(rs.getBigDecimal("referralNumber"));
        fd.setDepositAmount(rs.getBigDecimal("depositAmount"));
        fd.setInterestRate(rs.getBigDecimal("interestRt"));
        fd.setStartDate(rs.getDate("startDate"));
        fd.setTenure(rs.getInt("tenure"));
        fd.setMaturityDate(rs.getDate("maturityDate"));
        fd.setCertNo(rs.getString("certNo"));
        fd.setFdType(rs.getString("fdType"));
        fd.setStatus(rs.getString("status"));
        fd.setBankID(rs.getInt("bankID"));
        fd.setBankName(rs.getString("bankName"));
        fd.setRemainingBalance(rs.getBigDecimal("REMAININGBALANCE"));
        fd.setTotalWithdrawn(rs.getBigDecimal("TOTALWITHDRAWN"));
        return fd;
    }
    
    public boolean insertFDTransaction_Create(int fdID, int staffID, BigDecimal calcTotalProfit) {
        String sql = "INSERT INTO FixedDepositTransaction " +
                     "(fdID, staffID, transactionType, calcTotalProfit) " +
                     "VALUES (?, ?, 'CREATE', ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            pstmt.setInt(2, staffID);
            
            if (calcTotalProfit != null) {
                pstmt.setBigDecimal(3, calcTotalProfit);
            } else {
                pstmt.setNull(3, Types.NUMERIC);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Transaction record created for FD" + fdID + " (CREATE) with profit: RM " + calcTotalProfit);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error inserting FD transaction (CREATE): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean insertFDTransaction_Create(int fdID, int staffID) {
        return insertFDTransaction_Create(fdID, staffID, null);
    }


    /**
     * Insert a transaction record for WITHDRAW
     */
    public boolean insertFDTransaction_Withdraw(int fdID, int staffID, 
                                               java.sql.Timestamp transactionDate,
                                               BigDecimal withdrawAmount,
                                               BigDecimal calcTotalProfit) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Step 1: Insert transaction record
            String insertSql = "INSERT INTO FixedDepositTransaction " +
                         "(fdID, staffID, transactionDate, transactionType, withdrawAmount, calcTotalProfit) " +
                         "VALUES (?, ?, ?, 'WITHDRAW', ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, fdID);
                pstmt.setInt(2, staffID);
                pstmt.setTimestamp(3, transactionDate);
                pstmt.setBigDecimal(4, withdrawAmount);
                
                if (calcTotalProfit != null) {
                    pstmt.setBigDecimal(5, calcTotalProfit);
                } else {
                    pstmt.setNull(5, Types.NUMERIC);
                }
                
                pstmt.executeUpdate();
            }
            
            // Step 2: Update REMAININGBALANCE and TOTALWITHDRAWN in FIXEDDEPOSITRECORD
            String updateSql = "UPDATE FixedDepositRecord " +
                             "SET REMAININGBALANCE = REMAININGBALANCE - ?, " +
                             "    TOTALWITHDRAWN = COALESCE(TOTALWITHDRAWN, 0) + ? " +
                             "WHERE FDID = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setBigDecimal(1, withdrawAmount);
                pstmt.setBigDecimal(2, withdrawAmount);
                pstmt.setInt(3, fdID);
                
                
                int rowsUpdated = pstmt.executeUpdate();
                System.out.println("🔍 DEBUG UPDATE RESULT:");
                System.out.println("   Rows updated: " + rowsUpdated);
                System.out.println("   FD ID: " + fdID);
                System.out.println("   Withdraw Amount: " + withdrawAmount);
                
                if (rowsUpdated == 0) {
                    System.err.println("⚠️ WARNING: UPDATE affected 0 rows! Check FDID or column names.");
                }
            }
            
            conn.commit();
            System.out.println("✅ Withdrawal transaction completed for FD" + fdID);
            System.out.println("   Withdrawn: RM " + withdrawAmount);
            System.out.println("   Total Withdrawn updated, Remaining Balance decreased");
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Error inserting FD withdrawal: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Insert a transaction record for REINVEST
     */
    public boolean insertFDTransaction_Reinvest(int fdID, int staffID,
                                               java.sql.Timestamp transactionDate,
                                               BigDecimal calcTotalProfit) {
        String sql = "INSERT INTO FixedDepositTransaction " +
                     "(fdID, staffID, transactionDate, transactionType, calcTotalProfit) " +
                     "VALUES (?, ?, ?, 'REINVEST', ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            pstmt.setInt(2, staffID);
            pstmt.setTimestamp(3, transactionDate);
            
            if (calcTotalProfit != null) {
                pstmt.setBigDecimal(4, calcTotalProfit);
            } else {
                pstmt.setNull(4, Types.NUMERIC);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Transaction record created for FD" + fdID + " (REINVEST)");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error inserting FD transaction (REINVEST): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all transactions for a specific FD
     */
    public List<Map<String, Object>> getTransactionsByFD(int fdID) {
        List<Map<String, Object>> transactions = new ArrayList<>();
        
        String sql = "SELECT t.fdID, t.staffID, t.transactionDate, t.transactionType, " +
                     "t.withdrawAmount, t.calcTotalProfit, s.staffName " +
                     "FROM FixedDepositTransaction t " +
                     "LEFT JOIN Staff s ON t.staffID = s.staffID " +
                     "WHERE t.fdID = ? " +
                     "ORDER BY t.transactionDate DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> transaction = new HashMap<>();
                    transaction.put("fdID", rs.getInt("fdID"));
                    transaction.put("staffID", rs.getInt("staffID"));
                    transaction.put("transactionDate", rs.getTimestamp("transactionDate"));
                    transaction.put("transactionType", rs.getString("transactionType"));
                    transaction.put("withdrawAmount", rs.getBigDecimal("withdrawAmount"));
                    transaction.put("calcTotalProfit", rs.getBigDecimal("calcTotalProfit"));
                    transaction.put("staffName", rs.getString("staffName"));
                    transactions.add(transaction);
                }
            }
            
            System.out.println("Retrieved " + transactions.size() + " transactions for FD" + fdID);
            
        } catch (SQLException e) {
            System.err.println("Error getting transactions for FD: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    public int reinvestFD(int previousFdID, BigDecimal newDepositAmount, 
            BigDecimal newInterestRate, Date newStartDate, 
            int newTenure, Date newMaturityDate, int staffID) {

		Connection conn = null;
		try {
		conn = DBConnection.getConnection();
		conn.setAutoCommit(false); // Start transaction
		
		// Step 1: Update old FD status to MATURED
		String updateSql = "UPDATE FixedDepositRecord SET STATUS = 'MATURED' WHERE FDID = ?";
		PreparedStatement updateStmt = conn.prepareStatement(updateSql);
		updateStmt.setInt(1, previousFdID);
		updateStmt.executeUpdate();
		
		// Step 2: Get old FD details
		FixedDepositRecord oldFD = getFixedDepositById(previousFdID);
		
		// Step 3: Record REINVEST transaction for old FD
		String txnSql = "INSERT INTO FixedDepositTransaction " +
		              "(fdID, staffID, transactionType, calcTotalProfit) " +
		              "VALUES (?, ?, 'REINVEST', ?)";
		PreparedStatement txnStmt = conn.prepareStatement(txnSql);
		txnStmt.setInt(1, previousFdID);
		txnStmt.setInt(2, staffID);
		txnStmt.setBigDecimal(3, newDepositAmount);
		txnStmt.executeUpdate();
		
		// Step 4: Create new FD record
		String insertSql = "INSERT INTO FixedDepositRecord " +
		                 "(accNumber, depositAmount, interestRt, startDate, tenure, " +
		                 "maturityDate, fdType, status, bankID, previousFdID, " +
		                 "reminderMaturity, reminderIncomplete) " +
		                 "VALUES (?, ?, ?, ?, ?, ?, ?, 'ONGOING', ?, ?, ?, ?) " +
		                 "RETURNING fdID INTO ?";
		
		PreparedStatement insertStmt = conn.prepareStatement(insertSql);
		insertStmt.setString(1, oldFD.getAccNumber());
		insertStmt.setBigDecimal(2, newDepositAmount);
		insertStmt.setBigDecimal(3, newInterestRate);
		insertStmt.setDate(4, newStartDate);
		insertStmt.setInt(5, newTenure);
		insertStmt.setDate(6, newMaturityDate);
		insertStmt.setString(7, oldFD.getFdType());
		insertStmt.setInt(8, oldFD.getBankID());
		insertStmt.setInt(9, previousFdID); // Link to previous FD
		insertStmt.setString(10, "N"); // Default reminders
		insertStmt.setString(11, "N");
		
		ResultSet rs = insertStmt.executeQuery();
		int newFdID = -1;
		if (rs.next()) {
		   newFdID = rs.getInt(1);
		}
		
		// Step 5: Calculate profit for new FD
		BigDecimal interest = newDepositAmount.multiply(newInterestRate)
		                                    .multiply(new BigDecimal(newTenure))
		                                    .divide(new BigDecimal(1200), 2, BigDecimal.ROUND_HALF_UP);
		BigDecimal newTotalProfit = newDepositAmount.add(interest);
		
		// Step 6: Record CREATE transaction for new FD
		insertFDTransaction_Create(newFdID, staffID, newTotalProfit);
		
		// Step 7: Copy type-specific data
		if ("FREEFD".equals(oldFD.getFdType())) {
		   Map<String, String> freeData = getFreeFDData(previousFdID);
		   insertFreeFD(newFdID, freeData.get("autoRenewalStatus"), 
		               freeData.get("withdrawableStatus"));
		} else if ("PLEDGEFD".equals(oldFD.getFdType())) {
		   Map<String, Object> pledgeData = getPledgeFDData(previousFdID);
		   insertPledgeFD(newFdID, (String)pledgeData.get("collateralStatus"), 
		                 (BigDecimal)pledgeData.get("pledgeValue"));
		}
		
		conn.commit(); // Commit transaction
		System.out.println("✅ FD" + previousFdID + " reinvested as FD" + newFdID);
		return newFdID;
		
		} catch (SQLException e) {
		if (conn != null) {
		   try {
		       conn.rollback();
		   } catch (SQLException ex) {
		       ex.printStackTrace();
		   }
		}
		System.err.println("❌ Error reinvesting FD: " + e.getMessage());
		e.printStackTrace();
		return -1;
		} finally {
		DBConnection.closeConnection(conn);
		}
		}
    
    public int createReinvestedFD(int previousFDID, String accNumber, String referralNumber,
            BigDecimal depositAmount, BigDecimal interestRate,
            Date startDate, int tenure, Date maturityDate,
            InputStream fdCertStream, String certNo, String fdType, int bankID,
            String reminderMaturity, String reminderIncomplete) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int generatedFdID = -1;
		
		try {
		conn = DBConnection.getConnection();
		conn.setAutoCommit(false);
		
		// Insert new FD with previousFDID link
		String sql = "INSERT INTO FixedDepositRecord (accNumber, referralNumber, depositAmount, " +
		  "interestRt, startDate, tenure, maturityDate, fdCert, certNo, fdType, status, bankID, " +
		  "reminderMaturity, reminderIncomplete, previousFDID) " +
		  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ONGOING', ?, ?, ?, ?)";
		
		pstmt = conn.prepareStatement(sql, new String[]{"fdID"});
		
		pstmt.setString(1, accNumber);
		
		if (referralNumber != null && !referralNumber.trim().isEmpty()) {
		pstmt.setBigDecimal(2, new BigDecimal(referralNumber));
		} else {
		pstmt.setNull(2, Types.NUMERIC);
		}
		
		pstmt.setBigDecimal(3, depositAmount);
		pstmt.setBigDecimal(4, interestRate);
		pstmt.setDate(5, startDate);
		pstmt.setInt(6, tenure);
		pstmt.setDate(7, maturityDate);
		
		if (fdCertStream != null) {
		pstmt.setBlob(8, fdCertStream);
		} else {
		pstmt.setNull(8, Types.BLOB);
		}
		
		if (certNo != null && !certNo.trim().isEmpty()) {
		pstmt.setString(9, certNo);
		} else {
		pstmt.setNull(9, Types.VARCHAR);
		}
		
		pstmt.setString(10, fdType);
		pstmt.setInt(11, bankID);
		
		// Set reminder settings (convert "on" to "Y", anything else to "N")
		pstmt.setString(12, "on".equals(reminderMaturity) ? "Y" : "N");
		pstmt.setString(13, "on".equals(reminderIncomplete) ? "Y" : "N");
		
		// Set previousFDID - THIS IS THE KEY FIELD!
		pstmt.setInt(14, previousFDID);
		
		int rowsAffected = pstmt.executeUpdate();
		
		if (rowsAffected > 0) {
		rs = pstmt.getGeneratedKeys();
		if (rs.next()) {
		generatedFdID = rs.getInt(1);
		}
		}
		
		conn.commit();
		System.out.println("✅ Reinvested FD created successfully!");
		System.out.println("   New FD ID: " + generatedFdID);
		System.out.println("   Previous FD ID: " + previousFDID);
		System.out.println("   Deposit Amount: RM " + depositAmount);
		System.out.println("   Status: ONGOING (newly reinvested)");
		
		} catch (SQLException e) {
		System.err.println("❌ Error creating reinvested FD: " + e.getMessage());
		e.printStackTrace();
		if (conn != null) {
		try {
		conn.rollback();
		System.err.println("Transaction rolled back");
		} catch (SQLException ex) {
		System.err.println("Error rolling back: " + ex.getMessage());
		}
		}
		return -1;
		
		} finally {
		try {
		if (rs != null) rs.close();
		if (pstmt != null) pstmt.close();
		if (conn != null) {
		conn.setAutoCommit(true);
		conn.close();
		}
		} catch (SQLException e) {
		System.err.println("Error closing resources: " + e.getMessage());
		}
		}
		
		return generatedFdID;
		}
    
    public boolean updateFDStatusToReinvested(int fdID) {
        String sql = "UPDATE FixedDepositRecord SET status = 'REINVESTED' WHERE fdID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ FD" + fdID + " status updated to REINVESTED");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating FD status to REINVESTED: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public FixedDepositRecord getFixedDepositByIdComplete(int fdID) {
        String sql = "SELECT fd.*, b.bankName " +
                     "FROM FixedDepositRecord fd " +
                     "LEFT JOIN Bank b ON fd.bankID = b.bankID " +
                     "WHERE fd.fdID = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, fdID);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                FixedDepositRecord fd = new FixedDepositRecord();
                fd.setFdID(rs.getInt("fdID"));
                fd.setAccNumber(rs.getString("accNumber"));
                fd.setReferralNumber(rs.getBigDecimal("referralNumber"));
                fd.setDepositAmount(rs.getBigDecimal("depositAmount"));
                fd.setInterestRate(rs.getBigDecimal("interestRt"));
                fd.setStartDate(rs.getDate("startDate"));
                fd.setTenure(rs.getInt("tenure"));
                fd.setMaturityDate(rs.getDate("maturityDate"));
                fd.setFdCert(rs.getBytes("fdCert"));
                fd.setCertNo(rs.getString("certNo"));
                fd.setFdType(rs.getString("fdType"));
                fd.setStatus(rs.getString("status"));
                fd.setBankID(rs.getInt("bankID"));
                fd.setBankName(rs.getString("bankName"));
                fd.setReminderMaturity(rs.getString("reminderMaturity"));
                fd.setReminderIncomplete(rs.getString("reminderIncomplete"));
                
                // Get previousFDID
                int previousFDID = rs.getInt("previousFDID");
                if (!rs.wasNull()) {
                    fd.setPreviousFDID(previousFDID);
                }
                
                return fd;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting FD by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }


    // ==================== AUTO-RENEWAL & REINVESTMENT METHODS ====================
    
    /**
     * Auto-renew matured Free FDs with Auto Renewal = Yes
     * Called by scheduled task daily
     * @return Number of FDs auto-renewed
     */
    public int autoRenewMaturedFDs() {
        String sql = "SELECT f.FDID, f.ACCNUMBER, f.DEPOSITAMOUNT, f.INTERESTRT, f.STARTDATE, " +
                     "f.TENURE, f.MATURITYDATE, f.BANKID, f.FDTYPE, " +
                     "fr.AUTORENEWALSTATUS, fr.WITHDRAWABLESTATUS " +
                     "FROM FIXEDDEPOSITRECORD f " +
                     "JOIN FREEFD fr ON f.FDID = fr.FDID " +
                     "WHERE f.FDTYPE = 'FREEFD' " +
                     "AND f.STATUS = 'ONGOING' " +
                     "AND f.MATURITYDATE <= CURRENT_DATE " +
                     "AND fr.AUTORENEWALSTATUS = 'Y'";
        
        int renewedCount = 0;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int fdID = rs.getInt("FDID");
                String accNumber = rs.getString("ACCNUMBER");
                BigDecimal depositAmount = rs.getBigDecimal("DEPOSITAMOUNT");
                BigDecimal interestRate = rs.getBigDecimal("INTERESTRT");
                int tenure = rs.getInt("TENURE");
                int bankID = rs.getInt("BANKID");
                String autoRenewal = rs.getString("AUTORENEWALSTATUS");
                String withdrawable = rs.getString("WITHDRAWABLESTATUS");
                
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("🔄 AUTO-RENEWING FD" + fdID);
                
                // Calculate maturity amount
                BigDecimal interest = depositAmount.multiply(interestRate)
                                                  .multiply(new BigDecimal(tenure))
                                                  .divide(new BigDecimal(1200), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal maturityAmount = depositAmount.add(interest);
                
                // Calculate new dates
                Date currentDate = new Date(System.currentTimeMillis());
                Date newStartDate = new Date(currentDate.getTime() + 86400000); // Tomorrow
                
                // Calculate new maturity date
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(newStartDate);
                cal.add(java.util.Calendar.MONTH, tenure);
                Date newMaturityDate = new Date(cal.getTimeInMillis());
                
                // Use system/scheduler as staffID = 0 (or configure as needed)
                int staffID = 0; // System auto-renewal
                
                // Call reinvestFD method
                int newFdID = reinvestFD(fdID, newStartDate, tenure, newMaturityDate, 
                                        new Timestamp(System.currentTimeMillis()), staffID);
                
                if (newFdID > 0) {
                    renewedCount++;
                    System.out.println("✅ AUTO-RENEWED: FD" + fdID + " → FD" + newFdID);
                } else {
                    System.err.println("❌ AUTO-RENEWAL FAILED for FD" + fdID);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error in auto-renewal process: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("📊 Auto-Renewal Summary: " + renewedCount + " FDs renewed");
        return renewedCount;
    }
    
    /**
     * Reinvest an FD - Creates new FD linked to the old one
     * Used by both manual reinvest and auto-renewal
     * 
     * @param previousFdID The FD being reinvested
     * @param newStartDate Start date for new FD
     * @param newTenure Tenure in months for new FD
     * @param newMaturityDate Maturity date for new FD
     * @param transactionDate When reinvestment happened
     * @param staffID Staff who processed it (0 for system auto-renewal)
     * @return New FD ID, or -1 if failed
     */
    public int reinvestFD(int previousFdID, Date newStartDate, int newTenure, 
                          Date newMaturityDate, Timestamp transactionDate, int staffID) {
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Get old FD details
            FixedDepositRecord oldFD = getFixedDepositById(previousFdID);
            if (oldFD == null) {
                System.err.println("❌ Error: Old FD not found: FD" + previousFdID);
                return -1;
            }
            
            // Calculate maturity amount (profit from old FD)
            BigDecimal interest = oldFD.getDepositAmount().multiply(oldFD.getInterestRate())
                                       .multiply(new BigDecimal(oldFD.getTenure()))
                                       .divide(new BigDecimal(1200), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal maturityAmount = oldFD.getDepositAmount().add(interest);
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🔄 REINVESTING FD" + previousFdID);
            System.out.println("   Old Principal: RM " + oldFD.getDepositAmount());
            System.out.println("   Old Interest: RM " + interest);
            System.out.println("   Maturity Amount: RM " + maturityAmount);
            System.out.println("   New Start Date: " + newStartDate);
            System.out.println("   New Tenure: " + newTenure + " months");
            System.out.println("   New Maturity Date: " + newMaturityDate);
            System.out.println("   Staff ID: " + (staffID == 0 ? "System (Auto)" : staffID));
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            // Step 1: Update old FD status to MATURED
            String updateSql = "UPDATE FixedDepositRecord SET STATUS = 'MATURED' WHERE FDID = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, previousFdID);
            int updatedRows = updateStmt.executeUpdate();
            updateStmt.close();
            
            if (updatedRows == 0) {
                conn.rollback();
                System.err.println("❌ Failed to update old FD status");
                return -1;
            }
            
            System.out.println("✅ Step 1: Updated FD" + previousFdID + " status to MATURED");
            
            // Step 2: Record REINVEST transaction for old FD
            String reinvestSql = "INSERT INTO FixedDepositTransaction " +
                                "(fdID, staffID, transactionDate, transactionType, calcTotalProfit) " +
                                "VALUES (?, ?, ?, 'REINVEST', ?)";
            PreparedStatement reinvestStmt = conn.prepareStatement(reinvestSql);
            reinvestStmt.setInt(1, previousFdID);
            reinvestStmt.setInt(2, staffID);
            reinvestStmt.setTimestamp(3, transactionDate);
            reinvestStmt.setBigDecimal(4, maturityAmount);
            reinvestStmt.executeUpdate();
            reinvestStmt.close();
            
            System.out.println("✅ Step 2: Recorded REINVEST transaction for FD" + previousFdID);
            
            // Step 3: Create new FD record with PREVIOUSFDID link
            String insertSql = "INSERT INTO FixedDepositRecord " +
                              "(accNumber, depositAmount, interestRt, startDate, tenure, " +
                              "maturityDate, fdType, status, bankID, previousFdID, " +
                              "reminderMaturity, reminderIncomplete) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, 'ONGOING', ?, ?, 'N', 'N')";
            
            PreparedStatement insertStmt = conn.prepareStatement(insertSql, new String[]{"FDID"});
            insertStmt.setString(1, oldFD.getAccNumber());
            insertStmt.setBigDecimal(2, maturityAmount);  // New principal = old FD's maturity amount
            insertStmt.setBigDecimal(3, oldFD.getInterestRate());  // Keep same interest rate
            insertStmt.setDate(4, newStartDate);  // New start date
            insertStmt.setInt(5, newTenure);  // New tenure
            insertStmt.setDate(6, newMaturityDate);  // New maturity date
            insertStmt.setString(7, oldFD.getFdType());  // Keep same FD type
            insertStmt.setInt(8, oldFD.getBankID());  // Keep same bank
            insertStmt.setInt(9, previousFdID);  // LINK TO OLD FD!
            
            int rowsAffected = insertStmt.executeUpdate();
            
            // Get new FD ID
            int newFdID = -1;
            ResultSet rs = insertStmt.getGeneratedKeys();
            if (rs.next()) {
                newFdID = rs.getInt(1);
            }
            rs.close();
            insertStmt.close();
            
            if (rowsAffected == 0 || newFdID == -1) {
                conn.rollback();
                System.err.println("❌ Failed to create new FD record");
                return -1;
            }
            
            System.out.println("✅ Step 3: Created new FD" + newFdID + " linked to FD" + previousFdID);
            
            // Step 4: Calculate expected profit for new FD
            BigDecimal newInterest = maturityAmount.multiply(oldFD.getInterestRate())
                                                  .multiply(new BigDecimal(newTenure))
                                                  .divide(new BigDecimal(1200), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal newTotalProfit = maturityAmount.add(newInterest);
            
            // Step 5: Record CREATE transaction for new FD
            String createSql = "INSERT INTO FixedDepositTransaction " +
                              "(fdID, staffID, transactionType, calcTotalProfit) " +
                              "VALUES (?, ?, 'CREATE', ?)";
            PreparedStatement createStmt = conn.prepareStatement(createSql);
            createStmt.setInt(1, newFdID);
            createStmt.setInt(2, staffID);
            createStmt.setBigDecimal(3, newTotalProfit);
            createStmt.executeUpdate();
            createStmt.close();
            
            System.out.println("✅ Step 4: Recorded CREATE transaction for FD" + newFdID);
            System.out.println("   Expected new profit: RM " + newTotalProfit);
            
            // Step 6: Copy type-specific data from old FD to new FD
            if ("FREEFD".equals(oldFD.getFdType())) {
                Map<String, String> freeData = getFreeFDData(previousFdID);
                if (freeData != null) {
                    String autoRenewal = freeData.get("autoRenewalStatus");
                    String withdrawable = freeData.get("withdrawableStatus");
                    insertFreeFD(newFdID, autoRenewal, withdrawable);
                    System.out.println("✅ Step 5: Copied FreeFD data to FD" + newFdID);
                    System.out.println("   - Auto Renewal: " + ("Y".equals(autoRenewal) ? "Yes" : "No"));
                    System.out.println("   - Withdrawable: " + ("Y".equals(withdrawable) ? "Yes" : "No"));
                }
            } else if ("PLEDGEFD".equals(oldFD.getFdType())) {
                Map<String, Object> pledgeData = getPledgeFDData(previousFdID);
                if (pledgeData != null) {
                    String collateral = (String) pledgeData.get("collateralStatus");
                    BigDecimal pledgeValue = (BigDecimal) pledgeData.get("pledgeValue");
                    insertPledgeFD(newFdID, collateral, pledgeValue);
                    System.out.println("✅ Step 5: Copied PledgeFD data to FD" + newFdID);
                }
            }
            
            conn.commit(); // Commit all changes
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("✅ REINVESTMENT SUCCESSFUL!");
            System.out.println("   Old FD: FD" + previousFdID + " (MATURED)");
            System.out.println("   New FD: FD" + newFdID + " (ONGOING)");
            System.out.println("   New Principal: RM " + maturityAmount);
            System.out.println("   Expected Profit: RM " + newTotalProfit);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return newFdID;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("❌ Reinvestment failed - transaction rolled back");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("❌ Error reinvesting FD" + previousFdID + ": " + e.getMessage());
            e.printStackTrace();
            return -1;
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // ========================================
    // DUPLICATE CHECKING METHODS
    // ========================================
    
    /**
     * Check if account number already exists
     */
    public boolean accountNumberExists(String accountNumber) {
        String query = "SELECT COUNT(*) FROM FIXEDDEPOSITRECORD WHERE ACCNUMBER = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("🔍 Account Number Check: " + accountNumber + " - " + (count > 0 ? "EXISTS" : "Available"));
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking account number: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if referral number already exists
     */
    public boolean referralNumberExists(String referralNumber) {
        if (referralNumber == null || referralNumber.trim().isEmpty()) {
            return false;
        }
        
        String query = "SELECT COUNT(*) FROM FIXEDDEPOSITRECORD WHERE REFERRALNUMBER = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, referralNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("🔍 Referral Number Check: " + referralNumber + " - " + (count > 0 ? "EXISTS" : "Available"));
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking referral number: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if certificate number already exists
     */
    public boolean certificateNumberExists(String certNo) {
        if (certNo == null || certNo.trim().isEmpty()) {
            return false;
        }
        
        String query = "SELECT COUNT(*) FROM FIXEDDEPOSITRECORD WHERE CERTNO = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, certNo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("🔍 Certificate Number Check: " + certNo + " - " + (count > 0 ? "EXISTS" : "Available"));
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking certificate number: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check all unique fields at once
     * @return Error message if duplicates found, null if all are unique
     */
    public String checkForDuplicates(String accountNumber, String referralNumber, String certNo) {
        System.out.println("========================================");
        System.out.println("🔍 Checking for duplicates...");
        System.out.println("   Account Number: " + accountNumber);
        System.out.println("   Referral Number: " + referralNumber);
        System.out.println("   Certificate Number: " + certNo);
        
        StringBuilder errors = new StringBuilder();
        
        if (accountNumberExists(accountNumber)) {
            errors.append("Account Number ").append(accountNumber).append(" already exists. ");
        }
        
        if (referralNumberExists(referralNumber)) {
            errors.append("Referral Number ").append(referralNumber).append(" already exists. ");
        }
        
        if (certificateNumberExists(certNo)) {
            errors.append("Certificate Number ").append(certNo).append(" already exists. ");
        }
        
        String result = errors.length() > 0 ? errors.toString().trim() : null;
        System.out.println("   Result: " + (result != null ? "❌ DUPLICATES FOUND" : "✅ All unique"));
        System.out.println("========================================");
        
        return result;
    }
    
    public List<FixedDepositRecord> getAllFDsForReminders() {
        List<FixedDepositRecord> fdList = new ArrayList<>();
        
        String query = "SELECT f.FDID, f.ACCNUMBER, f.DEPOSITAMOUNT, f.INTERESTRT, " +
                       "f.STARTDATE, f.TENURE, f.MATURITYDATE, f.CERTNO, f.FDTYPE, " +
                       "f.STATUS, f.REMINDERMATURITY, f.REMINDERINCOMPLETE, f.FDCERT, " +
                       "f.REFERRALNUMBER, f.REMAININGBALANCE, f.TOTALWITHDRAWN, " +
                       "b.BANKNAME, " +
                       "t.STAFFID " +
                       "FROM FIXEDDEPOSITRECORD f " +
                       "LEFT JOIN BANK b ON f.BANKID = b.BANKID " +
                       "LEFT JOIN FIXEDDEPOSITTRANSACTION t ON f.FDID = t.FDID " +
                       "WHERE t.TRANSACTIONTYPE = 'CREATE' " +
                       "ORDER BY f.FDID DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                FixedDepositRecord fd = new FixedDepositRecord();
                
                // Set FD ID
                fd.setFdID(rs.getInt("FDID"));
                
                // Set Account Number
                fd.setAccNumber(rs.getString("ACCNUMBER"));
                
                // Set Deposit Amount (BigDecimal)
                fd.setDepositAmount(rs.getBigDecimal("DEPOSITAMOUNT"));
                
                // Set Interest Rate (BigDecimal)
                fd.setInterestRate(rs.getBigDecimal("INTERESTRT"));
                
                // Set Dates
                fd.setStartDate(rs.getDate("STARTDATE"));
                fd.setMaturityDate(rs.getDate("MATURITYDATE"));
                
                // Set Tenure
                fd.setTenure(rs.getInt("TENURE"));
                
                // Set Certificate Number
                fd.setCertNo(rs.getString("CERTNO"));
                
                // Set FD Type
                fd.setFdType(rs.getString("FDTYPE"));
                
                // Set Status
                fd.setStatus(rs.getString("STATUS"));
                
                // Set Reminder flags
                fd.setReminderMaturity(rs.getString("REMINDERMATURITY"));
                fd.setReminderIncomplete(rs.getString("REMINDERINCOMPLETE"));
                
                // Set Bank Name
                fd.setBankName(rs.getString("BANKNAME"));
                
                // Set Staff ID from transaction
                fd.setStaffID(rs.getInt("STAFFID"));
                
                // Set Balance tracking fields
                fd.setRemainingBalance(rs.getBigDecimal("REMAININGBALANCE"));
                fd.setTotalWithdrawn(rs.getBigDecimal("TOTALWITHDRAWN"));
                
                // Get Certificate File (BLOB)
                Blob certBlob = rs.getBlob("FDCERT");
                if (certBlob != null) {
                    fd.setFdCert(certBlob.getBytes(1, (int) certBlob.length()));
                }
                
                // Get Referral Number (NUMBER stored as BigDecimal)
                BigDecimal refNum = rs.getBigDecimal("REFERRALNUMBER");
                if (refNum != null) {
                    fd.setReferralNumber(refNum);
                }
                
                fdList.add(fd);
            }
            
            System.out.println("✅ Loaded " + fdList.size() + " FD records for reminder checking");
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting FDs for reminders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return fdList;
    }
    
}

