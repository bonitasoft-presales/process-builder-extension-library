package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link GenericType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("GenericType Property-Based Tests")
class GenericTypePropertyTest {

    private static final int EXPECTED_ENUM_COUNT = 2;

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("genericTypes") GenericType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("genericTypes") GenericType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("genericTypes") GenericType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(GenericType.values().length);
    }

    @Property(tries = 100)
    @Label("Each constant should be retrievable by name")
    void eachConstantShouldBeRetrievableByName(@ForAll @From("genericTypes") GenericType type) {
        assertThat(GenericType.valueOf(type.name())).isEqualTo(type);
    }

    // =========================================================================
    // isValid() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for valid uppercase enum names")
    void isValidShouldReturnTrueForValidUppercaseNames(@ForAll @From("genericTypes") GenericType type) {
        assertThat(GenericType.isValid(type.name())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should return true for valid lowercase enum names")
    void isValidShouldReturnTrueForValidLowercaseNames(@ForAll @From("genericTypes") GenericType type) {
        assertThat(GenericType.isValid(type.name().toLowerCase())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should handle names with leading/trailing spaces")
    void isValidShouldHandleNamesWithSpaces(@ForAll @From("genericTypes") GenericType type) {
        assertThat(GenericType.isValid("  " + type.name() + "  ")).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return false for random invalid strings")
    void isValidShouldReturnFalseForRandomStrings(
            @ForAll @StringLength(min = 10, max = 25) @AlphaChars String input) {
        boolean isValidEnumName = false;
        for (GenericType type : GenericType.values()) {
            if (type.name().equalsIgnoreCase(input.trim())) {
                isValidEnumName = true;
                break;
            }
        }
        if (!isValidEnumName) {
            assertThat(GenericType.isValid(input)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isValid should return false for null")
    void isValidShouldReturnFalseForNull() {
        assertThat(GenericType.isValid(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isValid should return false for empty string")
    void isValidShouldReturnFalseForEmptyString() {
        assertThat(GenericType.isValid("")).isFalse();
        assertThat(GenericType.isValid("   ")).isFalse();
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should contain all enum constants")
    void getAllDataShouldContainAllConstants() {
        Map<String, String> data = GenericType.getAllData();
        assertThat(data).hasSize(GenericType.values().length);
        assertThat(data).hasSize(EXPECTED_ENUM_COUNT);

        for (GenericType type : GenericType.values()) {
            assertThat(data).containsKey(type.getKey());
            assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
        }
    }

    @Property(tries = 100)
    @Label("getAllKeysList should contain all keys in order")
    void getAllKeysListShouldContainAllKeysInOrder() {
        List<String> keys = GenericType.getAllKeysList();
        assertThat(keys).hasSize(GenericType.values().length);
        assertThat(keys).hasSize(EXPECTED_ENUM_COUNT);

        int index = 0;
        for (GenericType type : GenericType.values()) {
            assertThat(keys.get(index)).isEqualTo(type.getKey());
            index++;
        }
    }

    @Property(tries = 100)
    @Label("getAllData map should be immutable")
    void getAllDataShouldBeImmutable() {
        Map<String, String> data = GenericType.getAllData();
        assertThatThrownBy(() -> data.put("NEW", "Value"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should be immutable")
    void getAllKeysListShouldBeImmutable() {
        List<String> keys = GenericType.getAllKeysList();
        assertThatThrownBy(() -> keys.add("NEW"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("Key should be lowercase")
    void keyShouldBeLowercase(@ForAll @From("genericTypes") GenericType type) {
        assertThat(type.getKey()).isEqualTo(type.getKey().toLowerCase());
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<GenericType> genericTypes() {
        return Arbitraries.of(GenericType.values());
    }
}
