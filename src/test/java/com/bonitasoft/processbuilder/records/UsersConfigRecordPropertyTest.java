package com.bonitasoft.processbuilder.records;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link UsersConfigRecord}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("UsersConfigRecord Property-Based Tests")
class UsersConfigRecordPropertyTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // =========================================================================
    // Class Structure Properties
    // =========================================================================

    @Example
    @Label("Class should be a record")
    void classShouldBeRecord() {
        assertThat(UsersConfigRecord.class.isRecord()).isTrue();
    }

    @Example
    @Label("Class should be public")
    void classShouldBePublic() {
        assertThat(Modifier.isPublic(UsersConfigRecord.class.getModifiers())).isTrue();
    }

    @Example
    @Label("Class should have four record components")
    void classShouldHaveFourComponents() {
        assertThat(UsersConfigRecord.class.getRecordComponents()).hasSize(4);
    }

    // =========================================================================
    // Empty Factory Method Properties
    // =========================================================================

    @Example
    @Label("empty() should always return consistent empty record")
    void emptyShouldReturnConsistentEmptyRecord() {
        for (int i = 0; i < 10; i++) {
            UsersConfigRecord record = UsersConfigRecord.empty();

            assertThat(record.stepUser()).isNull();
            assertThat(record.stepManager()).isNull();
            assertThat(record.memberShips()).isEmpty();
            assertThat(record.membersShipsInput()).isNull();
            assertThat(record.hasAnySource()).isFalse();
        }
    }

    // =========================================================================
    // Immutability Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("memberShips should always be immutable")
    void memberShipsShouldAlwaysBeImmutable(
            @ForAll @Size(min = 0, max = 5) List<@AlphaChars @StringLength(min = 1, max = 20) String> memberships) {

        UsersConfigRecord record = new UsersConfigRecord(null, null, memberships, null);

        assertThatThrownBy(() -> record.memberShips().add("new_item"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("memberShips should be defensive copy")
    void memberShipsShouldBeDefensiveCopy(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String membership) {

        java.util.ArrayList<String> originalList = new java.util.ArrayList<>();
        originalList.add(membership);

        UsersConfigRecord record = new UsersConfigRecord(null, null, originalList, null);

        // Modify original list
        originalList.add("another");

        // Record should not be affected
        assertThat(record.memberShips()).containsExactly(membership);
    }

    // =========================================================================
    // hasStepUser Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("hasStepUser should return true for non-blank stepUser")
    void hasStepUserShouldReturnTrueForNonBlank(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String stepUser) {

        UsersConfigRecord record = new UsersConfigRecord(stepUser, null, null, null);

        assertThat(record.hasStepUser()).isTrue();
    }

    @Property(tries = 50)
    @Label("hasStepUser should return false for blank stepUser")
    void hasStepUserShouldReturnFalseForBlank(
            @ForAll("blankStrings") String blankStepUser) {

        UsersConfigRecord record = new UsersConfigRecord(blankStepUser, null, null, null);

        assertThat(record.hasStepUser()).isFalse();
    }

    // =========================================================================
    // hasStepManager Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("hasStepManager should return true for non-blank stepManager")
    void hasStepManagerShouldReturnTrueForNonBlank(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String stepManager) {

        UsersConfigRecord record = new UsersConfigRecord(null, stepManager, null, null);

        assertThat(record.hasStepManager()).isTrue();
    }

    // =========================================================================
    // hasMemberShips Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("hasMemberShips should return true for non-empty list")
    void hasMemberShipsShouldReturnTrueForNonEmptyList(
            @ForAll @Size(min = 1, max = 5) List<@AlphaChars @StringLength(min = 1, max = 20) String> memberships) {

        UsersConfigRecord record = new UsersConfigRecord(null, null, memberships, null);

        assertThat(record.hasMemberShips()).isTrue();
    }

    @Example
    @Label("hasMemberShips should return false for empty list")
    void hasMemberShipsShouldReturnFalseForEmptyList() {
        UsersConfigRecord record = new UsersConfigRecord(null, null, Collections.emptyList(), null);

        assertThat(record.hasMemberShips()).isFalse();
    }

    @Example
    @Label("hasMemberShips should return false for null list")
    void hasMemberShipsShouldReturnFalseForNullList() {
        UsersConfigRecord record = new UsersConfigRecord(null, null, null, null);

        assertThat(record.hasMemberShips()).isFalse();
    }

    // =========================================================================
    // hasMembersShipsInput Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("hasMembersShipsInput should return true for non-blank input")
    void hasMembersShipsInputShouldReturnTrueForNonBlank(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String input) {

        UsersConfigRecord record = new UsersConfigRecord(null, null, null, input);

        assertThat(record.hasMembersShipsInput()).isTrue();
    }

    // =========================================================================
    // hasAnySource Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("hasAnySource should be true if stepUser is defined")
    void hasAnySourceShouldBeTrueIfStepUserDefined(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String stepUser) {

        UsersConfigRecord record = new UsersConfigRecord(stepUser, null, null, null);

        assertThat(record.hasAnySource()).isTrue();
    }

    @Property(tries = 100)
    @Label("hasAnySource should be true if stepManager is defined")
    void hasAnySourceShouldBeTrueIfStepManagerDefined(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String stepManager) {

        UsersConfigRecord record = new UsersConfigRecord(null, stepManager, null, null);

        assertThat(record.hasAnySource()).isTrue();
    }

    @Property(tries = 100)
    @Label("hasAnySource should be true if memberShips is not empty")
    void hasAnySourceShouldBeTrueIfMemberShipsNotEmpty(
            @ForAll @Size(min = 1, max = 5) List<@AlphaChars @StringLength(min = 1, max = 20) String> memberships) {

        UsersConfigRecord record = new UsersConfigRecord(null, null, memberships, null);

        assertThat(record.hasAnySource()).isTrue();
    }

    @Property(tries = 100)
    @Label("hasAnySource should be true if membersShipsInput is defined")
    void hasAnySourceShouldBeTrueIfMembersShipsInputDefined(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String input) {

        UsersConfigRecord record = new UsersConfigRecord(null, null, null, input);

        assertThat(record.hasAnySource()).isTrue();
    }

    // =========================================================================
    // Optional Getter Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("getStepUserOptional should return present Optional for valid stepUser")
    void getStepUserOptionalShouldReturnPresentForValid(
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String stepUser) {

        UsersConfigRecord record = new UsersConfigRecord(stepUser, null, null, null);
        Optional<String> result = record.getStepUserOptional();

        assertThat(result).isPresent().contains(stepUser);
    }

    @Example
    @Label("getStepUserOptional should return empty Optional for null stepUser")
    void getStepUserOptionalShouldReturnEmptyForNull() {
        UsersConfigRecord record = new UsersConfigRecord(null, null, null, null);
        Optional<String> result = record.getStepUserOptional();

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // parseMembersShipsInput Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("parseMembersShipsInput should return valid StepFieldRef for valid format")
    void parseMembersShipsInputShouldReturnValidStepFieldRef(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String step,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String field) {

        String membersShipsInput = step + ":" + field;
        UsersConfigRecord record = new UsersConfigRecord(null, null, null, membersShipsInput);

        Optional<StepFieldRef> result = record.parseMembersShipsInput();

        assertThat(result).isPresent();
        assertThat(result.get().stepRef()).isEqualTo(step);
        assertThat(result.get().fieldRef()).isEqualTo(field);
    }

    @Example
    @Label("parseMembersShipsInput should return empty for null membersShipsInput")
    void parseMembersShipsInputShouldReturnEmptyForNull() {
        UsersConfigRecord record = UsersConfigRecord.empty();
        Optional<StepFieldRef> result = record.parseMembersShipsInput();

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // fromUsersNode Properties
    // =========================================================================

    @Example
    @Label("fromUsersNode should return empty record for null node")
    void fromUsersNodeShouldReturnEmptyForNull() {
        UsersConfigRecord record = UsersConfigRecord.fromUsersNode(null, null);

        assertThat(record).isEqualTo(UsersConfigRecord.empty());
    }

    @Property(tries = 50)
    @Label("fromUsersNode should parse stepUser correctly")
    void fromUsersNodeShouldParseStepUserCorrectly(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepUser) throws Exception {

        String json = "{\"stepUser\": \"" + stepUser + "\"}";
        JsonNode usersNode = OBJECT_MAPPER.readTree(json);

        UsersConfigRecord record = UsersConfigRecord.fromUsersNode(usersNode, null);

        assertThat(record.stepUser()).isEqualTo(stepUser);
    }

    // =========================================================================
    // Equality Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("Records with same values should be equal")
    void recordsWithSameValuesShouldBeEqual(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepUser,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepManager,
            @ForAll @Size(min = 0, max = 3) List<@AlphaChars @StringLength(min = 1, max = 10) String> memberships,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String input) {

        UsersConfigRecord record1 = new UsersConfigRecord(stepUser, stepManager, memberships, input);
        UsersConfigRecord record2 = new UsersConfigRecord(stepUser, stepManager, memberships, input);

        assertThat(record1).isEqualTo(record2);
        assertThat(record1.hashCode()).isEqualTo(record2.hashCode());
    }

    @Property(tries = 100)
    @Label("Records with different stepUser should not be equal")
    void recordsWithDifferentStepUserShouldNotBeEqual(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepUser1,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepUser2) {

        Assume.that(!stepUser1.equals(stepUser2));

        UsersConfigRecord record1 = new UsersConfigRecord(stepUser1, null, null, null);
        UsersConfigRecord record2 = new UsersConfigRecord(stepUser2, null, null, null);

        assertThat(record1).isNotEqualTo(record2);
    }

    // =========================================================================
    // Constants Properties
    // =========================================================================

    @Example
    @Label("Constants should be deterministic")
    void constantsShouldBeDeterministic() {
        for (int i = 0; i < 10; i++) {
            assertThat(UsersConfigRecord.USERS_KEY).isEqualTo("users");
            assertThat(UsersConfigRecord.STEP_USER_KEY).isEqualTo("stepUser");
            assertThat(UsersConfigRecord.STEP_MANAGER_KEY).isEqualTo("stepManager");
            assertThat(UsersConfigRecord.MEMBERSHIPS_KEY).isEqualTo("memberShips");
            assertThat(UsersConfigRecord.MEMBERSHIPS_INPUT_KEY).isEqualTo("membersShipsInput");
        }
    }

    // =========================================================================
    // Providers
    // =========================================================================

    @Provide
    Arbitrary<String> blankStrings() {
        return Arbitraries.of(null, "", "   ", "\t", "\n");
    }
}
