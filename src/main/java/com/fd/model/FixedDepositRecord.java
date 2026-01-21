package com.fd.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * FixedDepositRecord Model Class (UPDATED)
 * Now includes balance tracking for partial withdrawals
 */
public class FixedDepositRecord {
    
    private int fdID;
    private String accNumber;
    private BigDecimal referralNumber;
    private BigDecimal depositAmount;
    private BigDecimal interestRate;
    private Date startDate;
    private int tenure;
    private Date maturityDate;
    private byte[] fdCert;
    private String certNo;
    private String fdType;      // 'FREEFD' or 'PLEDGEFD'
    private String status;      // 'PENDING', 'ONGOING', 'MATURED', 'WITHDRAWN'
    private int bankID;
    private String bankName;    // Joined from Bank table
    private String reminderMaturity;   // Stores 'Y' or 'N'
    private String reminderIncomplete; // Stores 'Y' or 'N'
    private Integer previousFDID;      // Links to previous FD if reinvested
    private int staffID;
    
    // NEW: Balance tracking fields
    private BigDecimal remainingBalance;  // Current available balance
    private BigDecimal totalWithdrawn;    // Total amount withdrawn so far
    
    // Default Constructor
    public FixedDepositRecord() {
    }
    
    // Constructor with all fields (including new balance fields)
    public FixedDepositRecord(int fdID, String accNumber, BigDecimal referralNumber,
                               BigDecimal depositAmount, BigDecimal interestRate,
                               Date startDate, int tenure, Date maturityDate,
                               String certNo, String fdType, String status,
                               int bankID, String bankName,
                               BigDecimal remainingBalance, BigDecimal totalWithdrawn) {
        this.fdID = fdID;
        this.accNumber = accNumber;
        this.referralNumber = referralNumber;
        this.depositAmount = depositAmount;
        this.interestRate = interestRate;
        this.startDate = startDate;
        this.tenure = tenure;
        this.maturityDate = maturityDate;
        this.certNo = certNo;
        this.fdType = fdType;
        this.status = status;
        this.bankID = bankID;
        this.bankName = bankName;
        this.remainingBalance = remainingBalance;
        this.totalWithdrawn = totalWithdrawn;
    }
    
    // Getters and Setters
    public int getFdID() {
        return fdID;
    }
    
    public void setFdID(int fdID) {
        this.fdID = fdID;
    }
    
    public String getAccNumber() {
        return accNumber;
    }
    
    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }
    
    public BigDecimal getReferralNumber() {
        return referralNumber;
    }
    
    public void setReferralNumber(BigDecimal referralNumber) {
        this.referralNumber = referralNumber;
    }
    
    public BigDecimal getDepositAmount() {
        return depositAmount;
    }
    
    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }
    
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public int getTenure() {
        return tenure;
    }
    
    public void setTenure(int tenure) {
        this.tenure = tenure;
    }
    
    public Date getMaturityDate() {
        return maturityDate;
    }
    
    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }
    
    public byte[] getFdCert() {
        return fdCert;
    }
    
    public void setFdCert(byte[] fdCert) {
        this.fdCert = fdCert;
    }
    
    public String getCertNo() {
        return certNo;
    }
    
    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }
    
    public String getFdType() {
        return fdType;
    }
    
    public void setFdType(String fdType) {
        this.fdType = fdType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getBankID() {
        return bankID;
    }
    
    public void setBankID(int bankID) {
        this.bankID = bankID;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    // Reminder getters and setters
    public String getReminderMaturity() {
        return reminderMaturity;
    }
    
    public void setReminderMaturity(String reminderMaturity) {
        this.reminderMaturity = reminderMaturity;
    }
    
    public String getReminderIncomplete() {
        return reminderIncomplete;
    }
    
    public void setReminderIncomplete(String reminderIncomplete) {
        this.reminderIncomplete = reminderIncomplete;
    }
    
    // Previous FD ID getters and setters
    public Integer getPreviousFDID() {
        return previousFDID;
    }
    
    public void setPreviousFDID(Integer previousFDID) {
        this.previousFDID = previousFDID;
    }
    
    // NEW: Balance tracking getters and setters
    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }
    
    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
    
    public BigDecimal getTotalWithdrawn() {
        return totalWithdrawn;
    }
    
    public void setTotalWithdrawn(BigDecimal totalWithdrawn) {
        this.totalWithdrawn = totalWithdrawn;
    }
    
    // Helper method to calculate maturity amount
    public BigDecimal getMaturityAmount() {
        if (depositAmount != null && interestRate != null) {
            BigDecimal interest = depositAmount.multiply(interestRate)
                                              .multiply(new BigDecimal(tenure))
                                              .divide(new BigDecimal(1200), 2, BigDecimal.ROUND_HALF_UP);
            return depositAmount.add(interest);
        }
        return depositAmount;
    }
    
    // Helper method to check if FD is fully withdrawn
    public boolean isFullyWithdrawn() {
        if (remainingBalance == null) return false;
        return remainingBalance.compareTo(BigDecimal.ZERO) == 0;
    }
    
    // Helper method to check if FD has any withdrawals
    public boolean hasWithdrawals() {
        if (totalWithdrawn == null) return false;
        return totalWithdrawn.compareTo(BigDecimal.ZERO) > 0;
    }
    
    // Helper method to format deposit amount with commas
    public String getFormattedDepositAmount() {
        if (depositAmount != null) {
            return String.format("%,.2f", depositAmount);
        }
        return "0.00";
    }
    
    // Helper method to format remaining balance
    public String getFormattedRemainingBalance() {
        if (remainingBalance != null) {
            return String.format("%,.2f", remainingBalance);
        }
        return "0.00";
    }
    
    // Helper method to format total withdrawn
    public String getFormattedTotalWithdrawn() {
        if (totalWithdrawn != null) {
            return String.format("%,.2f", totalWithdrawn);
        }
        return "0.00";
    }
    
    // Helper method to get display status (capitalize first letter)
    public String getDisplayStatus() {
        if (status != null) {
            return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
        }
        return "";
    }
    
    // Helper method to get CSS class for status
    public String getStatusClass() {
        if (status != null) {
            return status.toLowerCase();
        }
        return "";
    }
    
    @Override
    public String toString() {
        return "FixedDepositRecord{" +
                "fdID=" + fdID +
                ", accNumber='" + accNumber + '\'' +
                ", depositAmount=" + depositAmount +
                ", remainingBalance=" + remainingBalance +
                ", totalWithdrawn=" + totalWithdrawn +
                ", bankName='" + bankName + '\'' +
                ", status='" + status + '\'' +
                ", previousFDID=" + previousFDID +
                '}';
    }
    
    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }
}
