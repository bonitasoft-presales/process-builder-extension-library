package com.bonitasoft.processbuilder.extension;

/**
 * Utility class providing common String manipulation methods, focusing on
 * normalization and case formatting.
 * <p>
 * This class is designed to be non-instantiable and should only be accessed
 * via static methods.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class PBStringUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private PBStringUtils() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

    // ----------------------------------------------------------------------
    // Case Normalization Methods
    // ----------------------------------------------------------------------

    /**
     * Normalizes the input String to Title Case format: the first letter is 
     * capitalized, and all subsequent letters are lowercased.
     * <p>
     * This implementation is optimized to minimize intermediate String object creation.
     * </p>
     * <ul>
     * <li>{@code "CATEGORY"} becomes {@code "Category"}</li>
     * <li>{@code "category"} becomes {@code "Category"}</li>
     * <li>{@code "CaTegory"} becomes {@code "Category"}</li>
     * <li>{@code null} remains {@code null}</li>
     * <li>{@code ""} remains {@code ""}</li>
     * <li>{@code "a"} becomes {@code "A"}</li>
     * </ul>
     * @param str The string to normalize.
     * @return The string in Title Case, or the original string if null or empty.
     */
    public static String normalizeTitleCase(String str) {
        
        // Handle null or empty cases first (fastest check)
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        // Handle single character case separately for clarity, though it works below
        if (str.length() == 1) {
            return str.toUpperCase();
        }

        // 1. Get the first character, convert to uppercase.
        String firstChar = String.valueOf(str.charAt(0)).toUpperCase();
        
        // 2. Get the rest of the string and convert it to lowercase.
        String rest = str.substring(1).toLowerCase();
        
        // 3. Return the combined string.
        return firstChar + rest;
    }

    /**
     * Converts a string from human-readable format (e.g., spaces) to 
     * {@code snake_case} format.
     * 
     * The conversion process involves:
     * <ul>
     * <li>Converting the entire string to lowercase.</li>
     * <li>Replacing all space characters (' ') with underscores ('_').</li>
     * </ul>
     * 
     * <ul>
     * <li>{@code "Bonita and delete"} becomes {@code "bonita_and_delete"}</li>
     * <li>{@code "A Long Name"} becomes {@code "a_long_name"}</li>
     * <li>{@code null} remains {@code null}</li>
     * </ul>
     *
     * @param input The string to convert.
     * @return The string in snake_case format, or the original string if null.
     */
    public static String toLowerSnakeCase(String input) {
        if (input == null) {
            return null;
        }
        
        // 1. Convert to lowercase.
        String lowerCase = input.toLowerCase(); 
        
        // 2. Replace all spaces with underscores.
        String snakeCase = lowerCase.replace(' ', '_'); 
        
        return snakeCase;
    }

     /**
     * Converts a string from human-readable format (e.g., spaces) to 
     * {@code snake_case} format.
     * 
     * The conversion process involves:
     * <ul>
     * <li>Converting the entire string to uppercase.</li>
     * <li>Replacing all space characters (' ') with underscores ('_').</li>
     * </ul>
     * 
     * <ul>
     * <li>{@code "Bonita and delete"} becomes {@code "BONITA_AND_DELETE"}</li>
     * <li>{@code "A Long Name"} becomes {@code "A_LONG_NAME"}</li>
     * <li>{@code null} remains {@code null}</li>
     * </ul>
     *
     * @param input The string to convert.
     * @return The string in snake_case format, or the original string if null.
     */
    public static String toUpperSnakeCase(String input) {
        if (input == null) {
            return null;
        }
        
        // 1. Convert to uppercase.
        String upperCase = input.toUpperCase(); 
        
        // 2. Replace all spaces with underscores.
        String snakeCase = upperCase.replace(' ', '_'); 
        
        return snakeCase;
    }
}