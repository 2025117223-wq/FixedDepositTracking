package com.fd.model;

import java.sql.Timestamp;

/**
 * FixedDepositTransaction Model
 * Bridge table to track FD lifecycle events
 * Records which staff member performed what action on which FD
 */
public class FixedDepositTransaction {
    private int transactionID;
    private int fdID;
    private int staffID;
    private String transactionType; // CREATE, WITHDRAW, REINVEST, UPDATE
    private Timestamp transactionDate;
    private String remarks;

    // Default constructor
    public FixedDepositTransaction() {
    }

    // Constructor for creating new transaction (without transactionID - auto-generated)
    public FixedDepositTransaction(int fdID, int staffID, String transactionType, String remarks) {
        this.fdID = fdID;
        this.staffID = staffID;
        this.transactionType = transactionType;
        this.remarks = remarks;
    }

    // Full constructor (for retrieving from database)
    public FixedDepositTransaction(int transactionID, int fdID, int staffID, 
                                   String transactionType, Timestamp transactionDate, String remarks) {
        this.transactionID = transactionID;
        this.fdID = fdID;
        this.staffID = staffID;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.remarks = remarks;
    }

    // Getters and Setters
    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getFdID() {
        return fdID;
    }

    public void setFdID(int fdID) {
        this.fdID = fdID;
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "FixedDepositTransaction{" +
                "transactionID=" + transactionID +
                ", fdID=" + fdID +
                ", staffID=" + staffID +
                ", transactionType='" + transactionType + '\'' +
                ", transactionDate=" + transactionDate +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
