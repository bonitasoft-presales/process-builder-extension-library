package com.bonitasoft.processbuilder.extension;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Utility class for working with Jackson {@link JsonNode} objects and evaluating conditions.
 * <p>
 * Provides methods for:
 * </p>
 * <ul>
 *   <li>Converting {@link JsonNode} instances to their corresponding Java types</li>
 *   <li>Evaluating comparison conditions between values</li>
 *   <li>Safely comparing {@link Comparable} values of potentially different types</li>
 * </ul>
 * <p>
 * This class is designed to be non-instantiable and should only be accessed via static methods.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class JsonNodeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonNodeUtils.class);

    /**
     * Operator constant for equals comparison.
     */
    public static final String OP_EQUALS = "equals";

    /**
     * Operator constant for equals comparison (symbol form).
     */
    public static final String OP_EQUALS_SYMBOL = "==";

    /**
     * Operator constant for not equals comparison.
     */
    public static final String OP_NOT_EQUALS = "notequals";

    /**
     * Operator constant for not equals comparison (symbol form).
     */
    public static final String OP_NOT_EQUALS_SYMBOL = "!=";

    /**
     * Operator constant for contains comparison.
     */
    public static final String OP_CONTAINS = "contains";

    /**
     * Operator constant for greater than comparison.
     */
    public static final String OP_GREATER_THAN = "greaterthan";

    /**
     * Operator constant for greater than comparison (symbol form).
     */
    public static final String OP_GREATER_THAN_SYMBOL = ">";

    /**
     * Operator constant for less than comparison.
     */
    public static final String OP_LESS_THAN = "lessthan";

    /**
     * Operator constant for less than comparison (symbol form).
     */
    public static final String OP_LESS_THAN_SYMBOL = "<";

    /**
     * Operator constant for greater or equal comparison.
     */
    public static final String OP_GREATER_OR_EQUAL = "greaterorequal";

    /**
     * Operator constant for greater or equal comparison (symbol form).
     */
    public static final String OP_GREATER_OR_EQUAL_SYMBOL = ">=";

    /**
     * Operator constant for less or equal comparison.
     */
    public static final String OP_LESS_OR_EQUAL = "lessorequal";

    /**
     * Operator constant for less or equal comparison (symbol form).
     */
    public static final String OP_LESS_OR_EQUAL_SYMBOL = "<=";

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern
     */
    private JsonNodeUtils() {
        throw new UnsupportedOperationException(
            "This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated."
        );
    }

    /**
     * Converts a {@link JsonNode} to its corresponding Java object type.
     * <p>
     * The conversion follows these rules:
     * </p>
     * <ul>
     *   <li>{@code null} or {@code NullNode} returns {@code null}</li>
     *   <li>Text nodes return {@link String}</li>
     *   <li>Boolean nodes return {@link Boolean}</li>
     *   <li>Integer nodes return {@link Integer}</li>
     *   <li>Long nodes return {@link Long}</li>
     *   <li>Double or Float nodes return {@link Double}</li>
     *   <li>Array or Object nodes return their JSON string representation</li>
     *   <li>Any other type returns the text representation</li>
     * </ul>
     *
     * @param node the JsonNode to convert (may be null)
     * @return the converted Java object, or null if the node is null or represents a JSON null
     */
    public static Object convertJsonNodeToObject(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return node.asText();
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isInt()) {
            return node.asInt();
        }
        if (node.isLong()) {
            return node.asLong();
        }
        if (node.isDouble() || node.isFloat()) {
            return node.asDouble();
        }
        if (node.isArray() || node.isObject()) {
            // For complex types, return the JSON string representation
            return node.toString();
        }
        // Default fallback for any other node type
        return node.asText();
    }

    /**
     * Evaluates a condition comparing two values using the specified operator.
     * <p>
     * Supported operators (case-insensitive):
     * </p>
     * <ul>
     *   <li>{@code equals} or {@code ==}: equality comparison</li>
     *   <li>{@code notequals} or {@code !=}: inequality comparison</li>
     *   <li>{@code contains}: string containment check</li>
     *   <li>{@code greaterthan} or {@code >}: greater than comparison</li>
     *   <li>{@code lessthan} or {@code <}: less than comparison</li>
     *   <li>{@code greaterorequal} or {@code >=}: greater than or equal comparison</li>
     *   <li>{@code lessorequal} or {@code <=}: less than or equal comparison</li>
     * </ul>
     *
     * @param currentValue  the current value to compare (may be null)
     * @param operator      the comparison operator (case-insensitive)
     * @param expectedValue the expected value to compare against (may be null)
     * @param logger        the logger for warning messages (if null, uses class logger)
     * @return {@code true} if the condition is satisfied, {@code false} otherwise
     */
    public static boolean evaluateCondition(Object currentValue, String operator,
                                            Object expectedValue, Logger logger) {
        Logger log = logger != null ? logger : LOGGER;

        if (operator == null) {
            log.warn("Operator is null. Defaulting to false.");
            return false;
        }

        String op = operator.toLowerCase();

        return switch (op) {
            case OP_EQUALS, OP_EQUALS_SYMBOL -> evaluateEquals(currentValue, expectedValue);
            case OP_NOT_EQUALS, OP_NOT_EQUALS_SYMBOL -> evaluateNotEquals(currentValue, expectedValue);
            case OP_CONTAINS -> evaluateContains(currentValue, expectedValue);
            case OP_GREATER_THAN, OP_GREATER_THAN_SYMBOL ->
                evaluateComparison(currentValue, expectedValue, log, 1);
            case OP_LESS_THAN, OP_LESS_THAN_SYMBOL ->
                evaluateComparison(currentValue, expectedValue, log, -1);
            case OP_GREATER_OR_EQUAL, OP_GREATER_OR_EQUAL_SYMBOL ->
                evaluateComparisonOrEqual(currentValue, expectedValue, log, 1);
            case OP_LESS_OR_EQUAL, OP_LESS_OR_EQUAL_SYMBOL ->
                evaluateComparisonOrEqual(currentValue, expectedValue, log, -1);
            default -> {
                log.warn("Unknown operator: {}. Defaulting to false.", operator);
                yield false;
            }
        };
    }

    /**
     * Compares two {@link Comparable} values in a type-safe manner.
     * <p>
     * Comparison rules:
     * </p>
     * <ul>
     *   <li>If both values are {@link Number}, they are converted to {@link Double} for comparison</li>
     *   <li>If both values are of the same type, they are compared directly</li>
     *   <li>If types differ (and are not both numbers), they are compared as strings</li>
     * </ul>
     *
     * @param actual   the actual value to compare (must not be null)
     * @param expected the expected value to compare against (must not be null)
     * @param logger   the logger for debugging (if null, uses class logger)
     * @return a negative integer, zero, or a positive integer as the actual value
     *         is less than, equal to, or greater than the expected value
     * @throws NullPointerException if actual or expected is null
     */
    @SuppressWarnings("unchecked")
    public static int compareValues(Comparable<?> actual, Comparable<?> expected, Logger logger) {
        Objects.requireNonNull(actual, "Actual value cannot be null");
        Objects.requireNonNull(expected, "Expected value cannot be null");

        // If both are numbers, convert to Double for consistent comparison
        if (actual instanceof Number actualNum && expected instanceof Number expectedNum) {
            Double actualDouble = actualNum.doubleValue();
            Double expectedDouble = expectedNum.doubleValue();
            return actualDouble.compareTo(expectedDouble);
        }

        // If they are the same type, compare directly
        if (actual.getClass() == expected.getClass()) {
            return ((Comparable<Object>) actual).compareTo(expected);
        }

        // Different types: compare as strings
        return actual.toString().compareTo(expected.toString());
    }

    // -------------------------------------------------------------------------
    // Private helper methods
    // -------------------------------------------------------------------------

    /**
     * Evaluates equality between two objects.
     *
     * @param currentValue  the current value
     * @param expectedValue the expected value
     * @return true if the values are equal
     */
    private static boolean evaluateEquals(Object currentValue, Object expectedValue) {
        return Objects.equals(currentValue, expectedValue);
    }

    /**
     * Evaluates inequality between two objects.
     *
     * @param currentValue  the current value
     * @param expectedValue the expected value
     * @return true if the values are not equal
     */
    private static boolean evaluateNotEquals(Object currentValue, Object expectedValue) {
        return !Objects.equals(currentValue, expectedValue);
    }

    /**
     * Evaluates if the current value contains the expected value as a substring.
     *
     * @param currentValue  the current value
     * @param expectedValue the expected value
     * @return true if currentValue contains expectedValue
     */
    private static boolean evaluateContains(Object currentValue, Object expectedValue) {
        if (currentValue == null) {
            return false;
        }
        String currentStr = currentValue.toString();
        String expectedStr = expectedValue != null ? expectedValue.toString() : "";
        return currentStr.contains(expectedStr);
    }

    /**
     * Evaluates a comparison (greater than or less than).
     *
     * @param currentValue  the current value
     * @param expectedValue the expected value
     * @param logger        the logger
     * @param direction     1 for greater than, -1 for less than
     * @return true if the comparison holds
     */
    private static boolean evaluateComparison(Object currentValue, Object expectedValue,
                                              Logger logger, int direction) {
        if (currentValue instanceof Comparable<?> currentComp
            && expectedValue instanceof Comparable<?> expectedComp) {
            int result = compareValues(currentComp, expectedComp, logger);
            return direction > 0 ? result > 0 : result < 0;
        }
        return false;
    }

    /**
     * Evaluates a comparison with equality (greater/less than or equal).
     *
     * @param currentValue  the current value
     * @param expectedValue the expected value
     * @param logger        the logger
     * @param direction     1 for greater or equal, -1 for less or equal
     * @return true if the comparison holds
     */
    private static boolean evaluateComparisonOrEqual(Object currentValue, Object expectedValue,
                                                     Logger logger, int direction) {
        if (currentValue instanceof Comparable<?> currentComp
            && expectedValue instanceof Comparable<?> expectedComp) {
            int result = compareValues(currentComp, expectedComp, logger);
            return direction > 0 ? result >= 0 : result <= 0;
        }
        return false;
    }
}
