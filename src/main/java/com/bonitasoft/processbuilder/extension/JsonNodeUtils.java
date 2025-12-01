package com.bonitasoft.processbuilder.extension;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.StreamSupport;

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
     * @return {@code true} if the condition is satisfied, {@code false} otherwise
     */
    public static boolean evaluateCondition(Object currentValue, String operator,
                                            Object expectedValue) {

        if (operator == null) {
            LOGGER.warn("Operator is null. Defaulting to false.");
            return false;
        }

        String op = operator.toLowerCase();

        return switch (op) {
            case OP_EQUALS, OP_EQUALS_SYMBOL -> evaluateEquals(currentValue, expectedValue);
            case OP_NOT_EQUALS, OP_NOT_EQUALS_SYMBOL -> evaluateNotEquals(currentValue, expectedValue);
            case OP_CONTAINS -> evaluateContains(currentValue, expectedValue);
            case OP_GREATER_THAN, OP_GREATER_THAN_SYMBOL ->
                evaluateComparison(currentValue, expectedValue, 1);
            case OP_LESS_THAN, OP_LESS_THAN_SYMBOL ->
                evaluateComparison(currentValue, expectedValue, -1);
            case OP_GREATER_OR_EQUAL, OP_GREATER_OR_EQUAL_SYMBOL ->
                evaluateComparisonOrEqual(currentValue, expectedValue, 1);
            case OP_LESS_OR_EQUAL, OP_LESS_OR_EQUAL_SYMBOL ->
                evaluateComparisonOrEqual(currentValue, expectedValue, -1);
            default -> {
                LOGGER.warn("Unknown operator: {}. Defaulting to false.", operator);
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
     * @return a negative integer, zero, or a positive integer as the actual value
     *         is less than, equal to, or greater than the expected value
     * @throws NullPointerException if actual or expected is null
     */
    @SuppressWarnings("unchecked")
    public static int compareValues(Comparable<?> actual, Comparable<?> expected) {
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

    /**
     * Evaluates all conditions in a JSON array, returning true only if ALL conditions are met.
     * <p>
     * This method is designed for evaluating process conditions where each condition references
     * a step and field, compares against an expected value using an operator. The actual data
     * retrieval is delegated to the caller via the {@code dataValueResolver} function.
     * </p>
     * <p>
     * Each condition in the array must have the following structure:
     * </p>
     * <pre>{@code
     * {
     *   "stepRef": "step_identifier",
     *   "variableName": "field_name",
     *   "variableOperator": "equals",
     *   "variableValue": "expected_value"
     * }
     * }</pre>
     * <p>
     * The method short-circuits on the first failing condition (uses {@code allMatch}).
     * </p>
     *
     * @param conditionsNode    the JSON array containing condition objects (may be null or empty)
     * @param dataValueResolver a function that takes (fieldRef, stepRef) and returns the current
     *                          data value as a String, or null if not found
     * @return {@code true} if all conditions are met or if conditionsNode is null/empty,
     *         {@code false} if any condition fails or has invalid structure
     */
    public static boolean evaluateAllConditions(
            JsonNode conditionsNode,
            BiFunction<String, String, String> dataValueResolver) {

        // Handle null or empty conditions array - considered as "no conditions to fail"
        if (conditionsNode == null || !conditionsNode.isArray() || conditionsNode.isEmpty()) {
            LOGGER.debug("No conditions to evaluate or conditionsNode is not an array. Returning true.");
            return true;
        }

        // Validate dataValueResolver
        if (dataValueResolver == null) {
            LOGGER.error("dataValueResolver function is null. Cannot evaluate conditions.");
            return false;
        }

        // ALL conditions must be true (allMatch short-circuits on first false)
        return StreamSupport.stream(conditionsNode.spliterator(), false)
            .allMatch(condition -> evaluateSingleCondition(condition, dataValueResolver));
    }

    /**
     * Evaluates a single condition from a JSON object.
     *
     * @param condition         the condition JSON object
     * @param dataValueResolver the function to resolve data values
     * @return true if the condition is met, false otherwise
     */
    private static boolean evaluateSingleCondition(
            JsonNode condition,
            BiFunction<String, String, String> dataValueResolver) {

        // Extract condition fields
        String stepRef = getTextOrNull(condition, "stepRef");
        String fieldRef = getTextOrNull(condition, "fieldRef");
        String operator = getTextOrNull(condition, "operator");
        JsonNode expectedValueNode = condition.get("value");

        LOGGER.debug("Evaluating condition: stepRef='{}', fieldRef='{}', operator='{}'",
                    stepRef, fieldRef, operator);

        // Validate operator
        if (operator == null || operator.isBlank()) {
            LOGGER.error("Invalid operator for field '{}'. Operator is null or blank.", fieldRef);
            return false;
        }

        // Validate required fields
        if (fieldRef == null || fieldRef.isBlank() || stepRef == null || stepRef.isBlank()) {
            LOGGER.error("Missing fieldRef or stepRef for condition. fieldRef='{}', stepRef='{}'",
                        fieldRef, stepRef);
            return false;
        }

        // Retrieve current value using the resolver function
        String currentValue;
        try {
            currentValue = dataValueResolver.apply(fieldRef, stepRef);
        } catch (Exception e) {
            LOGGER.error("Error retrieving data for field '{}' in step '{}': {}",
                        fieldRef, stepRef, e.getMessage());
            return false;
        }

        // Check if data was found
        if (currentValue == null) {
            LOGGER.warn("No data found for field '{}' in step '{}'. Condition fails.",
                       fieldRef, stepRef);
            return false;
        }

        // Convert expected value and evaluate
        Object expectedValue = convertJsonNodeToObject(expectedValueNode);
        boolean conditionMet = evaluateCondition(currentValue, operator, expectedValue);

        LOGGER.info("Condition '{}': current='{}' {} expected='{}' -> {}",
                   fieldRef, currentValue, operator, expectedValue,
                   conditionMet ? "PASSED" : "FAILED");

        return conditionMet;
    }

    /**
     * Safely extracts a text value from a JSON node path.
     *
     * @param node the parent node
     * @param fieldName the field name to extract
     * @return the text value, or null if not present or not textual
     */
    private static String getTextOrNull(JsonNode node, String fieldName) {
        if (node == null) {
            return null;
        }
        JsonNode fieldNode = node.path(fieldName);
        if (fieldNode.isMissingNode() || fieldNode.isNull()) {
            return null;
        }
        return fieldNode.asText(null);
    }

    // -------------------------------------------------------------------------
    // Private helper methods for condition evaluation
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
     * @param direction     1 for greater than, -1 for less than
     * @return true if the comparison holds
     */
    private static boolean evaluateComparison(Object currentValue, Object expectedValue,
                                              int direction) {
        if (currentValue instanceof Comparable<?> currentComp
            && expectedValue instanceof Comparable<?> expectedComp) {
            int result = compareValues(currentComp, expectedComp);
            return direction > 0 ? result > 0 : result < 0;
        }
        return false;
    }

    /**
     * Evaluates a comparison with equality (greater/less than or equal).
     *
     * @param currentValue  the current value
     * @param expectedValue the expected value
     * @param direction     1 for greater or equal, -1 for less or equal
     * @return true if the comparison holds
     */
    private static boolean evaluateComparisonOrEqual(Object currentValue, Object expectedValue,
                                                     int direction) {
        if (currentValue instanceof Comparable<?> currentComp
            && expectedValue instanceof Comparable<?> expectedComp) {
            int result = compareValues(currentComp, expectedComp);
            return direction > 0 ? result >= 0 : result <= 0;
        }
        return false;
    }
}
