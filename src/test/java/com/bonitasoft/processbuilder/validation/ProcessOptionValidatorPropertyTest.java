package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.enums.ActionType;
import com.bonitasoft.processbuilder.enums.ObjectsManagementOptionType;
import com.bonitasoft.processbuilder.enums.ProcessOptionType;
import net.jqwik.api.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ProcessOptionValidator} utility class.
 * Tests invariants for action and option type matching.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ProcessOptionValidator Property-Based Tests")
class ProcessOptionValidatorPropertyTest {

    // =========================================================================
    // Null Input Handling Properties (ObjectsManagementOptionType overload)
    // =========================================================================

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for null actionType (Objects)")
    void isMatchingActionAndOption_objects_shouldReturnFalseForNullActionType(
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType optionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                null, optionType.name(), ActionType.INSERT, optionType);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for null optionType string (Objects)")
    void isMatchingActionAndOption_objects_shouldReturnFalseForNullOptionTypeString(
            @ForAll("actionTypes") ActionType actionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), null, actionType, ObjectsManagementOptionType.CATEGORY);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for null expected ActionType (Objects)")
    void isMatchingActionAndOption_objects_shouldReturnFalseForNullExpectedActionType(
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType optionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                "INSERT", optionType.name(), null, optionType);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for null expected OptionType (Objects)")
    void isMatchingActionAndOption_objects_shouldReturnFalseForNullExpectedOptionType(
            @ForAll("actionTypes") ActionType actionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), "CATEGORY", actionType, (ObjectsManagementOptionType) null);

        assertThat(result).isFalse();
    }

    // =========================================================================
    // Null Input Handling Properties (ProcessOptionType overload)
    // =========================================================================

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for null actionType (Process)")
    void isMatchingActionAndOption_process_shouldReturnFalseForNullActionType(
            @ForAll("processOptionTypes") ProcessOptionType optionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                null, optionType.name(), ActionType.INSERT, optionType);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for null optionType string (Process)")
    void isMatchingActionAndOption_process_shouldReturnFalseForNullOptionTypeString(
            @ForAll("actionTypes") ActionType actionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), null, actionType, ProcessOptionType.STEPS);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for null expected ActionType (Process)")
    void isMatchingActionAndOption_process_shouldReturnFalseForNullExpectedActionType(
            @ForAll("processOptionTypes") ProcessOptionType optionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                "INSERT", optionType.name(), null, optionType);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for null expected ProcessOptionType")
    void isMatchingActionAndOption_process_shouldReturnFalseForNullExpectedProcessOptionType(
            @ForAll("actionTypes") ActionType actionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), "STEPS", actionType, (ProcessOptionType) null);

        assertThat(result).isFalse();
    }

    // =========================================================================
    // Empty String Handling Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for empty actionType (Objects)")
    void isMatchingActionAndOption_objects_shouldReturnFalseForEmptyActionType(
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType optionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                "", optionType.name(), ActionType.INSERT, optionType);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for empty optionType (Objects)")
    void isMatchingActionAndOption_objects_shouldReturnFalseForEmptyOptionType(
            @ForAll("actionTypes") ActionType actionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), "", actionType, ObjectsManagementOptionType.CATEGORY);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for blank actionType (Objects)")
    void isMatchingActionAndOption_objects_shouldReturnFalseForBlankActionType(
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType optionType,
            @ForAll("blankStrings") String blankString) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                blankString, optionType.name(), ActionType.INSERT, optionType);

        assertThat(result).isFalse();
    }

    // =========================================================================
    // Matching Properties (ObjectsManagementOptionType)
    // =========================================================================

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return true for exact match (Objects)")
    void isMatchingActionAndOption_objects_shouldReturnTrueForExactMatch(
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType optionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), optionType.name(), actionType, optionType);

        assertThat(result).isTrue();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should be case-insensitive for actionType (Objects)")
    void isMatchingActionAndOption_objects_shouldBeCaseInsensitiveForActionType(
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType optionType) {

        // Test lowercase
        boolean resultLower = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name().toLowerCase(), optionType.name(), actionType, optionType);

        // Test uppercase
        boolean resultUpper = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name().toUpperCase(), optionType.name(), actionType, optionType);

        assertThat(resultLower).isTrue();
        assertThat(resultUpper).isTrue();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should be case-insensitive for optionType (Objects)")
    void isMatchingActionAndOption_objects_shouldBeCaseInsensitiveForOptionType(
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType optionType) {

        // Test lowercase
        boolean resultLower = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), optionType.name().toLowerCase(), actionType, optionType);

        // Test uppercase
        boolean resultUpper = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), optionType.name().toUpperCase(), actionType, optionType);

        assertThat(resultLower).isTrue();
        assertThat(resultUpper).isTrue();
    }

    // =========================================================================
    // Matching Properties (ProcessOptionType)
    // =========================================================================

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return true for exact match (Process)")
    void isMatchingActionAndOption_process_shouldReturnTrueForExactMatch(
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("processOptionTypes") ProcessOptionType optionType) {

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), optionType.name(), actionType, optionType);

        assertThat(result).isTrue();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should be case-insensitive for actionType (Process)")
    void isMatchingActionAndOption_process_shouldBeCaseInsensitiveForActionType(
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("processOptionTypes") ProcessOptionType optionType) {

        // Test lowercase
        boolean resultLower = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name().toLowerCase(), optionType.name(), actionType, optionType);

        assertThat(resultLower).isTrue();
    }

    // =========================================================================
    // Non-Matching Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for mismatched actionType (Objects)")
    void isMatchingActionAndOption_objects_shouldReturnFalseForMismatchedActionType(
            @ForAll("actionTypes") ActionType actualAction,
            @ForAll("actionTypes") ActionType expectedAction,
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType optionType) {

        Assume.that(!actualAction.equals(expectedAction));

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                actualAction.name(), optionType.name(), expectedAction, optionType);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isMatchingActionAndOption should return false for mismatched optionType (Objects)")
    void isMatchingActionAndOption_objects_shouldReturnFalseForMismatchedOptionType(
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType actualOption,
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType expectedOption) {

        Assume.that(!actualOption.equals(expectedOption));

        boolean result = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), actualOption.name(), actionType, expectedOption);

        assertThat(result).isFalse();
    }

    // =========================================================================
    // Class Structure Properties
    // =========================================================================

    @Example
    @Label("Class should be final to prevent inheritance")
    void class_shouldBeFinal() {
        assertThat(Modifier.isFinal(ProcessOptionValidator.class.getModifiers())).isTrue();
    }

    @Example
    @Label("Constructor should be private")
    void constructor_shouldBePrivate() throws Exception {
        Constructor<ProcessOptionValidator> constructor =
                ProcessOptionValidator.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }

    @Example
    @Label("Constructor should throw UnsupportedOperationException when invoked")
    void constructor_shouldThrowException() throws Exception {
        Constructor<ProcessOptionValidator> constructor =
                ProcessOptionValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // Consistency Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("isMatchingActionAndOption should be deterministic (Objects)")
    void isMatchingActionAndOption_objects_shouldBeDeterministic(
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("objectsManagementOptionTypes") ObjectsManagementOptionType optionType) {

        boolean result1 = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), optionType.name(), actionType, optionType);
        boolean result2 = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), optionType.name(), actionType, optionType);

        assertThat(result1).isEqualTo(result2);
    }

    @Property(tries = 50)
    @Label("isMatchingActionAndOption should be deterministic (Process)")
    void isMatchingActionAndOption_process_shouldBeDeterministic(
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("processOptionTypes") ProcessOptionType optionType) {

        boolean result1 = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), optionType.name(), actionType, optionType);
        boolean result2 = ProcessOptionValidator.isMatchingActionAndOption(
                actionType.name(), optionType.name(), actionType, optionType);

        assertThat(result1).isEqualTo(result2);
    }

    // =========================================================================
    // Providers
    // =========================================================================

    @Provide
    Arbitrary<ActionType> actionTypes() {
        return Arbitraries.of(ActionType.values());
    }

    @Provide
    Arbitrary<ObjectsManagementOptionType> objectsManagementOptionTypes() {
        return Arbitraries.of(ObjectsManagementOptionType.values());
    }

    @Provide
    Arbitrary<ProcessOptionType> processOptionTypes() {
        return Arbitraries.of(ProcessOptionType.values());
    }

    @Provide
    Arbitrary<String> blankStrings() {
        return Arbitraries.of("   ", "\t", "\n", "  \t\n  ");
    }
}
