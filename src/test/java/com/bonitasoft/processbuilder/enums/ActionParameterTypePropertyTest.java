package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ActionParameterType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ActionParameterType Property-Based Tests")
class ActionParameterTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("actionParameterTypes") ActionParameterType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("actionParameterTypes") ActionParameterType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("actionParameterTypes") ActionParameterType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(ActionParameterType.values().length);
    }

    @Property(tries = 100)
    @Label("Each constant should be retrievable by name")
    void eachConstantShouldBeRetrievableByName(@ForAll @From("actionParameterTypes") ActionParameterType type) {
        assertThat(ActionParameterType.valueOf(type.name())).isEqualTo(type);
    }

    // =========================================================================
    // isValid() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for valid enum names (case-insensitive)")
    void isValidShouldReturnTrueForValidEnumNames(@ForAll @From("actionParameterTypes") ActionParameterType type) {
        assertThat(ActionParameterType.isValid(type.name())).isTrue();
        assertThat(ActionParameterType.isValid(type.name().toLowerCase())).isTrue();
        assertThat(ActionParameterType.isValid(type.name().toUpperCase())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should handle spaces around valid names")
    void isValidShouldHandleSpacesAroundValidNames(@ForAll @From("actionParameterTypes") ActionParameterType type) {
        assertThat(ActionParameterType.isValid("  " + type.name() + "  ")).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return false for random invalid strings")
    void isValidShouldReturnFalseForInvalidStrings(
            @ForAll @StringLength(min = 20, max = 35) @AlphaChars String input) {
        boolean isValidName = false;
        for (ActionParameterType type : ActionParameterType.values()) {
            if (type.name().equalsIgnoreCase(input.trim())) {
                isValidName = true;
                break;
            }
        }
        if (!isValidName) {
            assertThat(ActionParameterType.isValid(input)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isValid should return false for null")
    void isValidShouldReturnFalseForNull() {
        assertThat(ActionParameterType.isValid(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isValid should return false for empty string")
    void isValidShouldReturnFalseForEmpty() {
        assertThat(ActionParameterType.isValid("")).isFalse();
        assertThat(ActionParameterType.isValid("   ")).isFalse();
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should map keys to descriptions correctly")
    void getAllDataShouldMapKeysToDescriptions() {
        Map<String, String> data = ActionParameterType.getAllData();

        for (ActionParameterType type : ActionParameterType.values()) {
            assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
        }
    }

    @Property(tries = 100)
    @Label("getAllData size should match enum values count")
    void getAllDataSizeShouldMatchEnumCount() {
        Map<String, String> data = ActionParameterType.getAllData();

        assertThat(data).hasSize(ActionParameterType.values().length);
        assertThat(data.size()).isEqualTo(10); // Explicit count kills mutations
    }

    @Property(tries = 100)
    @Label("getAllKeysList should preserve enum declaration order")
    void getAllKeysListShouldPreserveOrder() {
        List<String> keys = ActionParameterType.getAllKeysList();

        int index = 0;
        for (ActionParameterType type : ActionParameterType.values()) {
            assertThat(keys.get(index)).isEqualTo(type.getKey());
            index++;
        }
    }

    @Property(tries = 100)
    @Label("getAllKeysList size should match enum values count")
    void getAllKeysListSizeShouldMatchEnumCount() {
        List<String> keys = ActionParameterType.getAllKeysList();

        assertThat(keys).hasSize(ActionParameterType.values().length);
        assertThat(keys.size()).isEqualTo(10); // Explicit count kills mutations
    }

    @Property(tries = 100)
    @Label("Collections should be unmodifiable")
    void collectionsShouldBeUnmodifiable() {
        Map<String, String> data = ActionParameterType.getAllData();
        List<String> keys = ActionParameterType.getAllKeysList();

        assertThatThrownBy(() -> data.put("new", "value"))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> keys.add("new"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("getAllData and getAllKeysList should have matching keys")
    void getAllDataAndKeysListShouldMatch() {
        Map<String, String> data = ActionParameterType.getAllData();
        List<String> keys = ActionParameterType.getAllKeysList();

        // Keys from map should match keys from list
        assertThat(data.keySet()).containsExactlyInAnyOrderElementsOf(keys);
    }

    @Property(tries = 100)
    @Label("Each enum constant key should be unique")
    void enumConstantKeysShouldBeUnique() {
        List<String> keys = ActionParameterType.getAllKeysList();

        // No duplicates
        assertThat(keys).doesNotHaveDuplicates();
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<ActionParameterType> actionParameterTypes() {
        return Arbitraries.of(ActionParameterType.values());
    }
}
