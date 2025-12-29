package com.bonitasoft.processbuilder.extension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * <li>Converting {@link JsonNode} instances to their corresponding Java types</li>
 * <li>Evaluating comparison conditions between values</li>
 * <li>Safely comparing {@link Comparable} values of potentially different types</li>
 * <li>Safely navigating JSON structures using a dot-separated path.</li>
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
     * Operator constant for is empty check.
     * <p>
     * Evaluates to {@code true} if the value is null, an empty string,
     * or a string containing only whitespace characters.
     * </p>
     */
    public static final String OP_IS_EMPTY = "is_empty";

    /**
     * Operator constant for is not empty check.
     * <p>
     * Evaluates to {@code true} if the value is not null, not an empty string,
     * and not a string containing only whitespace characters.
     * </p>
     */
    public static final String OP_IS_NOT_EMPTY = "is_not_empty";

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
     * <li>{@code null} or {@code NullNode} returns {@code null}</li>
     * <li>Text nodes return {@link String}</li>
     * <li>Boolean nodes return {@link Boolean}</li>
     * <li>Integer nodes return {@link Integer}</li>
     * <li>Long nodes return {@link Long}</li>
     * <li>Double or Float nodes return {@link Double}</li>
     * <li>Array or Object nodes return their JSON string representation</li>
     * <li>Any other type returns the text representation</li>
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
     * Safely retrieves the value of a field from a JSON structure using a dot-separated path.
     * <p>
     * This method prevents {@code NullPointerException} by safely navigating through nested objects.
     * </p>
     *
     * @param rootNode the starting {@link JsonNode} (e.g., the root of the JSON structure).
     * @param path the dot-separated path to the desired field (e.g., "subject", "recipients.type").
     * @return the {@link JsonNode} representing the value at the specified path, or {@code null} if the path is invalid,
     * the field does not exist, or the value is JSON null.
     */
    public static JsonNode getValueByPath(JsonNode rootNode, String path) {
        if (rootNode == null || path == null || path.isBlank()) {
            return null;
        }

        String[] pathSegments = path.split("\\.");
        JsonNode currentNode = rootNode;

        for (String segment : pathSegments) {
            if (currentNode == null || !currentNode.isObject()) {
                // If the current node is null or not an object, the path is invalid.
                return null;
            }

            // .get(segment) safely returns null if the field does not exist
            currentNode = currentNode.get(segment.trim());
        }

        // Return the final node. It might be null (if path failed) or NullNode (if value was explicit null).
        return (currentNode == null || currentNode.isNull()) ? null : currentNode;
    }

    /**
     * Safely retrieves the value of a field from a JSON string using a dot-separated path.
     * <p>
     * This is a convenience method that first converts the JSON string to a {@link JsonNode}
     * and then navigates to the specified path.
     * </p>
     *
     * @param jsonString the JSON string to parse (e.g., "{\"name\": \"John\", \"address\": {\"city\": \"Madrid\"}}").
     * @param path       the dot-separated path to the desired field (e.g., "address.city").
     * @return the {@link JsonNode} representing the value at the specified path, or {@code null} if the JSON
     *         is invalid, the path is invalid, the field does not exist, or the value is JSON null.
     */
    public static JsonNode getValueByPath(String jsonString, String path) {
        JsonNode rootNode = convertStringToJsonNode(jsonString);
        if (rootNode == null) {
            return null;
        }
        return getValueByPath(rootNode, path);
    }

    /**
     * Retrieves the text value at the specified path from a {@link JsonNode}.
     * <p>
     * This is a convenience method that navigates to the specified path and returns the value
     * as a plain {@link String} (without JSON quotes). Unlike {@link #getValueByPath(JsonNode, String)}
     * which returns a {@link JsonNode}, this method directly returns the text representation.
     * </p>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * JsonNode root = objectMapper.readTree("{\"user\": {\"name\": \"John\"}}");
     * String name = JsonNodeUtils.getTextValueByPath(root, "user.name");
     * // Returns: "John" (without quotes)
     * }</pre>
     *
     * @param rootNode the starting {@link JsonNode} (e.g., the root of the JSON structure).
     * @param path     the dot-separated path to the desired field (e.g., "user.name", "address.city").
     * @return the text value at the specified path, or {@code null} if the path is invalid,
     *         the field does not exist, or the value is JSON null.
     */
    public static String getTextValueByPath(JsonNode rootNode, String path) {
        JsonNode node = getValueByPath(rootNode, path);
        return (node != null) ? node.asText() : null;
    }

    /**
     * Retrieves the text value at the specified path from a JSON string.
     * <p>
     * This is a convenience method that first parses the JSON string, navigates to the specified path,
     * and returns the value as a plain {@link String} (without JSON quotes).
     * </p>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * String json = "{\"address\": {\"city\": \"Madrid\", \"zip\": \"28001\"}}";
     * String city = JsonNodeUtils.getTextValueByPath(json, "address.city");
     * // Returns: "Madrid" (without quotes)
     * }</pre>
     *
     * @param jsonString the JSON string to parse (e.g., "{\"name\": \"John\"}").
     * @param path       the dot-separated path to the desired field (e.g., "address.city").
     * @return the text value at the specified path, or {@code null} if the JSON is invalid,
     *         the path is invalid, the field does not exist, or the value is JSON null.
     */
    public static String getTextValueByPath(String jsonString, String path) {
        JsonNode node = getValueByPath(jsonString, path);
        return (node != null) ? node.asText() : null;
    }

    /**
     * Converts a JSON string to a {@link JsonNode}.
     * <p>
     * This method safely parses a JSON string and returns the corresponding {@link JsonNode}.
     * If the input is null, empty, or not valid JSON, it returns {@code null} and logs the error.
     * </p>
     *
     * @param jsonString the JSON string to parse (may be null or empty).
     * @return the parsed {@link JsonNode}, or {@code null} if the input is null, empty, or invalid JSON.
     */
    public static JsonNode convertStringToJsonNode(String jsonString) {
        if (jsonString == null || jsonString.isBlank()) {
            LOGGER.debug("JSON string is null or blank. Returning null.");
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            LOGGER.debug("Successfully parsed JSON string. Node type: {}", jsonNode.getNodeType());
            return jsonNode;
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing JSON string: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Evaluates a condition comparing two values using the specified operator.
     * <p>
     * Supported operators (case-insensitive):
     * </p>
     * <ul>
     * <li>{@code equals} or {@code ==}: equality comparison</li>
     * <li>{@code notequals} or {@code !=}: inequality comparison</li>
     * <li>{@code contains}: string containment check</li>
     * <li>{@code greaterthan} or {@code >}: greater than comparison</li>
     * <li>{@code lessthan} or {@code <}: less than comparison</li>
     * <li>{@code greaterorequal} or {@code >=}: greater than or equal comparison</li>
     * <li>{@code lessorequal} or {@code <=}: less than or equal comparison</li>
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
            case OP_IS_EMPTY -> evaluateIsEmpty(currentValue);
            case OP_IS_NOT_EMPTY -> evaluateIsNotEmpty(currentValue);
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
     * <li>If both values are {@link Number}, they are converted to {@link Double} for consistent comparison</li>
     * <li>If both values are of the same type, they are compared directly</li>
     * <li>If types differ (and are not both numbers), they are compared as strings</li>
     * </ul>
     *
     * @param actual   the actual value to compare (must not be null)
     * @param expected the expected value to compare against (must not be null)
     * @return a negative integer, zero, or a positive integer as the actual value
     * is less than, equal to, or greater than the expected value
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
     * "stepRef": "step_identifier",
     * "variableName": "field_name",
     * "variableOperator": "equals",
     * "variableValue": "expected_value"
     * }
     * }</pre>
     * <p>
     * The method short-circuits on the first failing condition (uses {@code allMatch}).
     * </p>
     *
     * @param conditionsNode    the JSON array containing condition objects (may be null or empty)
     * @param dataValueResolver a function that takes (fieldRef, stepRef) and returns the current
     * data value as a String, or null if not found
     * @return {@code true} if all conditions are met or if conditionsNode is null/empty,
     * {@code false} if any condition fails or has invalid structure
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
     * Evaluates if the current value is empty.
     * <p>
     * A value is considered empty if it is:
     * </p>
     * <ul>
     * <li>{@code null}</li>
     * <li>An empty string ({@code ""})</li>
     * <li>A string containing only whitespace characters</li>
     * </ul>
     * <p>
     * <b>Note:</b> Primitive types ({@code long}, {@code int}, {@code boolean}, etc.) are
     * automatically boxed by Java when passed to this method. Their string representation
     * (e.g., "123", "true", "false") is never blank, so they always return {@code false}.
     * </p>
     *
     * @param currentValue the current value to check (primitives are auto-boxed)
     * @return {@code true} if the value is null, empty, or blank; {@code false} otherwise
     */
    private static boolean evaluateIsEmpty(Object currentValue) {
        if (currentValue == null) {
            return true;
        }
        String strValue = currentValue.toString();
        return strValue.isBlank();
    }

    /**
     * Evaluates if the current value is not empty.
     * <p>
     * A value is considered not empty if it is not null and contains
     * at least one non-whitespace character.
     * </p>
     * <p>
     * <b>Note:</b> Primitive types ({@code long}, {@code int}, {@code boolean}, etc.) are
     * automatically boxed by Java when passed to this method. Their string representation
     * is never blank, so they always return {@code true}.
     * </p>
     *
     * @param currentValue the current value to check (primitives are auto-boxed)
     * @return {@code true} if the value is not null and not blank; {@code false} otherwise
     */
    private static boolean evaluateIsNotEmpty(Object currentValue) {
        return !evaluateIsEmpty(currentValue);
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

    // -------------------------------------------------------------------------
    // Redirection utility methods (backward-compatible with old and new structure)
    // -------------------------------------------------------------------------

    /**
     * Default value returned when the redirection name cannot be determined.
     */
    public static final String DEFAULT_REDIRECTION_NAME = "Unknown";

    /**
     * Retrieves the redirection name from a JSON node, supporting both old and new data structures.
     * <p>
     * This method provides backward compatibility by checking:
     * </p>
     * <ol>
     * <li><b>New structure:</b> {@code parameters.name}</li>
     * <li><b>Old structure:</b> {@code name} (directly on the node)</li>
     * </ol>
     * <p>
     * If neither structure contains the name, returns {@value #DEFAULT_REDIRECTION_NAME}.
     * </p>
     *
     * @param redirection the JSON node containing redirection data (may be null)
     * @return the redirection name, or {@value #DEFAULT_REDIRECTION_NAME} if not found
     */
    public static String getRedirectionName(JsonNode redirection) {
        if (redirection == null) {
            return DEFAULT_REDIRECTION_NAME;
        }

        // New structure: parameters.name
        JsonNode parametersNode = redirection.get("parameters");
        if (parametersNode != null && parametersNode.has("name")) {
            return parametersNode.get("name").asText();
        }

        // Old structure: name directly
        if (redirection.has("name")) {
            return redirection.get("name").asText();
        }

        return DEFAULT_REDIRECTION_NAME;
    }

    /**
     * Retrieves the target step from a JSON node, supporting both old and new data structures.
     * <p>
     * This method provides backward compatibility by checking:
     * </p>
     * <ol>
     * <li><b>New structure:</b> {@code parameters.targetStep}</li>
     * <li><b>Old structure:</b> {@code targetStep} (directly on the node)</li>
     * </ol>
     * <p>
     * If neither structure contains the target step, returns {@code null}.
     * </p>
     *
     * @param redirection the JSON node containing redirection data (may be null)
     * @return the target step identifier, or {@code null} if not found
     */
    public static String getTargetStep(JsonNode redirection) {
        if (redirection == null) {
            return null;
        }

        // New structure: parameters.targetStep
        JsonNode parametersNode = redirection.get("parameters");
        if (parametersNode != null && parametersNode.has("targetStep")) {
            return parametersNode.get("targetStep").asText();
        }

        // Old structure: targetStep directly
        if (redirection.has("targetStep")) {
            return redirection.get("targetStep").asText();
        }

        return null;
    }
}