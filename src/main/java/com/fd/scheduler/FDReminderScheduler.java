package com.fd.scheduler;

import com.fd.dao.FixedDepositDAO;
import com.fd.dao.StaffDAO;
import com.fd.model.FixedDepositRecord;
import com.fd.model.Staff;
import com.fd.util.EmailUtil;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * FDReminderScheduler - Sends automatic email reminders for:
 * 1. FDs maturing in 7 days - TO THE FD CREATOR
 * 2. Incomplete FD records - TO THE FD CREATOR
 * 
 * SIMPLE VERSION: No database changes needed
 * Uses existing STAFFID from FIXEDDEPOSITTRANSACTION
 */
@WebListener
public class FDReminderScheduler implements ServletContextListener {
    
    private ScheduledExecutorService scheduler;
    
   /** @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("========================================");
        System.out.println("🔔 FD Reminder Scheduler STARTING");
        System.out.println("   Reminders sent to: FD Creator");
        System.out.println("========================================");
        
        // Create scheduler
        scheduler = Executors.newScheduledThreadPool(1);
        
        // Schedule task to run every hour
        // For testing: 60 minutes
        // For production: Change to 1440 for daily (24 hours)
        long initialDelay = 1; // Start after 1 minute
        long period = 180; // Run every 60 minutes (1 hour)
        
        scheduler.scheduleAtFixedRate(
            new ReminderTask(),
            initialDelay,
            period,
            TimeUnit.MINUTES
        );
        
        System.out.println("✅ Reminder scheduler started");
        System.out.println("   Initial delay: " + initialDelay + " minute(s)");
        System.out.println("   Period: " + period + " minutes");
        System.out.println("========================================");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("🔔 Stopping FD Reminder Scheduler...");
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
    
    /**
     * Reminder task that runs periodically
     */
    private static class ReminderTask implements Runnable {
        
