package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link FormContentType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("FormContentType Property-Based Tests")
class FormContentTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("formContentTypes") FormContentType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("formContentTypes") FormContentType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("formContentTypes") FormContentType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(FormContentType.values().length);
    }

    @Property(tries = 100)
    @Label("Each constant should be retrievable by name")
    void eachConstantShouldBeRetrievableByName(@ForAll @From("formContentTypes") FormContentType type) {
        assertThat(FormContentType.valueOf(type.name())).isEqualTo(type);
    }

    @Property(tries = 100)
    @Label("Key should be lowercase version of name")
    void keyShouldBeLowercaseVersionOfName(@ForAll @From("formContentTypes") FormContentType type) {
        assertThat(type.getKey()).isEqualTo(type.name().toLowerCase());
    }

    // =========================================================================
    // isValid() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for valid enum names (case-insensitive)")
    void isValidShouldReturnTrueForValidEnumNames(@ForAll @From("formContentTypes") FormContentType type) {
        assertThat(FormContentType.isValid(type.name())).isTrue();
        assertThat(FormContentType.isValid(type.name().toLowerCase())).isTrue();
        assertThat(FormContentType.isValid(type.name().toUpperCase())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should handle spaces around valid names")
    void isValidShouldHandleSpacesAroundValidNames(@ForAll @From("formContentTypes") FormContentType type) {
        assertThat(FormContentType.isValid("  " + type.name() + "  ")).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return false for random invalid strings")
    void isValidShouldReturnFalseForInvalidStrings(
            @ForAll @StringLength(min = 15, max = 30) @AlphaChars String input) {
        boolean isValidName = false;
        for (FormContentType type : FormContentType.values()) {
            if (type.name().equalsIgnoreCase(input.trim())) {
                isValidName = true;
                break;
            }
        }
        if (!isValidName) {
            assertThat(FormContentType.isValid(input)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isValid should return false for null")
    void isValidShouldReturnFalseForNull() {
        assertThat(FormContentType.isValid(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isValid should return false for empty string")
    void isValidShouldReturnFalseForEmpty() {
        assertThat(FormContentType.isValid("")).isFalse();
        assertThat(FormContentType.isValid("   ")).isFalse();
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should map keys to descriptions correctly")
    void getAllDataShouldMapKeysToDescriptions() {
        Map<String, String> data = FormContentType.getAllData();

        for (FormContentType type : FormContentType.values()) {
            assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
        }
    }

    @Property(tries = 100)
    @Label("getAllData size should match enum values count")
    void getAllDataSizeShouldMatchEnumCount() {
        Map<String, String> data = FormContentType.getAllData();

        assertThat(data).hasSize(FormContentType.values().length);
        assertThat(data.size()).isEqualTo(4); // Explicit count kills mutations
    }

    @Property(tries = 100)
    @Label("getAllKeysList should preserve enum declaration order")
    void getAllKeysListShouldPreserveOrder() {
        List<String> keys = FormContentType.getAllKeysList();

        int index = 0;
        for (FormContentType type : FormContentType.values()) {
            assertThat(keys.get(index)).isEqualTo(type.getKey());
            index++;
        }
    }

    @Property(tries = 100)
    @Label("getAllKeysList size should match enum values count")
    void getAllKeysListSizeShouldMatchEnumCount() {
        List<String> keys = FormContentType.getAllKeysList();

        assertThat(keys).hasSize(FormContentType.values().length);
        assertThat(keys.size()).isEqualTo(4); // Explicit count kills mutations
    }

    @Property(tries = 100)
    @Label("Collections should be unmodifiable")
    void collectionsShouldBeUnmodifiable() {
        Map<String, String> data = FormContentType.getAllData();
        List<String> keys = FormContentType.getAllKeysList();

        assertThatThrownBy(() -> data.put("new", "value"))
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> keys.add("new"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("getAllData and getAllKeysList should have matching keys")
    void getAllDataAndKeysListShouldMatch() {
        Map<String, String> data = FormContentType.getAllData();
        List<String> keys = FormContentType.getAllKeysList();

        // Keys from map should match keys from list
        assertThat(data.keySet()).containsExactlyInAnyOrderElementsOf(keys);
    }

    @Property(tries = 100)
    @Label("Each enum constant key should be unique")
    void enumConstantKeysShouldBeUnique() {
        List<String> keys = FormContentType.getAllKeysList();

        // No duplicates
        assertThat(keys).doesNotHaveDuplicates();
    }

    // =========================================================================
    // SPECIFIC VALUE PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("NOTIFICATIONS should have notifications key")
    void notificationsShouldHaveCorrectKey() {
        assertThat(FormContentType.NOTIFICATIONS.getKey()).isEqualTo("notifications");
    }

    @Property(tries = 100)
    @Label("DELAY should have delay key")
    void delayShouldHaveCorrectKey() {
        assertThat(FormContentType.DELAY.getKey()).isEqualTo("delay");
    }

    @Property(tries = 100)
    @Label("ALERT should have alert key")
    void alertShouldHaveCorrectKey() {
        assertThat(FormContentType.ALERT.getKey()).isEqualTo("alert");
    }

    @Property(tries = 100)
    @Label("MESSAGE should have message key")
    void messageShouldHaveCorrectKey() {
        assertThat(FormContentType.MESSAGE.getKey()).isEqualTo("message");
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<FormContentType> formContentTypes() {
        return Arbitraries.of(FormContentType.values());
    }
}
