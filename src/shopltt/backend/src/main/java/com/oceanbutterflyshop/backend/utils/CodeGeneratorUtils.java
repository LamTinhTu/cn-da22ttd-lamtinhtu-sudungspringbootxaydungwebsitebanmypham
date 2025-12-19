package com.oceanbutterflyshop.backend.utils;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/**
 * Utility class for generating auto-generated codes following the pattern:
 * PREFIX + 8 RANDOM DIGITS
 * 
 * Examples:
 * - Brand: "TH" + 8 digits (e.g., TH12345678)
 * - Product: "SP" + 8 digits (e.g., SP09123842)
 * - Order: "DH" + 8 digits (e.g., DH87654321)
 * - User: Role-based ("AD", "NV", "KH") + 8 digits (e.g., AD00001234)
 */
@Component
public class CodeGeneratorUtils {
    
    private static final SecureRandom random = new SecureRandom();
    private static final int MAX_RANDOM_VALUE = 100000000; // 10^8 for 8 digits max
    
    /**
     * Generates a code with the specified prefix followed by 8 random digits.
     * The digits are padded with leading zeros if necessary to ensure exactly 8 digits.
     * 
     * @param prefix The prefix to prepend to the generated digits (e.g., "SP", "TH", "DH", "AD", "NV", "KH")
     * @return A string containing the prefix followed by exactly 8 digits
     * 
     * @example
     * generateCode("SP") might return "SP09123842"
     * generateCode("TH") might return "TH00001234"
     * generateCode("DH") might return "DH87654321"
     */
    public String generateCode(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        
        // Generate a random number between 0 and 99999999 (inclusive)
        int randomNumber = random.nextInt(MAX_RANDOM_VALUE);
        
        // Format the number to ensure exactly 8 digits with leading zeros if necessary
        String paddedNumber = String.format("%08d", randomNumber);
        
        return prefix + paddedNumber;
    }
    
    /**
     * Generates a user code based on the role.
     * 
     * @param role The role name (Admin, Staff, Customer)
     * @return A generated code with appropriate prefix
     */
    public String generateUserCode(String role) {
        String prefix;
        
        switch (role.toUpperCase()) {
            case "ADMIN":
            case "ADMINISTRATOR":
                prefix = "AD";
                break;
            case "STAFF":
            case "NV":
                prefix = "NV";
                break;
            case "CUSTOMER":
            case "KH":
                prefix = "KH";
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role + ". Expected: Admin, Staff, or Customer");
        }
        
        return generateCode(prefix);
    }
    
    /**
     * Generates a brand code with "TH" prefix.
     * 
     * @return A generated brand code (e.g., "TH12345678")
     */
    public String generateBrandCode() {
        return generateCode("TH");
    }
    
    /**
     * Generates a product code with "SP" prefix.
     * 
     * @return A generated product code (e.g., "SP09123842")
     */
    public String generateProductCode() {
        return generateCode("SP");
    }
    
    /**
     * Generates an order code with "DH" prefix.
     * 
     * @return A generated order code (e.g., "DH87654321")
     */
    public String generateOrderCode() {
        return generateCode("DH");
    }
}