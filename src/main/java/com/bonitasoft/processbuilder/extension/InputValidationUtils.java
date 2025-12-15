package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.connector.ConnectorValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Utility class for common input parameter validation checks (e.g., positive numbers, non-null values)
 * typically used within Bonita connectors.
 * <p>
 * NOTE: This utility assumes the calling class has an accessible 'getInputParameter(String name)' method.
 * </p>
 */
public class InputValidationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputValidationUtils.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private InputValidationUtils() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

    /**
     * Parses a String input to a positive Long value with comprehensive validation and logging.
     * <p>
     * This method handles various edge cases for String-to-Long conversion:
     * </p>
     * <ul>
     *   <li>If input is {@code null} → returns {@code Optional.empty()}</li>
     *   <li>If trimmed input is empty or equals "null" (case-insensitive) → returns {@code Optional.empty()}</li>
     *   <li>If parsing fails (NumberFormatException) → returns {@code Optional.empty()}</li>
     *   <li>If parsed value is not positive (≤ 0) → returns {@code Optional.empty()}</li>
     *   <li>If valid positive Long → returns {@code Optional.of(value)}</li>
     * </ul>
     * <p>
     * All validation failures are logged with appropriate level (debug for null/empty, warn for invalid values).
     * </p>
     *
     * @param input     the String value to parse (may be null)
     * @param paramName the parameter name for logging purposes (used in log messages)
     * @return an {@code Optional<Long>} containing the positive Long value if valid,
     *         or {@code Optional.empty()} if the input is null, empty, "null", unparseable, or not positive
     */
    public static Optional<Long> parseStringToPositiveLong(String input, String paramName) {
        // Handle null input
        if (input == null) {
            LOGGER.debug("Input '{}' is null.", paramName);
            return Optional.empty();
        }

        String trimmedInput = input.trim();

        // Handle empty or "null" string
        if (trimmedInput.isEmpty() || "null".equalsIgnoreCase(trimmedInput)) {
            LOGGER.debug("Input '{}' value '{}' treated as null or empty.", paramName, trimmedInput);
            return Optional.empty();
        }

        // Attempt to parse to Long
        Long parsedValue;
        try {
            parsedValue = Long.parseLong(trimmedInput);
        } catch (NumberFormatException e) {
            LOGGER.warn("Error converting '{}' value '{}' to Long. Invalid number format.", paramName, trimmedInput);
            return Optional.empty();
        }

        // Validate positive value
        if (parsedValue <= 0L) {
            LOGGER.warn("Invalid input: '{}' must be positive but received: {}", paramName, parsedValue);
            return Optional.empty();
        }

        LOGGER.debug("Successfully parsed '{}' to positive Long: {}", paramName, parsedValue);
        return Optional.of(parsedValue);
    }

    /**
     * Parses a String input to a Long value with comprehensive validation and logging.
     * <p>
     * This method handles various edge cases for String-to-Long conversion:
     * </p>
     * <ul>
     *   <li>If input is {@code null} → returns {@code null}</li>
     *   <li>If trimmed input is empty or equals "null" (case-insensitive) → returns {@code null}</li>
     *   <li>If parsing fails (NumberFormatException) → returns {@code 0L}</li>
     *   <li>If valid number → returns the parsed Long value</li>
     * </ul>
     * <p>
     * All validation failures are logged with appropriate level (debug for null/empty, error for parse failures).
     * </p>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * Long actionIdLong = InputValidationUtils.parseStringToLong(actionPersistenceIdInput, "actionPersistenceIdInput");
     * if (actionIdLong == null || actionIdLong <= 0L) {
     *     logger.warn("Invalid input: '{}' is null or not positive.", "actionPersistenceIdInput");
     *     return Collections.emptyList();
     * }
     * }</pre>
     *
     * @param input     the String value to parse (may be null)
     * @param paramName the parameter name for logging purposes (used in log messages)
     * @return the parsed Long value, {@code null} if input is null/empty/"null",
     *         or {@code 0L} if the input cannot be parsed as a number
     */
    public static Long parseStringToLong(String input, String paramName) {
        // Handle null input
        if (input == null) {
            LOGGER.debug("Input '{}' is null.", paramName);
            return null;
        }

        String trimmedInput = input.trim();

        // Handle empty or "null" string
        if (trimmedInput.isEmpty() || "null".equalsIgnoreCase(trimmedInput)) {
            LOGGER.debug("Input '{}' value '{}' treated as null or empty.", paramName, trimmedInput);
            return null;
        }

        // Attempt to parse to Long
        try {
            Long parsedValue = Long.parseLong(trimmedInput);
            LOGGER.debug("Successfully parsed '{}' to Long: {}", paramName, parsedValue);
            return parsedValue;
        } catch (NumberFormatException e) {
            LOGGER.error("Error converting '{}' value '{}' to Long. Treating as invalid ID.", paramName, trimmedInput, e);
            return 0L;
        }
    }

    /**
    * Helper method to check if a numeric input parameter is a positive value (greater than zero).
    * <p>
    * This method leverages the {@code doubleValue()} method available on all {@code Number} subclasses 
    * for a safe, consistent numerical comparison against zero, regardless of whether T is 
    * Integer, Long, Double, etc.
    * </p>
    * * @param <T> The generic numeric type, constrained to extend {@code Number} and {@code Comparable}.
    * @param inputName The display name of the input parameter (used in error messages).
    * @param value The numeric value of the input parameter retrieved from the connector context.
    * @param typeName The simple name of the expected type (e.g., "integer", "long") for error messages.
    * @throws ConnectorValidationException if the {@code value} is {@code null} or less than or equal to zero.
    */
    private static <T extends Number & Comparable<T>> void checkPositiveNumber(final String inputName, final T value, final String typeName) throws ConnectorValidationException {
        if (value == null || value.doubleValue() <= 0) {
            throw new ConnectorValidationException(String.format("Mandatory parameter '%s' must be a positive %s but is '%s'.", inputName, typeName, value));
        }
    }

    /**
     * Checks if a given input parameter is a positive Integer.
     * @param inputName The name of the input parameter.
     * @param inputGetter A functional interface (Supplier) to retrieve the input parameter value.
     * @throws ConnectorValidationException if the parameter is not a positive Integer.
     */
    public static void checkPositiveIntegerInput(final String inputName, final java.util.function.Supplier<Object> inputGetter) throws ConnectorValidationException {
        try {
            Integer value = (Integer) inputGetter.get();
            checkPositiveNumber(inputName, value, "integer");
        } catch (ClassCastException e) {
            throw new ConnectorValidationException(String.format("'%s' parameter must be an Integer", inputName));
        }
    }

    /**
     * Checks if a given input parameter is a positive Long.
     * @param inputName The name of the input parameter.
     * @param inputGetter A functional interface (Supplier) to retrieve the input parameter value.
     * @throws ConnectorValidationException if the parameter is not a positive Long.
     */
    public static void checkPositiveLongInput(final String inputName, final java.util.function.Supplier<Object> inputGetter) throws ConnectorValidationException {
        try {
            Long value = (Long) inputGetter.get();
            checkPositiveNumber(inputName, value, "long");
        } catch (ClassCastException e) {
            throw new ConnectorValidationException(String.format("'%s' parameter must be an Long", inputName));
        }
    }
}