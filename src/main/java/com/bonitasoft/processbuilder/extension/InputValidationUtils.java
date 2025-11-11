package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.connector.ConnectorValidationException;

/**
 * Utility class for common input parameter validation checks (e.g., positive numbers, non-null values)
 * typically used within Bonita connectors.
 * <p>
 * NOTE: This utility assumes the calling class has an accessible 'getInputParameter(String name)' method.
 * </p>
 */
public class InputValidationUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private InputValidationUtils() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
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