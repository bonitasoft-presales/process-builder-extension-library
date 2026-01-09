package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link DataResolverType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("DataResolverType Property-Based Tests")
class DataResolverTypePropertyTest {

    private static final int EXPECTED_ENUM_COUNT = 6;

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(DataResolverType.values().length);
    }

    @Property(tries = 100)
    @Label("Each constant should be retrievable by name")
    void eachConstantShouldBeRetrievableByName(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(DataResolverType.valueOf(type.name())).isEqualTo(type);
    }

    @Property(tries = 100)
    @Label("Key should contain underscore")
    void keyShouldContainUnderscore(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(type.getKey()).contains("_");
    }

    // =========================================================================
    // isValid() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for valid uppercase enum names")
    void isValidShouldReturnTrueForValidUppercaseNames(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(DataResolverType.isValid(type.name())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should return true for valid lowercase enum names")
    void isValidShouldReturnTrueForValidLowercaseNames(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(DataResolverType.isValid(type.name().toLowerCase())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should handle names with leading/trailing spaces")
    void isValidShouldHandleNamesWithSpaces(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(DataResolverType.isValid("  " + type.name() + "  ")).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return false for random invalid strings")
    void isValidShouldReturnFalseForRandomStrings(
            @ForAll @StringLength(min = 25, max = 40) @AlphaChars String input) {
        boolean isValidEnumName = false;
        for (DataResolverType type : DataResolverType.values()) {
            if (type.name().equalsIgnoreCase(input.trim())) {
                isValidEnumName = true;
                break;
            }
        }
        if (!isValidEnumName) {
            assertThat(DataResolverType.isValid(input)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isValid should return false for null")
    void isValidShouldReturnFalseForNull() {
        assertThat(DataResolverType.isValid(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isValid should return false for empty string")
    void isValidShouldReturnFalseForEmptyString() {
        assertThat(DataResolverType.isValid("")).isFalse();
        assertThat(DataResolverType.isValid("   ")).isFalse();
    }

    // =========================================================================
    // isValidKey() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValidKey should return true for valid keys")
    void isValidKeyShouldReturnTrueForValidKeys(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(DataResolverType.isValidKey(type.getKey())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValidKey should handle keys with leading/trailing spaces")
    void isValidKeyShouldHandleKeysWithSpaces(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(DataResolverType.isValidKey("  " + type.getKey() + "  ")).isTrue();
    }

    @Property(tries = 100)
    @Label("isValidKey should return false for null")
    void isValidKeyShouldReturnFalseForNull() {
        assertThat(DataResolverType.isValidKey(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isValidKey should return false for empty string")
    void isValidKeyShouldReturnFalseForEmptyString() {
        assertThat(DataResolverType.isValidKey("")).isFalse();
        assertThat(DataResolverType.isValidKey("   ")).isFalse();
    }

    // =========================================================================
    // fromKey() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("fromKey should return correct enum for valid keys")
    void fromKeyShouldReturnCorrectEnum(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(DataResolverType.fromKey(type.getKey())).isEqualTo(type);
    }

    @Property(tries = 100)
    @Label("fromKey should handle keys with spaces")
    void fromKeyShouldHandleKeysWithSpaces(@ForAll @From("dataResolverTypes") DataResolverType type) {
        assertThat(DataResolverType.fromKey("  " + type.getKey() + "  ")).isEqualTo(type);
    }

    @Property(tries = 100)
    @Label("fromKey should return null for null input")
    void fromKeyShouldReturnNullForNull() {
        assertThat(DataResolverType.fromKey(null)).isNull();
    }

    @Property(tries = 500)
    @Label("fromKey should return null for random invalid keys")
    void fromKeyShouldReturnNullForInvalidKeys(
            @ForAll @StringLength(min = 20, max = 35) @AlphaChars String input) {
        boolean isValidKey = false;
        for (DataResolverType type : DataResolverType.values()) {
            if (type.getKey().equals(input.trim())) {
                isValidKey = true;
                break;
            }
        }
        if (!isValidKey) {
            assertThat(DataResolverType.fromKey(input)).isNull();
        }
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should contain all enum constants")
    void getAllDataShouldContainAllConstants() {
        Map<String, String> data = DataResolverType.getAllData();
        assertThat(data).hasSize(DataResolverType.values().length);
        assertThat(data).hasSize(EXPECTED_ENUM_COUNT);

        for (DataResolverType type : DataResolverType.values()) {
            assertThat(data).containsKey(type.getKey());
            assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
        }
    }

    @Property(tries = 100)
    @Label("getAllKeysList should contain all keys in order")
    void getAllKeysListShouldContainAllKeysInOrder() {
        List<String> keys = DataResolverType.getAllKeysList();
        assertThat(keys).hasSize(DataResolverType.values().length);
        assertThat(keys).hasSize(EXPECTED_ENUM_COUNT);

        int index = 0;
        for (DataResolverType type : DataResolverType.values()) {
            assertThat(keys.get(index)).isEqualTo(type.getKey());
            index++;
        }
    }

    @Property(tries = 100)
    @Label("getAllData map should be immutable")
    void getAllDataShouldBeImmutable() {
        Map<String, String> data = DataResolverType.getAllData();
        assertThatThrownBy(() -> data.put("NEW", "Value"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should be immutable")
    void getAllKeysListShouldBeImmutable() {
        List<String> keys = DataResolverType.getAllKeysList();
        assertThatThrownBy(() -> keys.add("NEW"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<DataResolverType> dataResolverTypes() {
        return Arbitraries.of(DataResolverType.values());
    }
}
