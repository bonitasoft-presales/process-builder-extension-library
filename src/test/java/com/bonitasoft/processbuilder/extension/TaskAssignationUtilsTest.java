package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.records.UsersConfigRecord;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.search.SearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TaskAssignationUtils}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskAssignationUtils Tests")
class TaskAssignationUtilsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskAssignationUtilsTest.class);

    @Mock
    private IdentityAPI identityAPI;

    @Mock
    private SearchResult<User> searchResult;

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Private constructor should throw UnsupportedOperationException")
        void privateConstructorShouldThrowException() throws Exception {
            Constructor<TaskAssignationUtils> constructor = TaskAssignationUtils.class.getDeclaredConstructor();
            assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

            constructor.setAccessible(true);
            assertThatThrownBy(constructor::newInstance)
                    .isInstanceOf(InvocationTargetException.class)
                    .hasCauseInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Class should be final")
        void classShouldBeFinal() {
            assertThat(Modifier.isFinal(TaskAssignationUtils.class.getModifiers())).isTrue();
        }
    }

    @Nested
    @DisplayName("parseUsersConfig Tests")
    class ParseUsersConfigTests {

        @Test
        @DisplayName("Should parse complete JSON content")
        void shouldParseCompleteJsonContent() {
            String json = """
                    {
                        "users": {
                            "stepUser": "step_xxx",
                            "stepManager": "step_yyy",
                            "memberShips": ["m1", "m2"],
                            "membersShipsInput": "step_zzz:field_www"
                        }
                    }
                    """;

            UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig(json, LOGGER);

            assertThat(result.stepUser()).isEqualTo("step_xxx");
            assertThat(result.stepManager()).isEqualTo("step_yyy");
            assertThat(result.memberShips()).containsExactly("m1", "m2");
            assertThat(result.membersShipsInput()).isEqualTo("step_zzz:field_www");
        }

        @Test
        @DisplayName("Should return empty config for null JSON")
        void shouldReturnEmptyConfigForNullJson() {
            UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig(null, LOGGER);

            assertThat(result).isEqualTo(UsersConfigRecord.empty());
        }

        @Test
        @DisplayName("Should return empty config for blank JSON")
        void shouldReturnEmptyConfigForBlankJson() {
            UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig("   ", LOGGER);

            assertThat(result).isEqualTo(UsersConfigRecord.empty());
        }

        @Test
        @DisplayName("Should return empty config for invalid JSON")
        void shouldReturnEmptyConfigForInvalidJson() {
            UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig("{ invalid json }", LOGGER);

            assertThat(result).isEqualTo(UsersConfigRecord.empty());
        }

        @Test
        @DisplayName("Should return empty config for JSON without users key")
        void shouldReturnEmptyConfigForJsonWithoutUsersKey() {
            String json = """
                    {
                        "other": "value"
                    }
                    """;

            UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig(json, LOGGER);

            assertThat(result).isEqualTo(UsersConfigRecord.empty());
        }

        @Test
        @DisplayName("Should handle null logger")
        void shouldHandleNullLogger() {
            String json = """
                    {
                        "users": {
                            "stepUser": "step_xxx"
                        }
                    }
                    """;

            UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig(json, null);

            assertThat(result.stepUser()).isEqualTo("step_xxx");
        }
    }

    @Nested
    @DisplayName("parseUsersNode Tests")
    class ParseUsersNodeTests {

        @Test
        @DisplayName("Should return Optional with users node")
        void shouldReturnOptionalWithUsersNode() {
            String json = """
                    {
                        "users": {
                            "stepUser": "step_xxx"
                        }
                    }
                    """;

            Optional<com.fasterxml.jackson.databind.JsonNode> result =
                    TaskAssignationUtils.parseUsersNode(json, LOGGER);

            assertThat(result).isPresent();
            assertThat(result.get().get("stepUser").asText()).isEqualTo("step_xxx");
        }

        @Test
        @DisplayName("Should return empty for null JSON")
        void shouldReturnEmptyForNullJson() {
            Optional<com.fasterxml.jackson.databind.JsonNode> result =
                    TaskAssignationUtils.parseUsersNode(null, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for JSON without users key")
        void shouldReturnEmptyForJsonWithoutUsersKey() {
            String json = """
                    {
                        "other": "value"
                    }
                    """;

            Optional<com.fasterxml.jackson.databind.JsonNode> result =
                    TaskAssignationUtils.parseUsersNode(json, LOGGER);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("processStepUser Tests")
    class ProcessStepUserTests {

        @Test
        @DisplayName("Should return user ID when step user is found")
        void shouldReturnUserIdWhenStepUserFound() {
            UsersConfigRecord config = new UsersConfigRecord("step_xxx", null, null, null);
            Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(123L);
            Function<TestStepInstance, Long> userIdExtractor = TestStepInstance::getUserId;

            Optional<Long> result = TaskAssignationUtils.processStepUser(
                    config, stepFinder, userIdExtractor, LOGGER);

            assertThat(result).isPresent().contains(123L);
        }

        @Test
        @DisplayName("Should return empty when config has no stepUser")
        void shouldReturnEmptyWhenNoStepUser() {
            UsersConfigRecord config = UsersConfigRecord.empty();
            Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(123L);
            Function<TestStepInstance, Long> userIdExtractor = TestStepInstance::getUserId;

            Optional<Long> result = TaskAssignationUtils.processStepUser(
                    config, stepFinder, userIdExtractor, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when step instance not found")
        void shouldReturnEmptyWhenStepInstanceNotFound() {
            UsersConfigRecord config = new UsersConfigRecord("step_xxx", null, null, null);
            Function<String, TestStepInstance> stepFinder = ref -> null;
            Function<TestStepInstance, Long> userIdExtractor = TestStepInstance::getUserId;

            Optional<Long> result = TaskAssignationUtils.processStepUser(
                    config, stepFinder, userIdExtractor, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when user ID is null")
        void shouldReturnEmptyWhenUserIdIsNull() {
            UsersConfigRecord config = new UsersConfigRecord("step_xxx", null, null, null);
            Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(null);
            Function<TestStepInstance, Long> userIdExtractor = TestStepInstance::getUserId;

            Optional<Long> result = TaskAssignationUtils.processStepUser(
                    config, stepFinder, userIdExtractor, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when user ID is zero")
        void shouldReturnEmptyWhenUserIdIsZero() {
            UsersConfigRecord config = new UsersConfigRecord("step_xxx", null, null, null);
            Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(0L);
            Function<TestStepInstance, Long> userIdExtractor = TestStepInstance::getUserId;

            Optional<Long> result = TaskAssignationUtils.processStepUser(
                    config, stepFinder, userIdExtractor, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when config is null")
        void shouldReturnEmptyWhenConfigIsNull() {
            Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(123L);
            Function<TestStepInstance, Long> userIdExtractor = TestStepInstance::getUserId;

            Optional<Long> result = TaskAssignationUtils.processStepUser(
                    null, stepFinder, userIdExtractor, LOGGER);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("processStepManager Tests")
    class ProcessStepManagerTests {

        @Test
        @DisplayName("Should return manager ID when found")
        void shouldReturnManagerIdWhenFound() throws Exception {
            UsersConfigRecord config = new UsersConfigRecord(null, "step_yyy", null, null);
            Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(100L);
            Function<TestStepInstance, Long> userIdExtractor = TestStepInstance::getUserId;

            User mockUser = mock(User.class);
            when(mockUser.getManagerUserId()).thenReturn(200L);
            when(identityAPI.getUser(100L)).thenReturn(mockUser);

            Optional<Long> result = TaskAssignationUtils.processStepManager(
                    config, stepFinder, userIdExtractor, identityAPI, LOGGER);

            assertThat(result).isPresent().contains(200L);
        }

        @Test
        @DisplayName("Should return empty when config has no stepManager")
        void shouldReturnEmptyWhenNoStepManager() {
            UsersConfigRecord config = UsersConfigRecord.empty();

            Optional<Long> result = TaskAssignationUtils.processStepManager(
                    config, ref -> new TestStepInstance(100L),
                    TestStepInstance::getUserId, identityAPI, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when step user not found")
        void shouldReturnEmptyWhenStepUserNotFound() {
            UsersConfigRecord config = new UsersConfigRecord(null, "step_yyy", null, null);
            Function<String, TestStepInstance> stepFinder = ref -> null;

            Optional<Long> result = TaskAssignationUtils.processStepManager(
                    config, stepFinder, TestStepInstance::getUserId, identityAPI, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when user has no manager")
        void shouldReturnEmptyWhenUserHasNoManager() throws Exception {
            UsersConfigRecord config = new UsersConfigRecord(null, "step_yyy", null, null);
            Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(100L);

            User mockUser = mock(User.class);
            when(mockUser.getManagerUserId()).thenReturn(0L);
            when(identityAPI.getUser(100L)).thenReturn(mockUser);

            Optional<Long> result = TaskAssignationUtils.processStepManager(
                    config, stepFinder, TestStepInstance::getUserId, identityAPI, LOGGER);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("processMemberships Tests")
    class ProcessMembershipsTests {

        @Test
        @DisplayName("Should return empty set for null membership list")
        void shouldReturnEmptySetForNullMembershipList() {
            Set<Long> result = TaskAssignationUtils.processMemberships(
                    null, refs -> Collections.emptyList(), identityAPI, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty set for empty membership list")
        void shouldReturnEmptySetForEmptyMembershipList() {
            Set<Long> result = TaskAssignationUtils.processMemberships(
                    Collections.emptyList(), refs -> Collections.emptyList(), identityAPI, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty set when finder returns null")
        void shouldReturnEmptySetWhenFinderReturnsNull() {
            Set<Long> result = TaskAssignationUtils.processMemberships(
                    List.of("m1"), refs -> null, identityAPI, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty set when finder returns empty list")
        void shouldReturnEmptySetWhenFinderReturnsEmptyList() {
            Set<Long> result = TaskAssignationUtils.processMemberships(
                    List.of("m1"), refs -> Collections.emptyList(), identityAPI, LOGGER);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("processSingleMembership Tests")
    class ProcessSingleMembershipTests {

        @Test
        @DisplayName("Should return empty set for null membership ref")
        void shouldReturnEmptySetForNullMembershipRef() {
            Set<Long> result = TaskAssignationUtils.processSingleMembership(
                    null, refs -> Collections.emptyList(), identityAPI, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty set for blank membership ref")
        void shouldReturnEmptySetForBlankMembershipRef() {
            Set<Long> result = TaskAssignationUtils.processSingleMembership(
                    "   ", refs -> Collections.emptyList(), identityAPI, LOGGER);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("extractMembershipFromStepInput Tests")
    class ExtractMembershipFromStepInputTests {

        @Test
        @DisplayName("Should extract membership ID from step input")
        void shouldExtractMembershipIdFromStepInput() {
            Function<String, TestStepInstance> stepFinder = ref ->
                    new TestStepInstance(100L, "{\"membershipField\": \"membership_123\"}");
            Function<TestStepInstance, String> jsonInputExtractor = TestStepInstance::getJsonInput;

            Optional<String> result = TaskAssignationUtils.extractMembershipFromStepInput(
                    "step_xxx:membershipField", stepFinder, jsonInputExtractor, LOGGER);

            assertThat(result).isPresent().contains("membership_123");
        }

        @Test
        @DisplayName("Should return empty for null stepFieldRef")
        void shouldReturnEmptyForNullStepFieldRef() {
            Optional<String> result = TaskAssignationUtils.extractMembershipFromStepInput(
                    null, ref -> null, obj -> null, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for blank stepFieldRef")
        void shouldReturnEmptyForBlankStepFieldRef() {
            Optional<String> result = TaskAssignationUtils.extractMembershipFromStepInput(
                    "   ", ref -> null, obj -> null, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for invalid format")
        void shouldReturnEmptyForInvalidFormat() {
            Optional<String> result = TaskAssignationUtils.extractMembershipFromStepInput(
                    "invalid_format_no_colon", ref -> null, obj -> null, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when step instance not found")
        void shouldReturnEmptyWhenStepInstanceNotFound() {
            Function<String, TestStepInstance> stepFinder = ref -> null;

            Optional<String> result = TaskAssignationUtils.extractMembershipFromStepInput(
                    "step_xxx:field_yyy", stepFinder, TestStepInstance::getJsonInput, LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when jsonInput is empty")
        void shouldReturnEmptyWhenJsonInputIsEmpty() {
            Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(100L, "");
            Function<TestStepInstance, String> jsonInputExtractor = TestStepInstance::getJsonInput;

            Optional<String> result = TaskAssignationUtils.extractMembershipFromStepInput(
                    "step_xxx:field_yyy", stepFinder, jsonInputExtractor, LOGGER);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("extractFieldFromJson Tests")
    class ExtractFieldFromJsonTests {

        @Test
        @DisplayName("Should extract field value from JSON")
        void shouldExtractFieldValueFromJson() {
            String json = "{\"fieldName\": \"fieldValue\"}";

            Optional<String> result = TaskAssignationUtils.extractFieldFromJson(json, "fieldName", LOGGER);

            assertThat(result).isPresent().contains("fieldValue");
        }

        @Test
        @DisplayName("Should return empty for null JSON")
        void shouldReturnEmptyForNullJson() {
            Optional<String> result = TaskAssignationUtils.extractFieldFromJson(null, "field", LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for blank JSON")
        void shouldReturnEmptyForBlankJson() {
            Optional<String> result = TaskAssignationUtils.extractFieldFromJson("   ", "field", LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when field not found")
        void shouldReturnEmptyWhenFieldNotFound() {
            String json = "{\"otherField\": \"value\"}";

            Optional<String> result = TaskAssignationUtils.extractFieldFromJson(json, "missingField", LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for invalid JSON")
        void shouldReturnEmptyForInvalidJson() {
            Optional<String> result = TaskAssignationUtils.extractFieldFromJson("{ invalid }", "field", LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should trim extracted value")
        void shouldTrimExtractedValue() {
            String json = "{\"field\": \"  value  \"}";

            Optional<String> result = TaskAssignationUtils.extractFieldFromJson(json, "field", LOGGER);

            assertThat(result).isPresent().contains("value");
        }

        @Test
        @DisplayName("Should return empty for blank field value")
        void shouldReturnEmptyForBlankFieldValue() {
            String json = "{\"field\": \"   \"}";

            Optional<String> result = TaskAssignationUtils.extractFieldFromJson(json, "field", LOGGER);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("collectAllUserIds Tests")
    class CollectAllUserIdsTests {

        @Test
        @DisplayName("Should return empty set for null config")
        void shouldReturnEmptySetForNullConfig() {
            Set<Long> result = TaskAssignationUtils.collectAllUserIds(
                    null,
                    ref -> null,
                    obj -> null,
                    refs -> Collections.emptyList(),
                    identityAPI,
                    LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty set for config with no sources")
        void shouldReturnEmptySetForConfigWithNoSources() {
            Set<Long> result = TaskAssignationUtils.collectAllUserIds(
                    UsersConfigRecord.empty(),
                    ref -> null,
                    obj -> null,
                    refs -> Collections.emptyList(),
                    identityAPI,
                    LOGGER);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should collect stepUser ID")
        void shouldCollectStepUserId() {
            UsersConfigRecord config = new UsersConfigRecord("step_xxx", null, null, null);
            Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(100L);
            Function<TestStepInstance, Long> userIdExtractor = TestStepInstance::getUserId;

            Set<Long> result = TaskAssignationUtils.collectAllUserIds(
                    config, stepFinder, userIdExtractor,
                    refs -> Collections.emptyList(), identityAPI, LOGGER);

            assertThat(result).containsExactly(100L);
        }
    }

    @Nested
    @DisplayName("parseAndCollectUserIds Tests")
    class ParseAndCollectUserIdsTests {

        @Test
        @DisplayName("Should parse and collect user IDs in one call")
        void shouldParseAndCollectUserIdsInOneCall() {
            String json = """
                    {
                        "users": {
                            "stepUser": "step_xxx"
                        }
                    }
                    """;
            Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(100L);
            Function<TestStepInstance, Long> userIdExtractor = TestStepInstance::getUserId;

            Set<Long> result = TaskAssignationUtils.parseAndCollectUserIds(
                    json, stepFinder, userIdExtractor,
                    refs -> Collections.emptyList(), identityAPI, LOGGER);

            assertThat(result).containsExactly(100L);
        }

        @Test
        @DisplayName("Should return empty set for invalid JSON")
        void shouldReturnEmptySetForInvalidJson() {
            Set<Long> result = TaskAssignationUtils.parseAndCollectUserIds(
                    "invalid",
                    ref -> new TestStepInstance(100L),
                    TestStepInstance::getUserId,
                    refs -> Collections.emptyList(),
                    identityAPI,
                    LOGGER);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // Test Helper Classes
    // =========================================================================

    /**
     * Test helper class to simulate a step instance.
     */
    static class TestStepInstance {
        private final Long userId;
        private final String jsonInput;

        TestStepInstance(Long userId) {
            this(userId, null);
        }

        TestStepInstance(Long userId, String jsonInput) {
            this.userId = userId;
            this.jsonInput = jsonInput;
        }

        public Long getUserId() {
            return userId;
        }

        public String getJsonInput() {
            return jsonInput;
        }
    }
}
