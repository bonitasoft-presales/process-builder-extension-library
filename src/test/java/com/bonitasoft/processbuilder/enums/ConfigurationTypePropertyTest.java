package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ConfigurationType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ConfigurationType Property-Based Tests")
class ConfigurationTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("configurationTypes") ConfigurationType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("configurationTypes") ConfigurationType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("configurationTypes") ConfigurationType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(ConfigurationType.values().length);
    }

    // =========================================================================
    // isValid() PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("isValid should return true for valid uppercase enum names")
    void isValidShouldReturnTrueForValidUppercaseNames(@ForAll @From("configurationTypes") ConfigurationType type) {
        assertThat(ConfigurationType.isValid(type.name())).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return true for valid lowercase enum names")
    void isValidShouldReturnTrueForValidLowercaseNames(@ForAll @From("configurationTypes") ConfigurationType type) {
        assertThat(ConfigurationType.isValid(type.name().toLowerCase())).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should handle names with leading/trailing spaces")
    void isValidShouldHandleNamesWithSpaces(@ForAll @From("configurationTypes") ConfigurationType type) {
        assertThat(ConfigurationType.isValid("  " + type.name() + "  ")).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return false for random invalid strings")
    void isValidShouldReturnFalseForRandomStrings(
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String input) {
        // Filter out valid enum names
        boolean isValidEnumName = false;
        for (ConfigurationType type : ConfigurationType.values()) {
            if (type.name().equalsIgnoreCase(input.trim())) {
                isValidEnumName = true;
                break;
            }
        }
        if (!isValidEnumName) {
            assertThat(ConfigurationType.isValid(input)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isValid should return false for null")
    void isValidShouldReturnFalseForNull() {
        assertThat(ConfigurationType.isValid(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isValid should return false for empty string")
    void isValidShouldReturnFalseForEmptyString() {
        assertThat(ConfigurationType.isValid("")).isFalse();
        assertThat(ConfigurationType.isValid("   ")).isFalse();
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should contain all enum constants")
    void getAllDataShouldContainAllConstants() {
        Map<String, String> data = ConfigurationType.getAllData();
        assertThat(data).hasSize(ConfigurationType.values().length);

        for (ConfigurationType type : ConfigurationType.values()) {
            assertThat(data).containsKey(type.getKey());
            assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
        }
    }

    @Property(tries = 100)
    @Label("getAllKeysList should contain all keys in order")
    void getAllKeysListShouldContainAllKeysInOrder() {
        List<String> keys = ConfigurationType.getAllKeysList();
        assertThat(keys).hasSize(ConfigurationType.values().length);

        int index = 0;
        for (ConfigurationType type : ConfigurationType.values()) {
            assertThat(keys.get(index)).isEqualTo(type.getKey());
            index++;
        }
    }

    @Property(tries = 100)
    @Label("getAllData map should be immutable")
    void getAllDataShouldBeImmutable() {
        Map<String, String> data = ConfigurationType.getAllData();
        assertThatThrownBy(() -> data.put("NEW", "Value"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should be immutable")
    void getAllKeysListShouldBeImmutable() {
        List<String> keys = ConfigurationType.getAllKeysList();
        assertThatThrownBy(() -> keys.add("NEW"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<ConfigurationType> configurationTypes() {
        return Arbitraries.of(ConfigurationType.values());
    }
}
