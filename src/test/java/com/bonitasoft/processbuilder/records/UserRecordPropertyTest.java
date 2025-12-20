package com.bonitasoft.processbuilder.records;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link UserRecord} record.
 * Tests invariants that must hold for any valid instance.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("UserRecord Property-Based Tests")
class UserRecordPropertyTest {

    // =========================================================================
    // EQUALITY PROPERTIES
    // =========================================================================

    @Property(tries = 1000)
    @Label("Equality should be reflexive: x.equals(x)")
    void equalityShouldBeReflexive(@ForAll @From("validUserRecords") UserRecord record) {
        assertThat(record).isEqualTo(record);
    }

    @Property(tries = 1000)
    @Label("Equality should be symmetric: x.equals(y) == y.equals(x)")
    void equalityShouldBeSymmetric(
            @ForAll @From("validUserRecords") UserRecord record1,
            @ForAll @From("validUserRecords") UserRecord record2) {
        assertThat(record1.equals(record2)).isEqualTo(record2.equals(record1));
    }

    @Property(tries = 500)
    @Label("Equality with same values should be true")
    void equalityWithSameValuesShouldBeTrue(
            @ForAll @LongRange(min = 1, max = 1000) Long id,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String userName,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String fullName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String firstName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String lastName,
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String email) {

        UserRecord record1 = new UserRecord(id, userName, fullName, firstName, lastName, email);
        UserRecord record2 = new UserRecord(id, userName, fullName, firstName, lastName, email);

        assertThat(record1).isEqualTo(record2);
    }

    @Property(tries = 500)
    @Label("equals(null) should return false")
    void equalsNullShouldReturnFalse(@ForAll @From("validUserRecords") UserRecord record) {
        assertThat(record.equals(null)).isFalse();
    }

    // =========================================================================
    // HASHCODE PROPERTIES
    // =========================================================================

    @Property(tries = 1000)
    @Label("HashCode should be consistent with equals")
    void hashCodeShouldBeConsistentWithEquals(
            @ForAll @From("validUserRecords") UserRecord record1,
            @ForAll @From("validUserRecords") UserRecord record2) {
        if (record1.equals(record2)) {
            assertThat(record1.hashCode()).isEqualTo(record2.hashCode());
        }
    }

    @Property(tries = 500)
    @Label("HashCode should be stable across multiple calls")
    void hashCodeShouldBeStable(@ForAll @From("validUserRecords") UserRecord record) {
        int hash1 = record.hashCode();
        int hash2 = record.hashCode();
        int hash3 = record.hashCode();

        assertThat(hash1).isEqualTo(hash2).isEqualTo(hash3);
    }

    // =========================================================================
    // TOSTRING PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("toString should never throw")
    void toStringShouldNeverThrow(@ForAll @From("recordsWithNulls") UserRecord record) {
        assertThatCode(() -> record.toString()).doesNotThrowAnyException();
    }

    @Property(tries = 500)
    @Label("toString should never return null")
    void toStringShouldNeverReturnNull(@ForAll @From("validUserRecords") UserRecord record) {
        assertThat(record.toString()).isNotNull();
    }

    @Property(tries = 500)
    @Label("toString should contain class name")
    void toStringShouldContainClassName(@ForAll @From("validUserRecords") UserRecord record) {
        assertThat(record.toString()).contains("UserRecord");
    }

