package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ProcessStateType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ProcessStateType Property-Based Tests")
class ProcessStateTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("processStateTypes") ProcessStateType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("processStateTypes") ProcessStateType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("processStateTypes") ProcessStateType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(ProcessStateType.values().length);
    }

    @Property(tries = 100)
    @Label("Each constant should be retrievable by name")
    void eachConstantShouldBeRetrievableByName(@ForAll @From("processStateTypes") ProcessStateType type) {
        assertThat(ProcessStateType.valueOf(type.name())).isEqualTo(type);
    }

    // =========================================================================
    // isValid() PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("isValid should return true for valid key (case-insensitive)")
    void isValidShouldReturnTrueForValidKey(@ForAll @From("processStateTypes") ProcessStateType type) {
        // isValid uses key, not name
        assertThat(ProcessStateType.isValid(type.getKey())).isTrue();
        assertThat(ProcessStateType.isValid(type.getKey().toLowerCase())).isTrue();
        assertThat(ProcessStateType.isValid(type.getKey().toUpperCase())).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should handle spaces around valid keys")
    void isValidShouldHandleSpacesAroundValidKeys(@ForAll @From("processStateTypes") ProcessStateType type) {
        assertThat(ProcessStateType.isValid("  " + type.getKey() + "  ")).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return false for random invalid strings")
    void isValidShouldReturnFalseForInvalidStrings(
            @ForAll @StringLength(min = 10, max = 30) @AlphaChars String input) {
        // With longer strings, less likely to match a valid key
        boolean isValidKey = false;
        for (ProcessStateType type : ProcessStateType.values()) {
            String normalized = input.trim().replace(" ", "_").toUpperCase();
            if (type.name().equals(normalized) || type.getKey().equalsIgnoreCase(input.trim())) {
                isValidKey = true;
                break;
            }
        }
        if (!isValidKey) {
            assertThat(ProcessStateType.isValid(input)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isValid should return false for null")
    void isValidShouldReturnFalseForNull() {
        assertThat(ProcessStateType.isValid(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isValid should return false for empty string")
    void isValidShouldReturnFalseForEmpty() {
        assertThat(ProcessStateType.isValid("")).isFalse();
        assertThat(ProcessStateType.isValid("   ")).isFalse();
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should map keys to descriptions correctly")
    void getAllDataShouldMapKeysToDescriptions() {
        Map<String, String> data = ProcessStateType.getAllData();

        for (ProcessStateType type : ProcessStateType.values()) {
            assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
        }
    }

    @Property(tries = 100)
    @Label("getAllKeysList should preserve enum declaration order")
    void getAllKeysListShouldPreserveOrder() {
        List<String> keys = ProcessStateType.getAllKeysList();

        int index = 0;
        for (ProcessStateType type : ProcessStateType.values()) {
            assertThat(keys.get(index)).isEqualTo(type.getKey());
            index++;
        }
    }

    @Property(tries = 100)
    @Label("Collections should be unmodifiable")
    void collectionsShouldBeUnmodifiable() {
        Map<String, String> data = ProcessStateType.getAllData();
        List<String> keys = ProcessStateType.getAllKeysList();

        assertThatThrownBy(() -> data.put("new", "value"))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> keys.add("new"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<ProcessStateType> processStateTypes() {
        return Arbitraries.of(ProcessStateType.values());
    }
}
