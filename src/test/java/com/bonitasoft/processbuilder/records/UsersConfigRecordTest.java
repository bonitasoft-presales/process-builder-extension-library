package com.bonitasoft.processbuilder.records;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link UsersConfigRecord}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("UsersConfigRecord Tests")
class UsersConfigRecordTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersConfigRecordTest.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create record with all values")
        void shouldCreateRecordWithAllValues() {
            List<String> memberships = List.of("membership_1", "membership_2");
            UsersConfigRecord record = new UsersConfigRecord(
                    "step_user", "step_manager", memberships, "step:field");

            assertThat(record.stepUser()).isEqualTo("step_user");
            assertThat(record.stepManager()).isEqualTo("step_manager");
            assertThat(record.memberShips()).containsExactly("membership_1", "membership_2");
            assertThat(record.membersShipsInput()).isEqualTo("step:field");
        }

        @Test
        @DisplayName("Should create record with null values")
        void shouldCreateRecordWithNullValues() {
            UsersConfigRecord record = new UsersConfigRecord(null, null, null, null);

            assertThat(record.stepUser()).isNull();
            assertThat(record.stepManager()).isNull();
            assertThat(record.memberShips()).isEmpty();
            assertThat(record.membersShipsInput()).isNull();
        }

        @Test
        @DisplayName("Should make memberShips immutable")
        void shouldMakeMemberShipsImmutable() {
            List<String> originalList = new java.util.ArrayList<>();
            originalList.add("membership_1");
            UsersConfigRecord record = new UsersConfigRecord(null, null, originalList, null);

            // Modify original list
            originalList.add("membership_2");

            // Record should not be affected
            assertThat(record.memberShips()).containsExactly("membership_1");
            assertThatThrownBy(() -> record.memberShips().add("new_membership"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("Empty Factory Method Tests")
    class EmptyFactoryMethodTests {

        @Test
        @DisplayName("empty() should return record with all null/empty values")
        void emptyShouldReturnEmptyRecord() {
            UsersConfigRecord record = UsersConfigRecord.empty();

            assertThat(record.stepUser()).isNull();
            assertThat(record.stepManager()).isNull();
            assertThat(record.memberShips()).isEmpty();
            assertThat(record.membersShipsInput()).isNull();
        }
    }

    @Nested
    @DisplayName("fromUsersNode Tests")
    class FromUsersNodeTests {

        @Test
        @DisplayName("Should parse complete users node")
        void shouldParseCompleteUsersNode() throws Exception {
            String json = """
                    {
                        "stepUser": "step_xxx",
                        "stepManager": "step_yyy",
                        "memberShips": ["membership_1", "membership_2"],
                        "membersShipsInput": "step_zzz:field_www"
                    }
                    """;
            JsonNode usersNode = OBJECT_MAPPER.readTree(json);

            UsersConfigRecord record = UsersConfigRecord.fromUsersNode(usersNode, LOGGER);

            assertThat(record.stepUser()).isEqualTo("step_xxx");
            assertThat(record.stepManager()).isEqualTo("step_yyy");
            assertThat(record.memberShips()).containsExactly("membership_1", "membership_2");
            assertThat(record.membersShipsInput()).isEqualTo("step_zzz:field_www");
        }

        @Test
        @DisplayName("Should handle partial users node")
        void shouldHandlePartialUsersNode() throws Exception {
            String json = """
                    {
                        "stepUser": "step_xxx"
                    }
                    """;
            JsonNode usersNode = OBJECT_MAPPER.readTree(json);

            UsersConfigRecord record = UsersConfigRecord.fromUsersNode(usersNode, LOGGER);

            assertThat(record.stepUser()).isEqualTo("step_xxx");
            assertThat(record.stepManager()).isNull();
            assertThat(record.memberShips()).isEmpty();
            assertThat(record.membersShipsInput()).isNull();
        }

        @Test
        @DisplayName("Should handle null users node")
        void shouldHandleNullUsersNode() {
            UsersConfigRecord record = UsersConfigRecord.fromUsersNode(null, LOGGER);

            assertThat(record).isEqualTo(UsersConfigRecord.empty());
        }

        @Test
        @DisplayName("Should handle empty string values")
        void shouldHandleEmptyStringValues() throws Exception {
            String json = """
                    {
                        "stepUser": "",
                        "stepManager": "   ",
                        "memberShips": [],
                        "membersShipsInput": ""
                    }
                    """;
            JsonNode usersNode = OBJECT_MAPPER.readTree(json);

            UsersConfigRecord record = UsersConfigRecord.fromUsersNode(usersNode, LOGGER);

            assertThat(record.stepUser()).isNull();
            assertThat(record.stepManager()).isNull();
            assertThat(record.memberShips()).isEmpty();
            assertThat(record.membersShipsInput()).isNull();
        }

        @Test
        @DisplayName("Should trim whitespace from values")
        void shouldTrimWhitespaceFromValues() throws Exception {
            String json = """
                    {
                        "stepUser": "  step_xxx  ",
                        "memberShips": ["  membership_1  ", "membership_2"]
                    }
                    """;
            JsonNode usersNode = OBJECT_MAPPER.readTree(json);

            UsersConfigRecord record = UsersConfigRecord.fromUsersNode(usersNode, LOGGER);

            assertThat(record.stepUser()).isEqualTo("step_xxx");
            assertThat(record.memberShips()).containsExactly("membership_1", "membership_2");
        }

        @Test
        @DisplayName("Should handle null logger")
        void shouldHandleNullLogger() throws Exception {
            String json = """
                    {
                        "stepUser": "step_xxx"
                    }
                    """;
            JsonNode usersNode = OBJECT_MAPPER.readTree(json);

            UsersConfigRecord record = UsersConfigRecord.fromUsersNode(usersNode, null);

            assertThat(record.stepUser()).isEqualTo("step_xxx");
        }

        @Test
        @DisplayName("Should filter blank strings from memberShips array")
        void shouldFilterBlankStringsFromMemberShipsArray() throws Exception {
            String json = """
                    {
                        "memberShips": ["membership_1", "", "  ", "membership_2"]
                    }
                    """;
            JsonNode usersNode = OBJECT_MAPPER.readTree(json);

            UsersConfigRecord record = UsersConfigRecord.fromUsersNode(usersNode, LOGGER);

            assertThat(record.memberShips()).containsExactly("membership_1", "membership_2");
        }
    }

    @Nested
    @DisplayName("Has Methods Tests")
    class HasMethodsTests {

        @Test
        @DisplayName("hasStepUser should return true when stepUser is defined")
        void hasStepUserShouldReturnTrueWhenDefined() {
            UsersConfigRecord record = new UsersConfigRecord("step_xxx", null, null, null);
            assertThat(record.hasStepUser()).isTrue();
        }

        @Test
        @DisplayName("hasStepUser should return false when stepUser is null")
        void hasStepUserShouldReturnFalseWhenNull() {
            UsersConfigRecord record = new UsersConfigRecord(null, null, null, null);
            assertThat(record.hasStepUser()).isFalse();
        }

        @Test
        @DisplayName("hasStepUser should return false when stepUser is blank")
        void hasStepUserShouldReturnFalseWhenBlank() {
            UsersConfigRecord record = new UsersConfigRecord("   ", null, null, null);
            assertThat(record.hasStepUser()).isFalse();
        }

        @Test
        @DisplayName("hasStepManager should return true when stepManager is defined")
        void hasStepManagerShouldReturnTrueWhenDefined() {
            UsersConfigRecord record = new UsersConfigRecord(null, "step_yyy", null, null);
            assertThat(record.hasStepManager()).isTrue();
        }

        @Test
        @DisplayName("hasMemberShips should return true when memberShips is not empty")
        void hasMemberShipsShouldReturnTrueWhenNotEmpty() {
            UsersConfigRecord record = new UsersConfigRecord(null, null, List.of("m1"), null);
            assertThat(record.hasMemberShips()).isTrue();
        }

        @Test
        @DisplayName("hasMemberShips should return false when memberShips is empty")
        void hasMemberShipsShouldReturnFalseWhenEmpty() {
            UsersConfigRecord record = new UsersConfigRecord(null, null, Collections.emptyList(), null);
            assertThat(record.hasMemberShips()).isFalse();
        }

        @Test
        @DisplayName("hasMembersShipsInput should return true when defined")
        void hasMembersShipsInputShouldReturnTrueWhenDefined() {
            UsersConfigRecord record = new UsersConfigRecord(null, null, null, "step:field");
            assertThat(record.hasMembersShipsInput()).isTrue();
        }

        @Test
        @DisplayName("hasAnySource should return true if any source is defined")
        void hasAnySourceShouldReturnTrueIfAnyDefined() {
            assertThat(new UsersConfigRecord("step", null, null, null).hasAnySource()).isTrue();
            assertThat(new UsersConfigRecord(null, "step", null, null).hasAnySource()).isTrue();
            assertThat(new UsersConfigRecord(null, null, List.of("m"), null).hasAnySource()).isTrue();
            assertThat(new UsersConfigRecord(null, null, null, "s:f").hasAnySource()).isTrue();
        }

        @Test
        @DisplayName("hasAnySource should return false if no source is defined")
        void hasAnySourceShouldReturnFalseIfNoneDefined() {
            UsersConfigRecord record = UsersConfigRecord.empty();
            assertThat(record.hasAnySource()).isFalse();
        }
    }

    @Nested
    @DisplayName("Optional Getter Tests")
    class OptionalGetterTests {

        @Test
        @DisplayName("getStepUserOptional should return Optional with value when defined")
        void getStepUserOptionalShouldReturnValueWhenDefined() {
            UsersConfigRecord record = new UsersConfigRecord("step_xxx", null, null, null);
            Optional<String> result = record.getStepUserOptional();

            assertThat(result).isPresent().contains("step_xxx");
        }

        @Test
        @DisplayName("getStepUserOptional should return empty Optional when not defined")
        void getStepUserOptionalShouldReturnEmptyWhenNotDefined() {
            UsersConfigRecord record = UsersConfigRecord.empty();
            Optional<String> result = record.getStepUserOptional();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("getStepManagerOptional should return Optional with value when defined")
        void getStepManagerOptionalShouldReturnValueWhenDefined() {
            UsersConfigRecord record = new UsersConfigRecord(null, "step_yyy", null, null);
            Optional<String> result = record.getStepManagerOptional();

            assertThat(result).isPresent().contains("step_yyy");
        }

        @Test
        @DisplayName("getMembersShipsInputOptional should return Optional with value when defined")
        void getMembersShipsInputOptionalShouldReturnValueWhenDefined() {
            UsersConfigRecord record = new UsersConfigRecord(null, null, null, "step:field");
            Optional<String> result = record.getMembersShipsInputOptional();

            assertThat(result).isPresent().contains("step:field");
        }
    }

    @Nested
    @DisplayName("parseMembersShipsInput Tests")
    class ParseMembersShipsInputTests {

        @Test
        @DisplayName("Should return parsed StepFieldRef when valid format")
        void shouldReturnParsedStepFieldRefWhenValidFormat() {
            UsersConfigRecord record = new UsersConfigRecord(null, null, null, "step_xxx:field_yyy");
            Optional<StepFieldRef> result = record.parseMembersShipsInput();

            assertThat(result).isPresent();
            assertThat(result.get().stepRef()).isEqualTo("step_xxx");
            assertThat(result.get().fieldRef()).isEqualTo("field_yyy");
        }

        @Test
        @DisplayName("Should return empty when membersShipsInput is not defined")
        void shouldReturnEmptyWhenNotDefined() {
            UsersConfigRecord record = UsersConfigRecord.empty();
            Optional<StepFieldRef> result = record.parseMembersShipsInput();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when format is invalid")
        void shouldReturnEmptyWhenInvalidFormat() {
            UsersConfigRecord record = new UsersConfigRecord(null, null, null, "invalid_format");
            Optional<StepFieldRef> result = record.parseMembersShipsInput();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Constants Tests")
    class ConstantsTests {

        @Test
        @DisplayName("USERS_KEY should be 'users'")
        void usersKeyShouldBeCorrect() {
            assertThat(UsersConfigRecord.USERS_KEY).isEqualTo("users");
        }

        @Test
        @DisplayName("STEP_USER_KEY should be 'stepUser'")
        void stepUserKeyShouldBeCorrect() {
            assertThat(UsersConfigRecord.STEP_USER_KEY).isEqualTo("stepUser");
        }

        @Test
        @DisplayName("STEP_MANAGER_KEY should be 'stepManager'")
        void stepManagerKeyShouldBeCorrect() {
            assertThat(UsersConfigRecord.STEP_MANAGER_KEY).isEqualTo("stepManager");
        }

        @Test
        @DisplayName("MEMBERSHIPS_KEY should be 'memberShips'")
        void membershipsKeyShouldBeCorrect() {
            assertThat(UsersConfigRecord.MEMBERSHIPS_KEY).isEqualTo("memberShips");
        }

        @Test
        @DisplayName("MEMBERSHIPS_INPUT_KEY should be 'membersShipsInput'")
        void membershipsInputKeyShouldBeCorrect() {
            assertThat(UsersConfigRecord.MEMBERSHIPS_INPUT_KEY).isEqualTo("membersShipsInput");
        }
    }

    @Nested
    @DisplayName("Record Equality Tests")
    class RecordEqualityTests {

        @Test
        @DisplayName("Records with same values should be equal")
        void recordsWithSameValuesShouldBeEqual() {
            UsersConfigRecord record1 = new UsersConfigRecord("step", "manager", List.of("m1"), "s:f");
            UsersConfigRecord record2 = new UsersConfigRecord("step", "manager", List.of("m1"), "s:f");

            assertThat(record1).isEqualTo(record2);
            assertThat(record1.hashCode()).isEqualTo(record2.hashCode());
        }

        @Test
        @DisplayName("Records with different values should not be equal")
        void recordsWithDifferentValuesShouldNotBeEqual() {
            UsersConfigRecord record1 = new UsersConfigRecord("step1", null, null, null);
            UsersConfigRecord record2 = new UsersConfigRecord("step2", null, null, null);

            assertThat(record1).isNotEqualTo(record2);
        }

        @Test
        @DisplayName("toString should contain all field values")
        void toStringShouldContainAllFields() {
            UsersConfigRecord record = new UsersConfigRecord("step", "manager", List.of("m1"), "s:f");
            String result = record.toString();

            assertThat(result)
                    .contains("step")
                    .contains("manager")
                    .contains("m1")
                    .contains("s:f");
        }
    }
}