    // =========================================================================
    // ACCESSOR PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("Accessors should return constructor values")
    void accessorsShouldReturnConstructorValues(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) Long id,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String userName,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String fullName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String firstName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String lastName,
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String email) {

        UserRecord record = new UserRecord(id, userName, fullName, firstName, lastName, email);

        assertThat(record.id()).isEqualTo(id);
        assertThat(record.userName()).isEqualTo(userName);
        assertThat(record.fullName()).isEqualTo(fullName);
        assertThat(record.firstName()).isEqualTo(firstName);
        assertThat(record.lastName()).isEqualTo(lastName);
        assertThat(record.email()).isEqualTo(email);
    }

    // =========================================================================
    // DISPLAY NAME PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("displayName should return fullName when fullName is not blank")
    void displayNameShouldReturnFullNameWhenAvailable(
            @ForAll @LongRange(min = 1, max = 1000) Long id,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String userName,
            @ForAll @StringLength(min = 1, max = 100) @AlphaChars String fullName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String firstName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String lastName,
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String email) {

        UserRecord record = new UserRecord(id, userName, fullName, firstName, lastName, email);
        assertThat(record.displayName()).isEqualTo(fullName);
    }

    @Property(tries = 300)
    @Label("displayName should return firstName + lastName when fullName is null")
    void displayNameShouldReturnFirstAndLastNameWhenFullNameIsNull(
            @ForAll @LongRange(min = 1, max = 1000) Long id,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String userName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String firstName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String lastName,
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String email) {

        UserRecord record = new UserRecord(id, userName, null, firstName, lastName, email);
        assertThat(record.displayName()).isEqualTo(firstName + " " + lastName);
    }

    @Property(tries = 300)
    @Label("displayName should return firstName when only firstName is available")
    void displayNameShouldReturnFirstNameWhenOnlyFirstNameAvailable(
            @ForAll @LongRange(min = 1, max = 1000) Long id,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String userName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String firstName,
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String email) {

        UserRecord record = new UserRecord(id, userName, null, firstName, null, email);
        assertThat(record.displayName()).isEqualTo(firstName);
    }

    @Property(tries = 300)
    @Label("displayName should return lastName when only lastName is available")
    void displayNameShouldReturnLastNameWhenOnlyLastNameAvailable(
            @ForAll @LongRange(min = 1, max = 1000) Long id,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String userName,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String lastName,
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String email) {

        UserRecord record = new UserRecord(id, userName, null, null, lastName, email);
        assertThat(record.displayName()).isEqualTo(lastName);
    }

    @Property(tries = 300)
    @Label("displayName should return userName when no name fields available")
    void displayNameShouldReturnUserNameWhenNoNameFieldsAvailable(
            @ForAll @LongRange(min = 1, max = 1000) Long id,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String userName,
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String email) {

        UserRecord record = new UserRecord(id, userName, null, null, null, email);
        assertThat(record.displayName()).isEqualTo(userName);
    }

    @Property(tries = 300)
    @Label("displayName should return empty string when all name fields are null including userName")
    void displayNameShouldReturnEmptyWhenAllNull(
            @ForAll @LongRange(min = 1, max = 1000) Long id,
            @ForAll @StringLength(min = 5, max = 50) @AlphaChars String email) {

        UserRecord record = new UserRecord(id, null, null, null, null, email);
        assertThat(record.displayName()).isEmpty();
    }

    @Property(tries = 300)
    @Label("displayName should never throw exception")
    void displayNameShouldNeverThrow(@ForAll @From("recordsWithNulls") UserRecord record) {
        assertThatCode(() -> record.displayName()).doesNotThrowAnyException();
    }

    @Property(tries = 300)
    @Label("displayName should never return null")
    void displayNameShouldNeverReturnNull(@ForAll @From("recordsWithNulls") UserRecord record) {
        assertThat(record.displayName()).isNotNull();
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<UserRecord> validUserRecords() {
        Arbitrary<Long> ids = Arbitraries.longs().greaterOrEqual(1L);
        Arbitrary<String> userNames = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<String> fullNames = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(100);
        Arbitrary<String> firstNames = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<String> lastNames = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<String> emails = Arbitraries.strings().alpha().ofMinLength(5).ofMaxLength(50);

        return Combinators.combine(ids, userNames, fullNames, firstNames, lastNames, emails)
            .as(UserRecord::new);
    }

    @Provide
    Arbitrary<UserRecord> recordsWithNulls() {
        Arbitrary<Long> ids = Arbitraries.longs().greaterOrEqual(1L).injectNull(0.2);
        Arbitrary<String> userNames = Arbitraries.strings().alpha().ofMaxLength(50).injectNull(0.2);
        Arbitrary<String> fullNames = Arbitraries.strings().alpha().ofMaxLength(100).injectNull(0.2);
        Arbitrary<String> firstNames = Arbitraries.strings().alpha().ofMaxLength(50).injectNull(0.2);
        Arbitrary<String> lastNames = Arbitraries.strings().alpha().ofMaxLength(50).injectNull(0.2);
        Arbitrary<String> emails = Arbitraries.strings().alpha().ofMaxLength(50).injectNull(0.2);

        return Combinators.combine(ids, userNames, fullNames, firstNames, lastNames, emails)
            .as(UserRecord::new);
    }
}
