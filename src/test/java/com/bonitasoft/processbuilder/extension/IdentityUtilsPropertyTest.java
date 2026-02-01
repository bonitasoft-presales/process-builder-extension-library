package com.bonitasoft.processbuilder.extension;

import com.fasterxml.jackson.databind.JsonNode;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link IdentityUtils} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("IdentityUtils Property-Based Tests")
class IdentityUtilsPropertyTest {

    // =========================================================================
    // JSON Parsing Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("parseJson should return empty for null or blank input")
    void parseJson_shouldReturnEmptyForNullOrBlank(
            @ForAll("nullOrBlankStrings") String input) {

        Optional<JsonNode> result = IdentityUtils.parseJson(input, null);

        assertThat(result).isEmpty();
    }

    @Provide
    Arbitrary<String> nullOrBlankStrings() {
        return Arbitraries.of(null, "", "   ", "\t", "\n", "  \t\n  ");
    }

    @Property(tries = 100)
    @Label("parseJson should return present for valid JSON objects")
    void parseJson_shouldReturnPresentForValidJson(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String key,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String value) {

        String json = "{\"" + key + "\":\"" + value + "\"}";

        Optional<JsonNode> result = IdentityUtils.parseJson(json, null);

        assertThat(result).isPresent();
        assertThat(result.get().has(key)).isTrue();
    }

