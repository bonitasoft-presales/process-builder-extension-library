package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link FlowActionType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("FlowActionType Property-Based Tests")
class FlowActionTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("flowActionTypes") FlowActionType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("flowActionTypes") FlowActionType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("flowActionTypes") FlowActionType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(FlowActionType.values().length);
    }

    @Property(tries = 100)
    @Label("Each constant should be retrievable by name")
    void eachConstantShouldBeRetrievableByName(@ForAll @From("flowActionTypes") FlowActionType type) {
        assertThat(FlowActionType.valueOf(type.name())).isEqualTo(type);
    }

    // =========================================================================
    // isValid() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for valid enum names (case-insensitive)")
    void isValidShouldReturnTrueForValidEnumNames(@ForAll @From("flowActionTypes") FlowActionType type) {
        assertThat(FlowActionType.isValid(type.name())).isTrue();
        assertThat(FlowActionType.isValid(type.name().toLowerCase())).isTrue();
        assertThat(FlowActionType.isValid(type.name().toUpperCase())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should handle spaces around valid names")
    void isValidShouldHandleSpacesAroundValidNames(@ForAll @From("flowActionTypes") FlowActionType type) {
        assertThat(FlowActionType.isValid("  " + type.name() + "  ")).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return false for random invalid strings")
    void isValidShouldReturnFalseForInvalidStrings(
            @ForAll @StringLength(min = 10, max = 30) @AlphaChars String input) {
        boolean isValidName = false;
        for (FlowActionType type : FlowActionType.values()) {
            if (type.name().equalsIgnoreCase(input.trim())) {
                isValidName = true;
                break;
            }
        }
        if (!isValidName) {
            assertThat(FlowActionType.isValid(input)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isValid should return false for null")
    void isValidShouldReturnFalseForNull() {
        assertThat(FlowActionType.isValid(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isValid should return false for empty string")
    void isValidShouldReturnFalseForEmpty() {
        assertThat(FlowActionType.isValid("")).isFalse();
        assertThat(FlowActionType.isValid("   ")).isFalse();
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should map keys to descriptions correctly")
    void getAllDataShouldMapKeysToDescriptions() {
        Map<String, String> data = FlowActionType.getAllData();

        for (FlowActionType type : FlowActionType.values()) {
            assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
        }
    }

    @Property(tries = 100)
    @Label("getAllKeysList should preserve enum declaration order")
    void getAllKeysListShouldPreserveOrder() {
        List<String> keys = FlowActionType.getAllKeysList();

        int index = 0;
        for (FlowActionType type : FlowActionType.values()) {
            assertThat(keys.get(index)).isEqualTo(type.getKey());
            index++;
        }
    }

    @Property(tries = 100)
    @Label("Collections should be unmodifiable")
    void collectionsShouldBeUnmodifiable() {
        Map<String, String> data = FlowActionType.getAllData();
        List<String> keys = FlowActionType.getAllKeysList();

        assertThatThrownBy(() -> data.put("new", "value"))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> keys.add("new"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<FlowActionType> flowActionTypes() {
        return Arbitraries.of(FlowActionType.values());
    }
}
