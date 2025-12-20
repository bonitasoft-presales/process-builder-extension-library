package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ActionType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ActionType Property-Based Tests")
class ActionTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("actionTypes") ActionType actionType) {
        assertThat(actionType.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("actionTypes") ActionType actionType) {
        assertThat(actionType.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum name() should never throw")
    void enumNameShouldNeverThrow(@ForAll @From("actionTypes") ActionType actionType) {
        assertThatCode(() -> actionType.name()).doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("actionTypes") ActionType actionType) {
        assertThat(actionType.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(ActionType.values().length);
    }

    // =========================================================================
    // INSERT VALIDATION PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("INSERT should validate empty persistence ID as true")
    void insertShouldValidateEmptyIdAsTrue() {
        assertThat(ActionType.INSERT.isValid("")).isTrue();
        assertThat(ActionType.INSERT.isValid("   ")).isTrue();
        assertThat(ActionType.INSERT.isValid("\t")).isTrue();
    }

    @Property(tries = 500)
    @Label("INSERT should validate any non-empty string as false")
    void insertShouldValidateNonEmptyStringAsFalse(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String input) {
        assertThat(ActionType.INSERT.isValid(input)).isFalse();
    }

    @Property(tries = 500)
    @Label("INSERT should validate numeric strings as false")
    void insertShouldValidateNumericStringsAsFalse(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) Long id) {
        assertThat(ActionType.INSERT.isValid(String.valueOf(id))).isFalse();
    }

    // =========================================================================
    // UPDATE VALIDATION PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("UPDATE should validate positive numeric strings as true")
    void updateShouldValidatePositiveNumericAsTrue(
            @ForAll @LongRange(min = 0, max = Long.MAX_VALUE) Long id) {
        assertThat(ActionType.UPDATE.isValid(String.valueOf(id))).isTrue();
    }

    @Property(tries = 500)
    @Label("UPDATE should validate empty strings as false")
    void updateShouldValidateEmptyStringAsFalse() {
        assertThat(ActionType.UPDATE.isValid("")).isFalse();
        assertThat(ActionType.UPDATE.isValid("   ")).isFalse();
    }

    @Property(tries = 500)
    @Label("UPDATE should validate alphabetic strings as false")
    void updateShouldValidateAlphabeticStringsAsFalse(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String input) {
        assertThat(ActionType.UPDATE.isValid(input)).isFalse();
    }

    // =========================================================================
    // DELETE VALIDATION PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("DELETE should validate positive numeric strings as true")
    void deleteShouldValidatePositiveNumericAsTrue(
            @ForAll @LongRange(min = 0, max = Long.MAX_VALUE) Long id) {
        assertThat(ActionType.DELETE.isValid(String.valueOf(id))).isTrue();
    }

    @Property(tries = 500)
    @Label("DELETE should validate empty strings as false")
    void deleteShouldValidateEmptyStringAsFalse() {
        assertThat(ActionType.DELETE.isValid("")).isFalse();
        assertThat(ActionType.DELETE.isValid("   ")).isFalse();
    }

    @Property(tries = 500)
    @Label("DELETE should validate alphabetic strings as false")
    void deleteShouldValidateAlphabeticStringsAsFalse(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String input) {
        assertThat(ActionType.DELETE.isValid(input)).isFalse();
    }

    // =========================================================================
    // UPDATE AND DELETE CONSISTENCY PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("UPDATE and DELETE should have same validation for numeric IDs")
    void updateAndDeleteShouldHaveSameValidationForNumericIds(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) Long id) {
        String idStr = String.valueOf(id);
        assertThat(ActionType.UPDATE.isValid(idStr))
            .isEqualTo(ActionType.DELETE.isValid(idStr));
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should return map with all constants")
    void getAllDataShouldReturnMapWithAllConstants() {
        Map<String, String> data = ActionType.getAllData();
        assertThat(data).hasSize(ActionType.values().length);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should return list with all keys")
    void getAllKeysListShouldReturnListWithAllKeys() {
        List<String> keys = ActionType.getAllKeysList();
        assertThat(keys).hasSize(ActionType.values().length);
    }

    @Property(tries = 100)
    @Label("getAllData keys should match getAllKeysList")
    void getAllDataKeysShouldMatchGetAllKeysList() {
        Map<String, String> data = ActionType.getAllData();
        List<String> keys = ActionType.getAllKeysList();
        assertThat(data.keySet()).containsExactlyInAnyOrderElementsOf(keys);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<ActionType> actionTypes() {
        return Arbitraries.of(ActionType.values());
    }
}
