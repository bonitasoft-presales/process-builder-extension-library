package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link RecipientsType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("RecipientsType Property-Based Tests")
class RecipientsTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("recipientsTypes") RecipientsType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("recipientsTypes") RecipientsType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Key should contain underscore for multi-word types")
    void keyShouldContainUnderscoreForMultiWordTypes(@ForAll @From("recipientsTypes") RecipientsType type) {
        // Keys like "step_users" and "step_managers" should contain underscore
        if (type == RecipientsType.STEP_USERS || type == RecipientsType.STEP_MANAGERS) {
            assertThat(type.getKey()).contains("_");
        }
    }

    // =========================================================================
    // fromKey() PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("fromKey should return correct enum for valid key")
    void fromKeyShouldReturnCorrectEnumForValidKey(@ForAll @From("recipientsTypes") RecipientsType type) {
        assertThat(RecipientsType.fromKey(type.getKey())).isEqualTo(type);
    }

    @Property(tries = 500)
    @Label("fromKey should be case-insensitive")
    void fromKeyShouldBeCaseInsensitive(@ForAll @From("recipientsTypes") RecipientsType type) {
        assertThat(RecipientsType.fromKey(type.getKey().toUpperCase())).isEqualTo(type);
        assertThat(RecipientsType.fromKey(type.getKey().toLowerCase())).isEqualTo(type);
    }

    @Property(tries = 500)
    @Label("fromKey should handle leading/trailing spaces")
    void fromKeyShouldHandleSpaces(@ForAll @From("recipientsTypes") RecipientsType type) {
        assertThat(RecipientsType.fromKey("  " + type.getKey() + "  ")).isEqualTo(type);
    }

    @Property(tries = 500)
    @Label("fromKey should return null for invalid keys")
    void fromKeyShouldReturnNullForInvalidKeys(
            @ForAll @StringLength(min = 1, max = 30) @AlphaChars String input) {
        // Filter out valid keys
        boolean isValidKey = false;
        for (RecipientsType type : RecipientsType.values()) {
            if (type.getKey().equalsIgnoreCase(input.trim())) {
                isValidKey = true;
                break;
            }
        }
        if (!isValidKey) {
            assertThat(RecipientsType.fromKey(input)).isNull();
        }
    }

    @Property(tries = 100)
    @Label("fromKey should return null for null input")
    void fromKeyShouldReturnNullForNullInput() {
        assertThat(RecipientsType.fromKey(null)).isNull();
    }

    @Property(tries = 100)
    @Label("fromKey should return null for empty input")
    void fromKeyShouldReturnNullForEmptyInput() {
        assertThat(RecipientsType.fromKey("")).isNull();
        assertThat(RecipientsType.fromKey("   ")).isNull();
    }

    // =========================================================================
    // ROUND-TRIP PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("fromKey(getKey()) should return same enum")
    void roundTripShouldPreserveEnum(@ForAll @From("recipientsTypes") RecipientsType type) {
        assertThat(RecipientsType.fromKey(type.getKey())).isSameAs(type);
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should have correct size")
    void getAllDataShouldHaveCorrectSize() {
        assertThat(RecipientsType.getAllData()).hasSize(RecipientsType.values().length);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should have correct size")
    void getAllKeysListShouldHaveCorrectSize() {
        assertThat(RecipientsType.getAllKeysList()).hasSize(RecipientsType.values().length);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<RecipientsType> recipientsTypes() {
        return Arbitraries.of(RecipientsType.values());
    }
}
