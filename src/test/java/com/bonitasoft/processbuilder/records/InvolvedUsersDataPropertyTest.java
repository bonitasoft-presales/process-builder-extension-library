package com.bonitasoft.processbuilder.records;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link InvolvedUsersData} record.
 * Tests invariants that must hold for any valid instance.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("InvolvedUsersData Property-Based Tests")
class InvolvedUsersDataPropertyTest {

    // =========================================================================
    // IMMUTABILITY PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("Memberships list should be immutable (cannot be modified externally after construction)")
    void membershipsListShouldBeImmutable(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepManagerRef,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepUserRef,
            @ForAll List<@StringLength(min = 1, max = 30) @AlphaChars String> memberships) {

        List<String> originalMemberships = new ArrayList<>(memberships);
        InvolvedUsersData data = new InvolvedUsersData(stepManagerRef, stepUserRef, originalMemberships);

        // Attempt to modify the original list after construction
        originalMemberships.add("SHOULD_NOT_APPEAR");

        // The record's internal list should not be affected
        assertThat(data.memberships()).doesNotContain("SHOULD_NOT_APPEAR");
    }

    @Property(tries = 300)
    @Label("Memberships accessor should return unmodifiable list")
    void membershipsAccessorShouldReturnUnmodifiableList(
            @ForAll @From("validInvolvedUsersData") InvolvedUsersData data) {

        assertThatThrownBy(() -> data.memberships().add("NEW_ITEM"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // EQUALITY PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("Equality should be reflexive")
    void equalityShouldBeReflexive(@ForAll @From("validInvolvedUsersData") InvolvedUsersData data) {
        assertThat(data).isEqualTo(data);
    }

    @Property(tries = 300)
    @Label("Equality should be symmetric")
    void equalityShouldBeSymmetric(
            @ForAll @From("validInvolvedUsersData") InvolvedUsersData data1,
            @ForAll @From("validInvolvedUsersData") InvolvedUsersData data2) {
        assertThat(data1.equals(data2)).isEqualTo(data2.equals(data1));
    }

    @Property(tries = 300)
    @Label("Equal objects should have equal hashCodes")
    void equalObjectsShouldHaveEqualHashCodes(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepManagerRef,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepUserRef,
            @ForAll List<@StringLength(min = 1, max = 30) @AlphaChars String> memberships) {

        InvolvedUsersData data1 = new InvolvedUsersData(stepManagerRef, stepUserRef, memberships);
        InvolvedUsersData data2 = new InvolvedUsersData(stepManagerRef, stepUserRef, memberships);

        assertThat(data1).isEqualTo(data2);
        assertThat(data1.hashCode()).isEqualTo(data2.hashCode());
    }

    @Property(tries = 300)
    @Label("equals(null) should return false")
    void equalsNullShouldReturnFalse(@ForAll @From("validInvolvedUsersData") InvolvedUsersData data) {
        assertThat(data.equals(null)).isFalse();
    }

    // =========================================================================
    // HASHCODE PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("HashCode should be stable across multiple calls")
    void hashCodeShouldBeStable(@ForAll @From("validInvolvedUsersData") InvolvedUsersData data) {
        int hash1 = data.hashCode();
        int hash2 = data.hashCode();
        int hash3 = data.hashCode();

        assertThat(hash1).isEqualTo(hash2).isEqualTo(hash3);
    }

    // =========================================================================
    // TOSTRING PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("toString should never return null")
    void toStringShouldNeverReturnNull(@ForAll @From("validInvolvedUsersData") InvolvedUsersData data) {
        assertThat(data.toString()).isNotNull();
    }

    @Property(tries = 300)
    @Label("toString should contain class name")
    void toStringShouldContainClassName(@ForAll @From("validInvolvedUsersData") InvolvedUsersData data) {
        assertThat(data.toString()).contains("InvolvedUsersData");
    }

    // =========================================================================
    // GET REF STEPS ARRAY PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("getRefStepsArray should return array with both refs when both are non-null and non-empty")
    void getRefStepsArrayShouldReturnBothRefsWhenBothValid(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepManagerRef,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepUserRef,
            @ForAll List<@StringLength(min = 1, max = 30) @AlphaChars String> memberships) {

        InvolvedUsersData data = new InvolvedUsersData(stepManagerRef, stepUserRef, memberships);
        String[] result = data.getRefStepsArray();

        assertThat(result).hasSize(2);
        assertThat(result).contains(stepManagerRef, stepUserRef);
    }

    @Property(tries = 300)
    @Label("getRefStepsArray should exclude null refs")
    void getRefStepsArrayShouldExcludeNullRefs(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepUserRef,
            @ForAll List<@StringLength(min = 1, max = 30) @AlphaChars String> memberships) {

        InvolvedUsersData data = new InvolvedUsersData(null, stepUserRef, memberships);
        String[] result = data.getRefStepsArray();

        assertThat(result).hasSize(1);
        assertThat(result).contains(stepUserRef);
    }

    @Property(tries = 300)
    @Label("getRefStepsArray should exclude empty refs")
    void getRefStepsArrayShouldExcludeEmptyRefs(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepManagerRef,
            @ForAll List<@StringLength(min = 1, max = 30) @AlphaChars String> memberships) {

        InvolvedUsersData data = new InvolvedUsersData(stepManagerRef, "", memberships);
        String[] result = data.getRefStepsArray();

        assertThat(result).hasSize(1);
        assertThat(result).contains(stepManagerRef);
    }

    @Property(tries = 300)
    @Label("getRefStepsArray should exclude blank refs")
    void getRefStepsArrayShouldExcludeBlankRefs(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepManagerRef,
            @ForAll List<@StringLength(min = 1, max = 30) @AlphaChars String> memberships) {

        InvolvedUsersData data = new InvolvedUsersData(stepManagerRef, "   ", memberships);
        String[] result = data.getRefStepsArray();

        assertThat(result).hasSize(1);
        assertThat(result).contains(stepManagerRef);
    }

    @Property(tries = 100)
    @Label("getRefStepsArray should return empty array when both refs are null")
    void getRefStepsArrayShouldReturnEmptyWhenBothNull(
            @ForAll List<@StringLength(min = 1, max = 30) @AlphaChars String> memberships) {

        InvolvedUsersData data = new InvolvedUsersData(null, null, memberships);
        String[] result = data.getRefStepsArray();

        assertThat(result).isEmpty();
    }

    @Property(tries = 300)
    @Label("getRefStepsArray should never throw exception")
    void getRefStepsArrayShouldNeverThrow(@ForAll @From("dataWithNulls") InvolvedUsersData data) {
        assertThatCode(() -> data.getRefStepsArray()).doesNotThrowAnyException();
    }

    @Property(tries = 300)
    @Label("getRefStepsArray should never return null")
    void getRefStepsArrayShouldNeverReturnNull(@ForAll @From("dataWithNulls") InvolvedUsersData data) {
        assertThat(data.getRefStepsArray()).isNotNull();
    }

    // =========================================================================
    // ACCESSOR PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("Accessors should return constructor values")
    void accessorsShouldReturnConstructorValues(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepManagerRef,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepUserRef,
            @ForAll List<@StringLength(min = 1, max = 30) @AlphaChars String> memberships) {

        InvolvedUsersData data = new InvolvedUsersData(stepManagerRef, stepUserRef, memberships);

        assertThat(data.stepManagerRef()).isEqualTo(stepManagerRef);
        assertThat(data.stepUserRef()).isEqualTo(stepUserRef);
        assertThat(data.memberships()).containsExactlyElementsOf(memberships);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<InvolvedUsersData> validInvolvedUsersData() {
        Arbitrary<String> refs = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<List<String>> membershipLists = Arbitraries.strings()
                .alpha()
                .ofMinLength(1)
                .ofMaxLength(30)
                .list()
                .ofMinSize(0)
                .ofMaxSize(10);

        return Combinators.combine(refs, refs, membershipLists)
                .as(InvolvedUsersData::new);
    }

    @Provide
    Arbitrary<InvolvedUsersData> dataWithNulls() {
        Arbitrary<String> refs = Arbitraries.strings()
                .alpha()
                .ofMaxLength(50)
                .injectNull(0.3);
        Arbitrary<List<String>> membershipLists = Arbitraries.strings()
                .alpha()
                .ofMaxLength(30)
                .list()
                .ofMinSize(0)
                .ofMaxSize(5);

        return Combinators.combine(refs, refs, membershipLists)
                .as(InvolvedUsersData::new);
    }
}