        @Override
        public void run() {
            System.out.println("========================================");
            System.out.println("🔔 Running FD Reminder Task");
            System.out.println("   Time: " + LocalDate.now());
            System.out.println("========================================");
            
            try {
                // Send maturity reminders
                sendMaturityReminders();
                
                // Send incomplete FD reminders
                sendIncompleteReminders();
                
                System.out.println("✅ Reminder task completed");
                
            } catch (Exception e) {
                System.err.println("❌ Error in reminder task: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("========================================");
        }
        
        /**
         * Send reminders for FDs maturing in 7 days
         */
        private void sendMaturityReminders() {
            System.out.println("📅 Checking for FDs maturing in 7 days...");
            
            FixedDepositDAO fdDAO = new FixedDepositDAO();
            StaffDAO staffDAO = new StaffDAO();
            
            // Get all FDs
            List<FixedDepositRecord> allFDs = fdDAO.getAllFDsForReminders();
            
            LocalDate today = LocalDate.now();
            
            int reminderCount = 0;
            
            for (FixedDepositRecord fd : allFDs) {
                try {
                    // Only check ONGOING FDs that have maturity reminder enabled
                    if (!"ONGOING".equals(fd.getStatus())) {
                        continue;
                    }
                    
                    if (!"Y".equals(fd.getReminderMaturity())) {
                        continue;
                    }
                    
                    // Check if maturity date exists
                    if (fd.getMaturityDate() == null) {
                        continue;
                    }
                    
                    // Check if maturity date is 7 days away
                    LocalDate maturityDate = fd.getMaturityDate().toLocalDate();
                    long daysUntilMaturity = ChronoUnit.DAYS.between(today, maturityDate);
                    
                    if (daysUntilMaturity == 7) {
                        // Send reminder email to FD creator
                        sendMaturityReminderEmail(fd, staffDAO);
                        reminderCount++;
                    }
                } catch (Exception e) {
                    System.err.println("   ❌ Error processing FD " + fd.getFdID() + ": " + e.getMessage());
                }
            }
            
            System.out.println("   Sent " + reminderCount + " maturity reminders");
        }
        
        /**
         * Send reminder email for FD maturing soon
         * TO THE FD CREATOR
         */
        private void sendMaturityReminderEmail(FixedDepositRecord fd, StaffDAO staffDAO) {
            try {
                // Get FD creator's staff ID
                int creatorStaffID = fd.getStaffID();
                
                if (creatorStaffID == 0) {
                    System.out.println("   ⚠️ No staff ID for FD " + fd.getAccNumber());
                    return;
                }
                
                // Get staff details
                Staff staff = staffDAO.getStaffById(creatorStaffID);
                
                if (staff == null || staff.getEmail() == null || staff.getEmail().isEmpty()) {
                    System.out.println("   ⚠️ No email for staff " + creatorStaffID + " (FD " + fd.getAccNumber() + ")");
                    return;
                }
                
                String toEmail = staff.getEmail();
                String subject = "Reminder: FD Maturing in 7 Days - " + fd.getAccNumber();
                
                String body = buildMaturityReminderEmail(fd, staff);
                
                // Send email
                EmailUtil.sendEmail(toEmail, subject, body);
                
                System.out.println("   ✅ Sent maturity reminder for FD " + fd.getAccNumber() + " to " + staff.getName() + " (" + toEmail + ")");
                
            } catch (Exception e) {
                System.err.println("   ❌ Failed to send maturity reminder for FD " + fd.getAccNumber());
                e.printStackTrace();
            }
        }
        
        /**
         * Build maturity reminder email body
         */
        private String buildMaturityReminderEmail(FixedDepositRecord fd, Staff staff) {
            StringBuilder body = new StringBuilder();
            
            body.append("Dear ").append(staff.getName()).append(",\n\n");
            
            body.append("This is a friendly reminder that your Fixed Deposit will mature in 7 days.\n\n");
            
            body.append("=".repeat(50)).append("\n");
            body.append("FD DETAILS\n");
            body.append("=".repeat(50)).append("\n");
            body.append("Account Number: ").append(fd.getAccNumber()).append("\n");
            body.append("Bank: ").append(fd.getBankName()).append("\n");
            body.append("Deposit Amount: RM ").append(fd.getFormattedDepositAmount()).append("\n");
            body.append("Interest Rate: ").append(fd.getInterestRate()).append("%\n");
            body.append("Tenure: ").append(fd.getTenure()).append(" months\n");
            body.append("Start Date: ").append(fd.getStartDate()).append("\n");
            body.append("Maturity Date: ").append(fd.getMaturityDate()).append("\n");
            
            if (fd.getMaturityAmount() != null) {
                body.append("Maturity Amount: RM ").append(String.format("%,.2f", fd.getMaturityAmount())).append("\n");
            }
            
            body.append("=".repeat(50)).append("\n\n");
            
            body.append("Please take necessary action before the maturity date.\n\n");
            
            body.append("Best regards,\n");
            body.append("Fixed Deposit Tracking System\n");
            
            return body.toString();
        }
        
        /**
         * Send reminders for incomplete FD records
         */
        private void sendIncompleteReminders() {
            System.out.println("📝 Checking for incomplete FD records...");
            
            FixedDepositDAO fdDAO = new FixedDepositDAO();
            StaffDAO staffDAO = new StaffDAO();
            
            // Get all FDs
            List<FixedDepositRecord> allFDs = fdDAO.getAllFDsForReminders();
            
            int reminderCount = 0;
            
            for (FixedDepositRecord fd : allFDs) {
                try {
                    // Only check FDs that have incomplete reminder enabled
                    if (!"Y".equals(fd.getReminderIncomplete())) {
                        continue;
                    }
                    
                    // Check if FD record is incomplete
                    if (isIncomplete(fd)) {
                        // Send reminder email to FD creator
                        sendIncompleteReminderEmail(fd, staffDAO);
                        reminderCount++;
                    }
                } catch (Exception e) {
                    System.err.println("   ❌ Error processing FD " + fd.getFdID() + ": " + e.getMessage());
                }
            }
            
            System.out.println("   Sent " + reminderCount + " incomplete reminders");
        }
        
        /**
         * Check if FD record is incomplete
         */
        private boolean isIncomplete(FixedDepositRecord fd) {
            // FD is incomplete if any required field is missing
            
            // Missing certificate number
            if (fd.getCertNo() == null || fd.getCertNo().trim().isEmpty()) {
                return true;
            }
            
            // Missing certificate file
            if (fd.getFdCert() == null || fd.getFdCert().length == 0) {
                return true;
            }
            
            // You can add more checks as needed
            
            return false;
        }
        
        /**
         * Send reminder email for incomplete FD record
         * TO THE FD CREATOR
         */
        private void sendIncompleteReminderEmail(FixedDepositRecord fd, StaffDAO staffDAO) {
            try {
                // Get FD creator's staff ID
                int creatorStaffID = fd.getStaffID();
                
                if (creatorStaffID == 0) {
                    System.out.println("   ⚠️ No staff ID for FD " + fd.getAccNumber());
                    return;
                }
                
                // Get staff details
                Staff staff = staffDAO.getStaffById(creatorStaffID);
                
                if (staff == null || staff.getEmail() == null || staff.getEmail().isEmpty()) {
                    System.out.println("   ⚠️ No email for staff " + creatorStaffID + " (FD " + fd.getAccNumber() + ")");
                    return;
                }
                
                String toEmail = staff.getEmail();
                String subject = "Reminder: Incomplete FD Record - " + fd.getAccNumber();
                
                String body = buildIncompleteReminderEmail(fd, staff);
                
                // Send email
                EmailUtil.sendEmail(toEmail, subject, body);
                
                System.out.println("   ✅ Sent incomplete reminder for FD " + fd.getAccNumber() + " to " + staff.getName() + " (" + toEmail + ")");
                
            } catch (Exception e) {
                System.err.println("   ❌ Failed to send incomplete reminder for FD " + fd.getAccNumber());
                e.printStackTrace();
            }
        }
        
        /**
         * Build incomplete reminder email body
         */
        private String buildIncompleteReminderEmail(FixedDepositRecord fd, Staff staff) {
            StringBuilder body = new StringBuilder();
            
            body.append("Dear ").append(staff.getName()).append(",\n\n");
            
            body.append("This is a reminder that your Fixed Deposit record is incomplete.\n\n");
            
            body.append("=".repeat(50)).append("\n");
            body.append("FD DETAILS\n");
            body.append("=".repeat(50)).append("\n");
            body.append("Account Number: ").append(fd.getAccNumber()).append("\n");
            body.append("Bank: ").append(fd.getBankName()).append("\n");
            body.append("Status: ").append(fd.getStatus()).append("\n");
            body.append("=".repeat(50)).append("\n\n");
            
            body.append("MISSING INFORMATION:\n");
            
            // List missing fields
            if (fd.getCertNo() == null || fd.getCertNo().trim().isEmpty()) {
                body.append("- Certificate Number\n");
            }
            
            if (fd.getFdCert() == null || fd.getFdCert().length == 0) {
                body.append("- Certificate File/Document\n");
            }
            
            body.append("\n");
            body.append("Please update the FD record with the missing information.\n\n");
            
            body.append("Best regards,\n");
            body.append("Fixed Deposit Tracking System\n");
            
            return body.toString();
        }
    }
}
