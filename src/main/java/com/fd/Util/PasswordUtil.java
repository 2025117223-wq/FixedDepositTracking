package com.fd.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Method to hash the plain password
    public static String processPassword(String plainPassword) {
        // Hash the password using BCrypt and return the hashed version
        String salt = BCrypt.gensalt();  // Generate a salt
        return BCrypt.hashpw(plainPassword, salt);  // Hash the password with the salt
    }

    // Method to verify the password during login
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        // Compare the plain password entered with the hashed password stored in the database
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
