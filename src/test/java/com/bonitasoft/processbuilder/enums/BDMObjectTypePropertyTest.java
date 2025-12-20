package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link BDMObjectType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("BDMObjectType Property-Based Tests")
class BDMObjectTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("bdmObjectTypes") BDMObjectType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("bdmObjectTypes") BDMObjectType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("bdmObjectTypes") BDMObjectType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(BDMObjectType.values().length);
    }

    @Property(tries = 100)
    @Label("Each constant should be retrievable by name")
    void eachConstantShouldBeRetrievableByName(@ForAll @From("bdmObjectTypes") BDMObjectType type) {
        assertThat(BDMObjectType.valueOf(type.name())).isEqualTo(type);
    }

    // =========================================================================
    // fromKey() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("fromKey should return correct type for valid key")
    void fromKeyShouldReturnCorrectType(@ForAll @From("bdmObjectTypes") BDMObjectType type) {
        assertThat(BDMObjectType.fromKey(type.getKey())).isEqualTo(type);
    }

    @Property(tries = 100)
    @Label("fromKey should be case-insensitive")
    void fromKeyShouldBeCaseInsensitive(@ForAll @From("bdmObjectTypes") BDMObjectType type) {
        assertThat(BDMObjectType.fromKey(type.getKey().toLowerCase())).isEqualTo(type);
        assertThat(BDMObjectType.fromKey(type.getKey().toUpperCase())).isEqualTo(type);
    }

    @Property(tries = 500)
    @Label("fromKey should throw for invalid key")
    void fromKeyShouldThrowForInvalidKey(
            @ForAll @StringLength(min = 20, max = 35) @AlphaChars String input) {
        boolean isValidKey = false;
        for (BDMObjectType type : BDMObjectType.values()) {
            if (type.getKey().equalsIgnoreCase(input)) {
                isValidKey = true;
                break;
            }
        }
        if (!isValidKey) {
            assertThatThrownBy(() -> BDMObjectType.fromKey(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No BDMObjectType found for key");
        }
    }

    // =========================================================================
    // toString() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("toString should contain enum name, key and description")
    void toStringShouldContainAllInfo(@ForAll @From("bdmObjectTypes") BDMObjectType type) {
        String result = type.toString();
        assertThat(result)
            .contains(type.name())
            .contains(type.getKey())
            .contains(type.getDescription());
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should map keys to descriptions correctly")
    void getAllDataShouldMapKeysToDescriptions() {
        Map<String, String> data = BDMObjectType.getAllData();

        for (BDMObjectType type : BDMObjectType.values()) {
            assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
        }
    }

    @Property(tries = 100)
    @Label("getAllKeysList should preserve enum declaration order")
    void getAllKeysListShouldPreserveOrder() {
        List<String> keys = BDMObjectType.getAllKeysList();

        int index = 0;
        for (BDMObjectType type : BDMObjectType.values()) {
            assertThat(keys.get(index)).isEqualTo(type.getKey());
            index++;
        }
    }

    @Property(tries = 100)
    @Label("Collections should be unmodifiable")
    void collectionsShouldBeUnmodifiable() {
        Map<String, String> data = BDMObjectType.getAllData();
        List<String> keys = BDMObjectType.getAllKeysList();

        assertThatThrownBy(() -> data.put("new", "value"))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> keys.add("new"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<BDMObjectType> bdmObjectTypes() {
        return Arbitraries.of(BDMObjectType.values());
    }
}
