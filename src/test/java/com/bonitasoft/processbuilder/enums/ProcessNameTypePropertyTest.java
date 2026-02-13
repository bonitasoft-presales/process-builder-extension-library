package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ProcessNameType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ProcessNameType Property-Based Tests")
class ProcessNameTypePropertyTest {

    private static final int EXPECTED_ENUM_COUNT = 18;

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("processNameTypes") ProcessNameType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("processNameTypes") ProcessNameType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("processNameTypes") ProcessNameType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(ProcessNameType.values().length);
    }

    @Property(tries = 100)
    @Label("Each constant should be retrievable by name")
    void eachConstantShouldBeRetrievableByName(@ForAll @From("processNameTypes") ProcessNameType type) {
        assertThat(ProcessNameType.valueOf(type.name())).isEqualTo(type);
    }

    @Property(tries = 100)
    @Label("Key should start with uppercase letter")
    void keyShouldStartWithUppercaseLetter(@ForAll @From("processNameTypes") ProcessNameType type) {
        assertThat(type.getKey()).matches("^[A-Z].*");
    }

    @Property(tries = 100)
    @Label("Description should start with 'Process for'")
    void descriptionShouldStartWithProcessFor(@ForAll @From("processNameTypes") ProcessNameType type) {
        assertThat(type.getDescription()).startsWith("Process for");
    }

    @Property(tries = 100)
    @Label("Description should end with period")
    void descriptionShouldEndWithPeriod(@ForAll @From("processNameTypes") ProcessNameType type) {
        assertThat(type.getDescription()).endsWith(".");
    }

    // =========================================================================
    // isValid() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for valid enum names (case-insensitive)")
    void isValidShouldReturnTrueForValidEnumNames(@ForAll @From("processNameTypes") ProcessNameType type) {
        assertThat(ProcessNameType.isValid(type.name())).isTrue();
        assertThat(ProcessNameType.isValid(type.name().toLowerCase())).isTrue();
        assertThat(ProcessNameType.isValid(type.name().toUpperCase())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should handle spaces around valid names")
    void isValidShouldHandleSpacesAroundValidNames(@ForAll @From("processNameTypes") ProcessNameType type) {
        assertThat(ProcessNameType.isValid("  " + type.name() + "  ")).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return false for random invalid strings")
    void isValidShouldReturnFalseForInvalidStrings(
            @ForAll @StringLength(min = 10, max = 30) @AlphaChars String input) {
        boolean isValidName = false;
        for (ProcessNameType type : ProcessNameType.values()) {
            if (type.name().equalsIgnoreCase(input.trim())) {
                isValidName = true;
                break;
            }
        }
        if (!isValidName) {
            assertThat(ProcessNameType.isValid(input)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isValid should return false for null")
    void isValidShouldReturnFalseForNull() {
        assertThat(ProcessNameType.isValid(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isValid should return false for empty string")
    void isValidShouldReturnFalseForEmpty() {
        assertThat(ProcessNameType.isValid("")).isFalse();
        assertThat(ProcessNameType.isValid("   ")).isFalse();
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should map keys to descriptions correctly")
    void getAllDataShouldMapKeysToDescriptions() {
        Map<String, String> data = ProcessNameType.getAllData();

        for (ProcessNameType type : ProcessNameType.values()) {
            assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
        }
    }

    @Property(tries = 100)
    @Label("getAllData should contain all constants")
    void getAllDataShouldContainAllConstants() {
        Map<String, String> data = ProcessNameType.getAllData();
        assertThat(data).hasSize(EXPECTED_ENUM_COUNT);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should preserve enum declaration order")
    void getAllKeysListShouldPreserveOrder() {
        List<String> keys = ProcessNameType.getAllKeysList();

        int index = 0;
        for (ProcessNameType type : ProcessNameType.values()) {
            assertThat(keys.get(index)).isEqualTo(type.getKey());
            index++;
        }
    }

    @Property(tries = 100)
    @Label("getAllKeysList should contain all keys in order")
    void getAllKeysListShouldContainAllKeysInOrder() {
        List<String> keys = ProcessNameType.getAllKeysList();
        assertThat(keys).hasSize(EXPECTED_ENUM_COUNT);
    }

    @Property(tries = 100)
    @Label("Collections should be unmodifiable")
    void collectionsShouldBeUnmodifiable() {
        Map<String, String> data = ProcessNameType.getAllData();
        List<String> keys = ProcessNameType.getAllKeysList();

        assertThatThrownBy(() -> data.put("new", "value"))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> keys.add("new"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("getAllData should not allow removal")
    void getAllDataShouldNotAllowRemoval() {
        Map<String, String> data = ProcessNameType.getAllData();
        assertThatThrownBy(() -> data.remove("Form"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should not allow removal")
    void getAllKeysListShouldNotAllowRemoval() {
        List<String> keys = ProcessNameType.getAllKeysList();
        assertThatThrownBy(() -> keys.remove(0))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // SPECIFIC VALUE VERIFICATION PROPERTIES
    // =========================================================================

    @Property(tries = 50)
    @Label("Enum values count should be exactly 18")
    void enumValuesCountShouldBeExactly18() {
        assertThat(ProcessNameType.values()).hasSize(EXPECTED_ENUM_COUNT);
    }

    @Property(tries = 100)
    @Label("Each enum constant should have unique key")
    void eachConstantShouldHaveUniqueKey() {
        Map<String, String> data = ProcessNameType.getAllData();
        List<String> keys = ProcessNameType.getAllKeysList();

        // If all keys are unique, the map and list should have the same size
        assertThat(data.keySet()).hasSize(keys.size());
    }

    @Property(tries = 100)
    @Label("getKey and getDescription should return consistent values")
    void getKeyAndDescriptionShouldReturnConsistentValues(@ForAll @From("processNameTypes") ProcessNameType type) {
        // Call multiple times to verify consistency
        String key1 = type.getKey();
        String key2 = type.getKey();
        String desc1 = type.getDescription();
        String desc2 = type.getDescription();

        assertThat(key1).isEqualTo(key2);
        assertThat(desc1).isEqualTo(desc2);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<ProcessNameType> processNameTypes() {
        return Arbitraries.of(ProcessNameType.values());
    }
}
