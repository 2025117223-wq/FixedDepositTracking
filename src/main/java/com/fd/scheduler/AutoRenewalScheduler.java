package com.fd.scheduler;

import com.fd.dao.FixedDepositDAO;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AutoRenewalScheduler
 * 
 * Automatically renews matured Free FDs that have Auto Renewal = Yes
 * Runs daily at configured time (default: 00:05 AM)
 * 
 * Features:
 * - Checks for matured Free FDs with Auto Renewal enabled
 * - Creates new FD linked to old one (PREVIOUSFDID)
 * - Records all transactions (REINVEST for old, CREATE for new)
 * - Copies type-specific settings
 * - Maintains complete audit trail
 */
@WebListener
public class AutoRenewalScheduler implements ServletContextListener {
    
    private ScheduledExecutorService scheduler;
    private FixedDepositDAO fdDAO;
    
    /**
     * Initialize scheduler when application starts
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔄 AUTO-RENEWAL SCHEDULER STARTING...");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        fdDAO = new FixedDepositDAO();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        
        // Calculate initial delay (time until next 00:05 AM)
        long initialDelay = calculateInitialDelay();
        long period = 24 * 60 * 60; // 24 hours in seconds
        
        // Schedule task
        scheduler.scheduleAtFixedRate(
            new AutoRenewalTask(),
            initialDelay,
            period,
            TimeUnit.SECONDS
        );
        
        LocalDateTime nextRun = LocalDateTime.now().plusSeconds(initialDelay);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        System.out.println("✅ Auto-Renewal Scheduler initialized successfully");
        System.out.println("📅 Next run scheduled at: " + nextRun.format(formatter));
        System.out.println("🔁 Runs every 24 hours thereafter");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Shutdown scheduler when application stops
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("🛑 Shutting down Auto-Renewal Scheduler...");
        
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("✅ Auto-Renewal Scheduler stopped");
    }
    
    /**
     * Calculate seconds until next 00:05 AM
     * @return seconds to wait
     */
    private long calculateInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.toLocalDate().atTime(0, 5); // 00:05 AM today
        
        // If it's already past 00:05 AM today, schedule for tomorrow
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }
        
        long secondsUntilNextRun = java.time.Duration.between(now, nextRun).getSeconds();
        
        // For testing: uncomment to run after 1 minute instead
        // return 60;
        
        return secondsUntilNextRun;
    }
    
    /**
     * Auto-Renewal Task
     * Executes the actual renewal process
     */
    private class AutoRenewalTask implements Runnable {
        
        @Override
        public void run() {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("🔄 AUTO-RENEWAL TASK STARTED");
            System.out.println("📅 Execution Time: " + now.format(formatter));
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            try {
                // Call DAO method to auto-renew matured FDs
                int renewedCount = fdDAO.autoRenewMaturedFDs();
                
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("✅ AUTO-RENEWAL TASK COMPLETED");
                System.out.println("📊 FDs Renewed: " + renewedCount);
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                
            } catch (Exception e) {
                System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.err.println("❌ AUTO-RENEWAL TASK FAILED");
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                System.err.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            }
        }
    }
}
