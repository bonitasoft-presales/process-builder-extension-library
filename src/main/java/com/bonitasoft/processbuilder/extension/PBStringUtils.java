package com.bonitasoft.processbuilder.extension;

/**
 * Utility class providing common String manipulation methods, focusing on
 * normalization and case formatting.
 * * This class cannot be instantiated.
 */
public final class PBStringUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PBStringUtils() {
        throw new UnsupportedOperationException("This is a PBStringUtils class and cannot be instantiated.");
    }

    // ----------------------------------------------------------------------
    // Case Normalization Methods
    // ----------------------------------------------------------------------

    /**
     * Normalizes the input String to Title Case format: the first letter is 
     * capitalized, and all subsequent letters are lowercased.
     * * This implementation is optimized to minimize intermediate String object creation.
     * * Examples:
     * <ul>
     * <li>"CATEGORY" becomes "Category"</li>
     * <li>"category" becomes "Category"</li>
     * <li>"CaTegory" becomes "Category"</li>
     * <li>null becomes null</li>
     * <li>"" becomes ""</li>
     * </ul>
     * * @param str The string to normalize.
     * @return The string in Title Case, or the original string if null or empty.
     */
    public static String normalizeTitleCase(String str) {
        
        // Handle null or empty cases first (fastest check)
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        // 1. Get the first character, convert to uppercase.
        // Using String.valueOf(char) is cleaner than substring(0, 1).toUpperCase().
        String firstChar = String.valueOf(str.charAt(0)).toUpperCase();
        
        // 2. Get the rest of the string and convert it to lowercase.
        // This is where the optimization lies: we only lowercase the remaining fragment.
        String rest = str.substring(1).toLowerCase();
        
        // 3. Return the combined string.
        return firstChar + rest;
    }
}