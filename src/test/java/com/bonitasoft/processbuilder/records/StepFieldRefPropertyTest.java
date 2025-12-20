package com.bonitasoft.processbuilder.records;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link StepFieldRef} record.
 * Tests invariants that must hold for any valid instance.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("StepFieldRef Property-Based Tests")
class StepFieldRefPropertyTest {

    // =========================================================================
    // EQUALITY PROPERTIES
    // =========================================================================

    @Property(tries = 1000)
    @Label("Equality should be reflexive: x.equals(x)")
    void equalityShouldBeReflexive(@ForAll @From("validStepFieldRefs") StepFieldRef record) {
        assertThat(record).isEqualTo(record);
    }

    @Property(tries = 1000)
    @Label("Equality should be symmetric: x.equals(y) == y.equals(x)")
    void equalityShouldBeSymmetric(
            @ForAll @From("validStepFieldRefs") StepFieldRef record1,
            @ForAll @From("validStepFieldRefs") StepFieldRef record2) {
        assertThat(record1.equals(record2)).isEqualTo(record2.equals(record1));
    }

    @Property(tries = 500)
    @Label("Equality with same values should be true")
    void equalityWithSameValuesShouldBeTrue(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepRef,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fieldRef) {

        StepFieldRef record1 = new StepFieldRef(stepRef, fieldRef);
        StepFieldRef record2 = new StepFieldRef(stepRef, fieldRef);

        assertThat(record1).isEqualTo(record2);
    }

    @Property(tries = 500)
    @Label("equals(null) should return false")
    void equalsNullShouldReturnFalse(@ForAll @From("validStepFieldRefs") StepFieldRef record) {
        assertThat(record.equals(null)).isFalse();
    }

    // =========================================================================
    // HASHCODE PROPERTIES
    // =========================================================================

    @Property(tries = 1000)
    @Label("HashCode should be consistent with equals")
    void hashCodeShouldBeConsistentWithEquals(
            @ForAll @From("validStepFieldRefs") StepFieldRef record1,
            @ForAll @From("validStepFieldRefs") StepFieldRef record2) {
        if (record1.equals(record2)) {
            assertThat(record1.hashCode()).isEqualTo(record2.hashCode());
        }
    }

    @Property(tries = 500)
    @Label("HashCode should be stable across multiple calls")
    void hashCodeShouldBeStable(@ForAll @From("validStepFieldRefs") StepFieldRef record) {
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
    void toStringShouldNeverThrow(@ForAll @From("stepFieldRefsWithNulls") StepFieldRef record) {
        assertThatCode(() -> record.toString()).doesNotThrowAnyException();
    }

    @Property(tries = 500)
    @Label("toString should never return null")
    void toStringShouldNeverReturnNull(@ForAll @From("validStepFieldRefs") StepFieldRef record) {
        assertThat(record.toString()).isNotNull();
    }

    @Property(tries = 500)
    @Label("toString should contain class name")
    void toStringShouldContainClassName(@ForAll @From("validStepFieldRefs") StepFieldRef record) {
        assertThat(record.toString()).contains("StepFieldRef");
    }

    // =========================================================================
    // ACCESSOR PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("Accessors should return constructor values")
    void accessorsShouldReturnConstructorValues(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepRef,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fieldRef) {

        StepFieldRef record = new StepFieldRef(stepRef, fieldRef);

        assertThat(record.stepRef()).isEqualTo(stepRef);
        assertThat(record.fieldRef()).isEqualTo(fieldRef);
    }

    // =========================================================================
    // PARSE METHOD PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("parse should return valid record for valid format")
    void parseShouldReturnValidRecordForValidFormat(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepRef,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fieldRef) {

        String input = stepRef + ":" + fieldRef;
        StepFieldRef result = StepFieldRef.parse(input);

        assertThat(result).isNotNull();
        assertThat(result.stepRef()).isEqualTo(stepRef);
        assertThat(result.fieldRef()).isEqualTo(fieldRef);
    }

    @Property(tries = 500)
    @Label("parse should handle whitespace around values")
    void parseShouldHandleWhitespace(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String stepRef,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fieldRef) {

        String input = "  " + stepRef + "  :  " + fieldRef + "  ";
        StepFieldRef result = StepFieldRef.parse(input);

        assertThat(result).isNotNull();
        assertThat(result.stepRef()).isEqualTo(stepRef);
        assertThat(result.fieldRef()).isEqualTo(fieldRef);
    }

    @Property(tries = 100)
    @Label("parse should return null for input without colon")
    void parseShouldReturnNullWithoutColon(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String input) {
        // Ensure no colon in input
        String noColonInput = input.replace(":", "");

        StepFieldRef result = StepFieldRef.parse(noColonInput);

        assertThat(result).isNull();
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<StepFieldRef> validStepFieldRefs() {
        Arbitrary<String> stepRefs = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<String> fieldRefs = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);

        return Combinators.combine(stepRefs, fieldRefs)
            .as(StepFieldRef::new);
    }

    @Provide
    Arbitrary<StepFieldRef> stepFieldRefsWithNulls() {
        Arbitrary<String> stepRefs = Arbitraries.strings().alpha().ofMaxLength(50).injectNull(0.3);
        Arbitrary<String> fieldRefs = Arbitraries.strings().alpha().ofMaxLength(50).injectNull(0.3);

        return Combinators.combine(stepRefs, fieldRefs)
            .as(StepFieldRef::new);
    }
}