    @Property(tries = 100)
    @Label("parseJson should return empty for invalid JSON")
    void parseJson_shouldReturnEmptyForInvalidJson(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String invalidJson) {

        // Make sure it's not accidentally valid JSON
        String definitelyInvalid = "{" + invalidJson + " invalid";

        Optional<JsonNode> result = IdentityUtils.parseJson(definitelyInvalid, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("parseJson should parse JSON with numeric values")
    void parseJson_shouldParseJsonWithNumbers(
            @ForAll @IntRange(min = -1000000, max = 1000000) int number) {

        String json = "{\"value\":" + number + "}";

        Optional<JsonNode> result = IdentityUtils.parseJson(json, null);

        assertThat(result).isPresent();
        assertThat(result.get().get("value").asInt()).isEqualTo(number);
    }

    @Property(tries = 2)
    @Label("parseJson should parse JSON with boolean values")
    void parseJson_shouldParseJsonWithBooleans(@ForAll boolean value) {

        String json = "{\"flag\":" + value + "}";

        Optional<JsonNode> result = IdentityUtils.parseJson(json, null);

        assertThat(result).isPresent();
        assertThat(result.get().get("flag").asBoolean()).isEqualTo(value);
    }

    // =========================================================================
    // getNodeText Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("getNodeText should return empty for null node")
    void getNodeText_shouldReturnEmptyForNull() {
        Optional<String> result = IdentityUtils.getNodeText(null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("getNodeText should trim whitespace from values")
    void getNodeText_shouldTrimWhitespace(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String value) {

        String json = "{\"text\":\"  " + value + "  \"}";
        Optional<JsonNode> root = IdentityUtils.parseJson(json, null);

        assertThat(root).isPresent();

        Optional<String> result = IdentityUtils.getNodeText(root.get().get("text"), null);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(value);
    }

    // =========================================================================
    // getNodeArrayAsStringList Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("getNodeArrayAsStringList should return empty for null")
    void getNodeArrayAsStringList_shouldReturnEmptyForNull() {
        List<String> result = IdentityUtils.getNodeArrayAsStringList(null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 50)
    @Label("getNodeArrayAsStringList should extract all non-blank strings")
    void getNodeArrayAsStringList_shouldExtractNonBlankStrings(
            @ForAll @Size(min = 1, max = 5) List<@AlphaChars @StringLength(min = 1, max = 10) String> values) {

        StringBuilder jsonBuilder = new StringBuilder("{\"items\":[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) jsonBuilder.append(",");
            jsonBuilder.append("\"").append(values.get(i)).append("\"");
        }
        jsonBuilder.append("]}");

        Optional<JsonNode> root = IdentityUtils.parseJson(jsonBuilder.toString(), null);
        assertThat(root).isPresent();

        List<String> result = IdentityUtils.getNodeArrayAsStringList(root.get().get("items"), null);

        assertThat(result).hasSize(values.size());
        assertThat(result).containsExactlyElementsOf(values);
    }

    // =========================================================================
    // extractFieldFromJson Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("extractFieldFromJson should extract existing field")
    void extractFieldFromJson_shouldExtractExistingField(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String fieldName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String fieldValue) {

        String json = "{\"" + fieldName + "\":\"" + fieldValue + "\"}";

        Optional<String> result = IdentityUtils.extractFieldFromJson(json, fieldName, null);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(fieldValue);
    }

    @Property(tries = 100)
    @Label("extractFieldFromJson should return empty for missing field")
    void extractFieldFromJson_shouldReturnEmptyForMissingField(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String existingField,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String missingField) {

        Assume.that(!existingField.equals(missingField));

        String json = "{\"" + existingField + "\":\"value\"}";

        Optional<String> result = IdentityUtils.extractFieldFromJson(json, missingField, null);

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // findMostRecentInstance Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("findMostRecentInstance should return null for null supplier")
    void findMostRecentInstance_shouldReturnNullForNullSupplier() {
        Object result = IdentityUtils.findMostRecentInstance(null, "Test", null);

        assertThat(result).isNull();
    }

    @Property(tries = 100)
    @Label("findMostRecentInstance should return null for empty list")
    void findMostRecentInstance_shouldReturnNullForEmptyList() {
        String result = IdentityUtils.findMostRecentInstance(
                Collections::emptyList, "Test", null);

        assertThat(result).isNull();
    }

    @Property(tries = 100)
    @Label("findMostRecentInstance should return first element")
    void findMostRecentInstance_shouldReturnFirstElement(
            @ForAll @Size(min = 1, max = 10) List<@IntRange(min = 1, max = 1000) Integer> values) {

        Integer expected = values.get(0);

        Integer result = IdentityUtils.findMostRecentInstance(
                () -> values, "Integer", null);

        assertThat(result).isEqualTo(expected);
    }

    @Property(tries = 100)
    @Label("findMostRecentInstance should return null on exception")
    void findMostRecentInstance_shouldReturnNullOnException() {
        String result = IdentityUtils.findMostRecentInstance(
                () -> { throw new RuntimeException("Test"); },
                "Test",
                null);

        assertThat(result).isNull();
    }

    // =========================================================================
    // filterAssignableUsers Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("filterAssignableUsers should return empty for null candidates")
    void filterAssignableUsers_shouldReturnEmptyForNullCandidates() {
        Set<Long> result = IdentityUtils.filterAssignableUsers(null, Arrays.asList(1L, 2L));

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("filterAssignableUsers should return empty for null assignable")
    void filterAssignableUsers_shouldReturnEmptyForNullAssignable() {
        Set<Long> candidates = new HashSet<>(Arrays.asList(1L, 2L));

        Set<Long> result = IdentityUtils.filterAssignableUsers(candidates, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("filterAssignableUsers should return intersection")
    void filterAssignableUsers_shouldReturnIntersection(
            @ForAll @Size(min = 1, max = 10) Set<@LongRange(min = 1, max = 100) Long> candidates,
            @ForAll @Size(min = 1, max = 10) Set<@LongRange(min = 1, max = 100) Long> assignable) {

        Set<Long> result = IdentityUtils.filterAssignableUsers(candidates, assignable);

        // Result should only contain IDs that are in BOTH sets
        for (Long id : result) {
            assertThat(candidates).contains(id);
            assertThat(assignable).contains(id);
        }

        // Result size should be at most the size of the smaller set
        assertThat(result.size()).isLessThanOrEqualTo(Math.min(candidates.size(), assignable.size()));
    }

    @Property(tries = 100)
    @Label("filterAssignableUsers should be commutative in a way")
    void filterAssignableUsers_sizeDoesNotDependOnOrder(
            @ForAll @Size(min = 1, max = 10) Set<@LongRange(min = 1, max = 50) Long> setA,
            @ForAll @Size(min = 1, max = 10) Set<@LongRange(min = 1, max = 50) Long> setB) {

        Set<Long> resultAB = IdentityUtils.filterAssignableUsers(setA, setB);
        Set<Long> resultBA = IdentityUtils.filterAssignableUsers(setB, setA);

        // Both should have the same elements (intersection is commutative)
        assertThat(resultAB).containsExactlyInAnyOrderElementsOf(resultBA);
    }

    // =========================================================================
    // extractUserIdsFromObjects Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("extractUserIdsFromObjects should return empty for null list")
    void extractUserIdsFromObjects_shouldReturnEmptyForNullList() {
        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(null, "getUserId");

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("extractUserIdsFromObjects should return empty for empty list")
    void extractUserIdsFromObjects_shouldReturnEmptyForEmptyList() {
        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(Collections.emptyList(), "getUserId");

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("extractUserIdsFromObjects should return empty for null method name")
    void extractUserIdsFromObjects_shouldReturnEmptyForNullMethod() {
        List<MockUserObject> objects = Collections.singletonList(new MockUserObject(1L));

        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(objects, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("extractUserIdsFromObjects should extract valid IDs and filter invalid ones")
    void extractUserIdsFromObjects_shouldExtractValidIds(
            @ForAll @Size(min = 1, max = 10) List<@LongRange(min = -10, max = 100) Long> ids) {

        List<MockUserObject> objects = new ArrayList<>();
        Set<Long> expectedValidIds = new HashSet<>();

        for (Long id : ids) {
            objects.add(new MockUserObject(id));
            if (id != null && id > 0) {
                expectedValidIds.add(id);
            }
        }

        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(objects, "getUserId");

        assertThat(result).containsExactlyInAnyOrderElementsOf(expectedValidIds);
    }

    // =========================================================================
    // extractLongValue Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("extractLongValue should return null for null object")
    void extractLongValue_shouldReturnNullForNullObject() {
        Long result = IdentityUtils.extractLongValue(null, "getValue");

        assertThat(result).isNull();
    }

    @Property(tries = 100)
    @Label("extractLongValue should return null for invalid IDs (0 or negative)")
    void extractLongValue_shouldReturnNullForInvalidIds(
            @ForAll @LongRange(min = Long.MIN_VALUE, max = 0) long invalidId) {

        MockUserObject obj = new MockUserObject(invalidId);

        Long result = IdentityUtils.extractLongValue(obj, "getUserId");

        assertThat(result).isNull();
    }

    @Property(tries = 100)
    @Label("extractLongValue should return value for valid IDs (positive)")
    void extractLongValue_shouldReturnValueForValidIds(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long validId) {

        MockUserObject obj = new MockUserObject(validId);

        Long result = IdentityUtils.extractLongValue(obj, "getUserId");

        assertThat(result).isEqualTo(validId);
    }

    @Property(tries = 100)
    @Label("extractLongValue should return null for non-existent method")
    void extractLongValue_shouldReturnNullForNonExistentMethod(
            @ForAll @AlphaChars @StringLength(min = 5, max = 20) String methodName) {

        MockUserObject obj = new MockUserObject(100L);

        Long result = IdentityUtils.extractLongValue(obj, methodName);

        assertThat(result).isNull();
    }

    // =========================================================================
    // Mock Helper Classes
    // =========================================================================

    private static class MockUserObject {
        private final Long userId;

        public MockUserObject(Long userId) {
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }
    }
}
