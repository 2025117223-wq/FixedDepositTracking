package com.fd.util;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * EmailUtil - Send emails via Gmail SMTP
 * Package: com.fd.util
 */
public class EmailUtil {

    // ⚠️ REPLACE THESE WITH YOUR OWN GMAIL CREDENTIALS
    private static final String FROM_EMAIL = "luqmanikmalrahmad@gmail.com";
    private static final String APP_PASSWORD = "rgcixidlqbjusshq";

    /**
     * Send email via Gmail SMTP
     * 
     * @param toEmail Recipient email
     * @param subject Email subject
     * @param body Email body (plain text)
     * @throws RuntimeException if email fails to send
     */
    public static void sendEmail(String toEmail, String subject, String body) {
        
        System.out.println("📧 EmailUtil: Sending email");
        System.out.println("   To: " + toEmail);
        System.out.println("   Subject: " + subject);

        Properties props = new Properties();

        // Gmail SMTP settings
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // TLS authentication
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Debug output (optional - set to false in production)
        props.put("mail.debug", "false");

        // Force TLSv1.2 (if needed)
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Create session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            // Send message
            Transport.send(message);
            
            System.out.println("✅ Email sent successfully");

        } catch (Exception e) {  // Catching general Exception instead of MessagingException
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
