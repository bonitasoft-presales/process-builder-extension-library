package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ObjectsManagementOptionType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ObjectsManagementOptionType Property-Based Tests")
class ObjectsManagementOptionTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("objectsManagementTypes") ObjectsManagementOptionType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("objectsManagementTypes") ObjectsManagementOptionType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("objectsManagementTypes") ObjectsManagementOptionType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(ObjectsManagementOptionType.values().length);
    }

    @Property(tries = 100)
    @Label("Each constant should be retrievable by name")
    void eachConstantShouldBeRetrievableByName(@ForAll @From("objectsManagementTypes") ObjectsManagementOptionType type) {
        assertThat(ObjectsManagementOptionType.valueOf(type.name())).isEqualTo(type);
    }

    // =========================================================================
    // isValid() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for valid enum names (case-insensitive)")
    void isValidShouldReturnTrueForValidEnumNames(@ForAll @From("objectsManagementTypes") ObjectsManagementOptionType type) {
        assertThat(ObjectsManagementOptionType.isValid(type.name())).isTrue();
        assertThat(ObjectsManagementOptionType.isValid(type.name().toLowerCase())).isTrue();
        assertThat(ObjectsManagementOptionType.isValid(type.name().toUpperCase())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should handle spaces around valid names")
    void isValidShouldHandleSpacesAroundValidNames(@ForAll @From("objectsManagementTypes") ObjectsManagementOptionType type) {
        assertThat(ObjectsManagementOptionType.isValid("  " + type.name() + "  ")).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return false for random invalid strings")
    void isValidShouldReturnFalseForInvalidStrings(
            @ForAll @StringLength(min = 15, max = 30) @AlphaChars String input) {
        boolean isValidName = false;
        for (ObjectsManagementOptionType type : ObjectsManagementOptionType.values()) {
            if (type.name().equalsIgnoreCase(input.trim())) {
                isValidName = true;
                break;
            }
        }
        if (!isValidName) {
            assertThat(ObjectsManagementOptionType.isValid(input)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isValid should return false for null")
    void isValidShouldReturnFalseForNull() {
        assertThat(ObjectsManagementOptionType.isValid(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isValid should return false for empty string")
    void isValidShouldReturnFalseForEmpty() {
        assertThat(ObjectsManagementOptionType.isValid("")).isFalse();
        assertThat(ObjectsManagementOptionType.isValid("   ")).isFalse();
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should map keys to descriptions correctly")
    void getAllDataShouldMapKeysToDescriptions() {
        Map<String, String> data = ObjectsManagementOptionType.getAllData();

        for (ObjectsManagementOptionType type : ObjectsManagementOptionType.values()) {
            assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
        }
    }

    @Property(tries = 100)
    @Label("getAllKeysList should preserve enum declaration order")
    void getAllKeysListShouldPreserveOrder() {
        List<String> keys = ObjectsManagementOptionType.getAllKeysList();

        int index = 0;
        for (ObjectsManagementOptionType type : ObjectsManagementOptionType.values()) {
            assertThat(keys.get(index)).isEqualTo(type.getKey());
            index++;
        }
    }

    @Property(tries = 100)
    @Label("Collections should be unmodifiable")
    void collectionsShouldBeUnmodifiable() {
        Map<String, String> data = ObjectsManagementOptionType.getAllData();
        List<String> keys = ObjectsManagementOptionType.getAllKeysList();

        assertThatThrownBy(() -> data.put("new", "value"))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> keys.add("new"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<ObjectsManagementOptionType> objectsManagementTypes() {
        return Arbitraries.of(ObjectsManagementOptionType.values());
    }
}
