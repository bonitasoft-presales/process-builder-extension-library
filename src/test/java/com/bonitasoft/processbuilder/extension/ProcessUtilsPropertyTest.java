package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ProcessUtils} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ProcessUtils Property-Based Tests")
class ProcessUtilsPropertyTest {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    private static final String OBJECT_TYPE = "TestObject";

    // =========================================================================
    // Mock class for testing
    // =========================================================================

    private static class MockBDM {
        private final Long id;
        private final String key;

        public MockBDM(Long id) {
            this.id = id;
            this.key = null;
        }

        public MockBDM(Long id, String key) {
            this.id = id;
            this.key = key;
        }

        public Long getId() {
            return id;
        }

        public String getKey() {
            return key;
        }
    }

    // =========================================================================
    // searchById PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("searchById should return null for null persistenceId")
    void searchById_shouldReturnNullForNullPersistenceId() {
        Function<Long, MockBDM> searchFunction = id -> new MockBDM(id);

        MockBDM result = ProcessUtils.searchById(null, searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("searchById should return null for zero persistenceId")
    void searchById_shouldReturnNullForZeroPersistenceId() {
        Function<Long, MockBDM> searchFunction = id -> new MockBDM(id);

        MockBDM result = ProcessUtils.searchById(0L, searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("searchById should return null for negative persistenceId")
    void searchById_shouldReturnNullForNegativePersistenceId(
            @ForAll @LongRange(min = Long.MIN_VALUE, max = -1) long negativePersistenceId) {

        Function<Long, MockBDM> searchFunction = id -> new MockBDM(id);

        MockBDM result = ProcessUtils.searchById(negativePersistenceId, searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("searchById should return result for positive persistenceId")
    void searchById_shouldReturnResultForPositivePersistenceId(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long positivePersistenceId) {

        MockBDM expectedObject = new MockBDM(positivePersistenceId);
        Function<Long, MockBDM> searchFunction = id -> expectedObject;

        MockBDM result = ProcessUtils.searchById(positivePersistenceId, searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull();
        assertThat(result).isSameAs(expectedObject);
    }

    @Property(tries = 200)
    @Label("searchById should pass correct persistenceId to search function")
    void searchById_shouldPassCorrectPersistenceIdToSearchFunction(
            @ForAll @LongRange(min = 1, max = 100000) long persistenceId) {

        final long[] capturedId = new long[1];
        Function<Long, MockBDM> searchFunction = id -> {
            capturedId[0] = id;
            return new MockBDM(id);
        };

        ProcessUtils.searchById(persistenceId, searchFunction, OBJECT_TYPE);

        assertThat(capturedId[0]).isEqualTo(persistenceId);
    }

    @Property(tries = 100)
    @Label("searchById should return null when search function throws exception")
    void searchById_shouldReturnNullWhenSearchFunctionThrowsException(
            @ForAll @LongRange(min = 1, max = 100000) long persistenceId) {

        Function<Long, MockBDM> searchFunction = id -> {
            throw new RuntimeException("Search error");
        };

        MockBDM result = ProcessUtils.searchById(persistenceId, searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    // =========================================================================
    // searchBDM PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("searchBDM should return null for null persistenceIdInput")
    void searchBDM_shouldReturnNullForNullPersistenceIdInput() {
        Function<Long, MockBDM> searchFunction = id -> new MockBDM(id);

        MockBDM result = ProcessUtils.searchBDM(null, searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("searchBDM should return null for empty persistenceIdInput")
    void searchBDM_shouldReturnNullForEmptyPersistenceIdInput() {
        Function<Long, MockBDM> searchFunction = id -> new MockBDM(id);

        MockBDM result = ProcessUtils.searchBDM("", searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("searchBDM should return null for blank persistenceIdInput")
    void searchBDM_shouldReturnNullForBlankPersistenceIdInput(
            @ForAll @IntRange(min = 1, max = 10) int spaceCount) {

        String blankInput = " ".repeat(spaceCount);
        Function<Long, MockBDM> searchFunction = id -> new MockBDM(id);

        MockBDM result = ProcessUtils.searchBDM(blankInput, searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("searchBDM should return result for valid numeric persistenceIdInput")
    void searchBDM_shouldReturnResultForValidNumericPersistenceIdInput(
            @ForAll @LongRange(min = 1, max = 100000) long persistenceId) {

        MockBDM expectedObject = new MockBDM(persistenceId);
        Function<Long, MockBDM> searchFunction = id -> expectedObject;

        MockBDM result = ProcessUtils.searchBDM(String.valueOf(persistenceId), searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull();
        assertThat(result).isSameAs(expectedObject);
    }

    @Property(tries = 200)
    @Label("searchBDM should trim whitespace from persistenceIdInput")
    void searchBDM_shouldTrimWhitespaceFromPersistenceIdInput(
            @ForAll @LongRange(min = 1, max = 100000) long persistenceId,
            @ForAll @IntRange(min = 1, max = 5) int leadingSpaces,
            @ForAll @IntRange(min = 1, max = 5) int trailingSpaces) {

        String paddedInput = " ".repeat(leadingSpaces) + persistenceId + " ".repeat(trailingSpaces);
        MockBDM expectedObject = new MockBDM(persistenceId);
        Function<Long, MockBDM> searchFunction = id -> expectedObject;

        MockBDM result = ProcessUtils.searchBDM(paddedInput, searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull();
        assertThat(result).isSameAs(expectedObject);
    }

    @Property(tries = 500)
    @Label("searchBDM should return null for non-numeric persistenceIdInput")
    void searchBDM_shouldReturnNullForNonNumericPersistenceIdInput(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String nonNumericInput) {

        Function<Long, MockBDM> searchFunction = id -> new MockBDM(id);

        MockBDM result = ProcessUtils.searchBDM(nonNumericInput, searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    // =========================================================================
    // searchByStringKey PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("searchByStringKey should return null for null searchKeyInput")
    void searchByStringKey_shouldReturnNullForNullSearchKeyInput() {
        Function<String, MockBDM> searchFunction = key -> new MockBDM(1L, key);

        MockBDM result = ProcessUtils.searchByStringKey(null, searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("searchByStringKey should return null for empty searchKeyInput")
    void searchByStringKey_shouldReturnNullForEmptySearchKeyInput() {
        Function<String, MockBDM> searchFunction = key -> new MockBDM(1L, key);

        MockBDM result = ProcessUtils.searchByStringKey("", searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("searchByStringKey should return null for blank searchKeyInput")
    void searchByStringKey_shouldReturnNullForBlankSearchKeyInput(
            @ForAll @IntRange(min = 1, max = 10) int spaceCount) {

        String blankInput = " ".repeat(spaceCount);
        Function<String, MockBDM> searchFunction = key -> new MockBDM(1L, key);

        MockBDM result = ProcessUtils.searchByStringKey(blankInput, searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("searchByStringKey should return result for valid non-blank searchKeyInput")
    void searchByStringKey_shouldReturnResultForValidNonBlankSearchKeyInput(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String searchKey) {

        MockBDM expectedObject = new MockBDM(1L, searchKey);
        Function<String, MockBDM> searchFunction = key -> expectedObject;

        MockBDM result = ProcessUtils.searchByStringKey(searchKey, searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull();
        assertThat(result).isSameAs(expectedObject);
    }

    @Property(tries = 200)
    @Label("searchByStringKey should trim whitespace from searchKeyInput")
    void searchByStringKey_shouldTrimWhitespaceFromSearchKeyInput(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String searchKey,
            @ForAll @IntRange(min = 1, max = 5) int leadingSpaces,
            @ForAll @IntRange(min = 1, max = 5) int trailingSpaces) {

        String paddedInput = " ".repeat(leadingSpaces) + searchKey + " ".repeat(trailingSpaces);
        final String[] capturedKey = new String[1];
        Function<String, MockBDM> searchFunction = key -> {
            capturedKey[0] = key;
            return new MockBDM(1L, key);
        };

        ProcessUtils.searchByStringKey(paddedInput, searchFunction, OBJECT_TYPE);

        assertThat(capturedKey[0]).isEqualTo(searchKey);
    }

    @Property(tries = 200)
    @Label("searchByStringKey should pass correct key to search function")
    void searchByStringKey_shouldPassCorrectKeyToSearchFunction(
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String searchKey) {

        final String[] capturedKey = new String[1];
        Function<String, MockBDM> searchFunction = key -> {
            capturedKey[0] = key;
            return new MockBDM(1L, key);
        };

        ProcessUtils.searchByStringKey(searchKey, searchFunction, OBJECT_TYPE);

        assertThat(capturedKey[0]).isEqualTo(searchKey);
    }

    @Property(tries = 100)
    @Label("searchByStringKey should return null when search function throws exception")
    void searchByStringKey_shouldReturnNullWhenSearchFunctionThrowsException(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String searchKey) {

        Function<String, MockBDM> searchFunction = key -> {
            throw new RuntimeException("Search error");
        };

        MockBDM result = ProcessUtils.searchByStringKey(searchKey, searchFunction, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 200)
    @Label("searchByStringKey should handle keys with special characters")
    void searchByStringKey_shouldHandleKeysWithSpecialCharacters(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String prefix,
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String suffix) {

        String keyWithSpecialChars = prefix + "-_./:" + suffix;
        MockBDM expectedObject = new MockBDM(1L, keyWithSpecialChars);
        Function<String, MockBDM> searchFunction = key -> expectedObject;

        MockBDM result = ProcessUtils.searchByStringKey(keyWithSpecialChars, searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull();
        assertThat(result).isSameAs(expectedObject);
    }

    // =========================================================================
    // searchBDMList PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("searchBDMList should return empty list for null persistenceIdInput")
    void searchBDMList_shouldReturnEmptyListForNullPersistenceIdInput() {
        Function<Long, List<MockBDM>> searchFunction = id -> List.of(new MockBDM(id));

        List<MockBDM> result = ProcessUtils.searchBDMList(null, searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull().isEmpty();
    }

    @Property(tries = 500)
    @Label("searchBDMList should return empty list for zero persistenceIdInput")
    void searchBDMList_shouldReturnEmptyListForZeroPersistenceIdInput() {
        Function<Long, List<MockBDM>> searchFunction = id -> List.of(new MockBDM(id));

        List<MockBDM> result = ProcessUtils.searchBDMList(0L, searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull().isEmpty();
    }

    @Property(tries = 500)
    @Label("searchBDMList should return empty list for negative persistenceIdInput")
    void searchBDMList_shouldReturnEmptyListForNegativePersistenceIdInput(
            @ForAll @LongRange(min = Long.MIN_VALUE, max = -1) long negativePersistenceId) {

        Function<Long, List<MockBDM>> searchFunction = id -> List.of(new MockBDM(id));

        List<MockBDM> result = ProcessUtils.searchBDMList(negativePersistenceId, searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull().isEmpty();
    }

    @Property(tries = 500)
    @Label("searchBDMList should return result for positive persistenceIdInput")
    void searchBDMList_shouldReturnResultForPositivePersistenceIdInput(
            @ForAll @LongRange(min = 1, max = 100000) long persistenceId,
            @ForAll @IntRange(min = 1, max = 10) int listSize) {

        List<MockBDM> expectedList = new java.util.ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            expectedList.add(new MockBDM(persistenceId + i));
        }
        Function<Long, List<MockBDM>> searchFunction = id -> expectedList;

        List<MockBDM> result = ProcessUtils.searchBDMList(persistenceId, searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull().hasSize(listSize);
    }

    @Property(tries = 100)
    @Label("searchBDMList should return empty list when search function returns null")
    void searchBDMList_shouldReturnEmptyListWhenSearchFunctionReturnsNull(
            @ForAll @LongRange(min = 1, max = 100000) long persistenceId) {

        Function<Long, List<MockBDM>> searchFunction = id -> null;

        List<MockBDM> result = ProcessUtils.searchBDMList(persistenceId, searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull().isEmpty();
    }

    @Property(tries = 100)
    @Label("searchBDMList should return empty list when search function throws exception")
    void searchBDMList_shouldReturnEmptyListWhenSearchFunctionThrowsException(
            @ForAll @LongRange(min = 1, max = 100000) long persistenceId) {

        Function<Long, List<MockBDM>> searchFunction = id -> {
            throw new RuntimeException("Database error");
        };

        List<MockBDM> result = ProcessUtils.searchBDMList(persistenceId, searchFunction, OBJECT_TYPE);

        assertThat(result).isNotNull().isEmpty();
    }

    // =========================================================================
    // findMostRecentStepInstance PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("findMostRecentStepInstance should return null when supplier returns null")
    void findMostRecentStepInstance_shouldReturnNullWhenSupplierReturnsNull() {
        Supplier<List<MockBDM>> searchSupplier = () -> null;

        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("findMostRecentStepInstance should return null when supplier returns empty list")
    void findMostRecentStepInstance_shouldReturnNullWhenSupplierReturnsEmptyList() {
        Supplier<List<MockBDM>> searchSupplier = Collections::emptyList;

        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 500)
    @Label("findMostRecentStepInstance should return first element from non-empty list")
    void findMostRecentStepInstance_shouldReturnFirstElementFromNonEmptyList(
            @ForAll @LongRange(min = 1, max = 100000) long firstId,
            @ForAll @IntRange(min = 1, max = 10) int additionalElements) {

        List<MockBDM> list = new java.util.ArrayList<>();
        MockBDM firstElement = new MockBDM(firstId);
        list.add(firstElement);
        for (int i = 0; i < additionalElements; i++) {
            list.add(new MockBDM(firstId - i - 1));
        }
        Supplier<List<MockBDM>> searchSupplier = () -> list;

        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        assertThat(result).isNotNull();
        assertThat(result).isSameAs(firstElement);
    }

    @Property(tries = 100)
    @Label("findMostRecentStepInstance should return null when supplier throws exception")
    void findMostRecentStepInstance_shouldReturnNullWhenSupplierThrowsException() {
        Supplier<List<MockBDM>> searchSupplier = () -> {
            throw new RuntimeException("Database error");
        };

        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        assertThat(result).isNull();
    }

    @Property(tries = 200)
    @Label("findMostRecentStepInstance should work with single element list")
    void findMostRecentStepInstance_shouldWorkWithSingleElementList(
            @ForAll @LongRange(min = 1, max = 100000) long id) {

        MockBDM singleElement = new MockBDM(id);
        Supplier<List<MockBDM>> searchSupplier = () -> Collections.singletonList(singleElement);

        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        assertThat(result).isNotNull();
        assertThat(result).isSameAs(singleElement);
    }
}
