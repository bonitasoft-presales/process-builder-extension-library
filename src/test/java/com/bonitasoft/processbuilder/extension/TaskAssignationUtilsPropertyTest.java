package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.records.UsersConfigRecord;
import com.fasterxml.jackson.databind.JsonNode;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link TaskAssignationUtils}.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("TaskAssignationUtils Property-Based Tests")
class TaskAssignationUtilsPropertyTest {

    // =========================================================================
    // Class Structure Properties
    // =========================================================================

    @Example
    @Label("Class should be final")
    void classShouldBeFinal() {
        assertThat(Modifier.isFinal(TaskAssignationUtils.class.getModifiers())).isTrue();
    }

    @Example
    @Label("Constructor should be private")
    void constructorShouldBePrivate() throws Exception {
        Constructor<TaskAssignationUtils> constructor =
                TaskAssignationUtils.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
    }

    @Example
    @Label("Constructor should throw UnsupportedOperationException")
    void constructorShouldThrowException() throws Exception {
        Constructor<TaskAssignationUtils> constructor =
                TaskAssignationUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // parseUsersConfig Null Safety Properties
    // =========================================================================

    @Example
    @Label("parseUsersConfig should return empty for null JSON")
    void parseUsersConfigShouldReturnEmptyForNullJson() {
        UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig(null, null);

        assertThat(result).isEqualTo(UsersConfigRecord.empty());
    }

    @Property(tries = 50)
    @Label("parseUsersConfig should return empty for blank JSON")
    void parseUsersConfigShouldReturnEmptyForBlankJson(
            @ForAll("blankStrings") String blankJson) {

        UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig(blankJson, null);

        assertThat(result).isEqualTo(UsersConfigRecord.empty());
    }

    @Example
    @Label("parseUsersConfig should handle null logger")
    void parseUsersConfigShouldHandleNullLogger() {
        String json = "{\"users\": {\"stepUser\": \"step_xxx\"}}";

        UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig(json, null);

        assertThat(result.stepUser()).isEqualTo("step_xxx");
    }

    // =========================================================================
    // parseUsersConfig Determinism Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("parseUsersConfig should be deterministic")
    void parseUsersConfigShouldBeDeterministic(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepUser) {

        String json = "{\"users\": {\"stepUser\": \"" + stepUser + "\"}}";

        UsersConfigRecord result1 = TaskAssignationUtils.parseUsersConfig(json, null);
        UsersConfigRecord result2 = TaskAssignationUtils.parseUsersConfig(json, null);
        UsersConfigRecord result3 = TaskAssignationUtils.parseUsersConfig(json, null);

        assertThat(result1).isEqualTo(result2).isEqualTo(result3);
    }

    @Property(tries = 100)
    @Label("parseUsersConfig should extract stepUser correctly")
    void parseUsersConfigShouldExtractStepUserCorrectly(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepUser) {

        String json = "{\"users\": {\"stepUser\": \"" + stepUser + "\"}}";

        UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig(json, null);

        assertThat(result.stepUser()).isEqualTo(stepUser);
    }

    @Property(tries = 100)
    @Label("parseUsersConfig should extract stepManager correctly")
    void parseUsersConfigShouldExtractStepManagerCorrectly(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String stepManager) {

        String json = "{\"users\": {\"stepManager\": \"" + stepManager + "\"}}";

        UsersConfigRecord result = TaskAssignationUtils.parseUsersConfig(json, null);

        assertThat(result.stepManager()).isEqualTo(stepManager);
    }

    // =========================================================================
    // parseUsersNode Properties
    // =========================================================================

    @Example
    @Label("parseUsersNode should return empty for null JSON")
    void parseUsersNodeShouldReturnEmptyForNullJson() {
        Optional<JsonNode> result = TaskAssignationUtils.parseUsersNode(null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 50)
    @Label("parseUsersNode should return empty for blank JSON")
    void parseUsersNodeShouldReturnEmptyForBlankJson(
            @ForAll("blankStrings") String blankJson) {

        Optional<JsonNode> result = TaskAssignationUtils.parseUsersNode(blankJson, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("parseUsersNode should return present for valid JSON with users key")
    void parseUsersNodeShouldReturnPresentForValidJson(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepUser) {

        String json = "{\"users\": {\"stepUser\": \"" + stepUser + "\"}}";

        Optional<JsonNode> result = TaskAssignationUtils.parseUsersNode(json, null);

        assertThat(result).isPresent();
    }

    // =========================================================================
    // processStepUser Properties
    // =========================================================================

    @Example
    @Label("processStepUser should return empty for null config")
    void processStepUserShouldReturnEmptyForNullConfig() {
        Optional<Long> result = TaskAssignationUtils.processStepUser(
                null, ref -> new TestStepInstance(100L),
                TestStepInstance::getUserId, null);

        assertThat(result).isEmpty();
    }

    @Example
    @Label("processStepUser should return empty for config without stepUser")
    void processStepUserShouldReturnEmptyForConfigWithoutStepUser() {
        UsersConfigRecord config = UsersConfigRecord.empty();

        Optional<Long> result = TaskAssignationUtils.processStepUser(
                config, ref -> new TestStepInstance(100L),
                TestStepInstance::getUserId, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("processStepUser should return user ID when step is found")
    void processStepUserShouldReturnUserIdWhenFound(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepRef,
            @ForAll @LongRange(min = 1, max = 1000000) long userId) {

        UsersConfigRecord config = new UsersConfigRecord(stepRef, null, null, null);
        Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(userId);

        Optional<Long> result = TaskAssignationUtils.processStepUser(
                config, stepFinder, TestStepInstance::getUserId, null);

        assertThat(result).isPresent().contains(userId);
    }

    @Property(tries = 50)
    @Label("processStepUser should return empty when step not found")
    void processStepUserShouldReturnEmptyWhenStepNotFound(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepRef) {

        UsersConfigRecord config = new UsersConfigRecord(stepRef, null, null, null);
        Function<String, TestStepInstance> stepFinder = ref -> null;

        Optional<Long> result = TaskAssignationUtils.processStepUser(
                config, stepFinder, TestStepInstance::getUserId, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 50)
    @Label("processStepUser should return empty for invalid user ID")
    void processStepUserShouldReturnEmptyForInvalidUserId(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepRef,
            @ForAll @LongRange(min = -100, max = 0) long invalidUserId) {

        UsersConfigRecord config = new UsersConfigRecord(stepRef, null, null, null);
        Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(invalidUserId);

        Optional<Long> result = TaskAssignationUtils.processStepUser(
                config, stepFinder, TestStepInstance::getUserId, null);

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // processStepManager Properties
    // =========================================================================

    @Example
    @Label("processStepManager should return empty for null config")
    void processStepManagerShouldReturnEmptyForNullConfig() {
        Optional<Long> result = TaskAssignationUtils.processStepManager(
                null, ref -> new TestStepInstance(100L),
                TestStepInstance::getUserId, null, null);

        assertThat(result).isEmpty();
    }

    @Example
    @Label("processStepManager should return empty for config without stepManager")
    void processStepManagerShouldReturnEmptyForConfigWithoutStepManager() {
        UsersConfigRecord config = UsersConfigRecord.empty();

        Optional<Long> result = TaskAssignationUtils.processStepManager(
                config, ref -> new TestStepInstance(100L),
                TestStepInstance::getUserId, null, null);

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // processMemberships Properties
    // =========================================================================

    @Example
    @Label("processMemberships should return empty for null list")
    void processMembershipsShouldReturnEmptyForNullList() {
        Set<Long> result = TaskAssignationUtils.processMemberships(
                null, refs -> Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    @Example
    @Label("processMemberships should return empty for empty list")
    void processMembershipsShouldReturnEmptyForEmptyList() {
        Set<Long> result = TaskAssignationUtils.processMemberships(
                Collections.emptyList(), refs -> Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 50)
    @Label("processMemberships should handle finder returning null")
    void processMembershipsShouldHandleFinderReturningNull(
            @ForAll @Size(min = 1, max = 5) List<@AlphaChars @StringLength(min = 1, max = 10) String> memberships) {

        Set<Long> result = TaskAssignationUtils.processMemberships(
                memberships, refs -> null, null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 50)
    @Label("processMemberships should handle finder returning empty list")
    void processMembershipsShouldHandleFinderReturningEmptyList(
            @ForAll @Size(min = 1, max = 5) List<@AlphaChars @StringLength(min = 1, max = 10) String> memberships) {

        Set<Long> result = TaskAssignationUtils.processMemberships(
                memberships, refs -> Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // processSingleMembership Properties
    // =========================================================================

    @Example
    @Label("processSingleMembership should return empty for null ref")
    void processSingleMembershipShouldReturnEmptyForNullRef() {
        Set<Long> result = TaskAssignationUtils.processSingleMembership(
                null, refs -> Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 50)
    @Label("processSingleMembership should return empty for blank ref")
    void processSingleMembershipShouldReturnEmptyForBlankRef(
            @ForAll("blankStrings") String blankRef) {

        Set<Long> result = TaskAssignationUtils.processSingleMembership(
                blankRef, refs -> Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // extractMembershipFromStepInput Properties
    // =========================================================================

    @Example
    @Label("extractMembershipFromStepInput should return empty for null stepFieldRef")
    void extractMembershipFromStepInputShouldReturnEmptyForNullRef() {
        Optional<String> result = TaskAssignationUtils.extractMembershipFromStepInput(
                null, ref -> null, obj -> null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 50)
    @Label("extractMembershipFromStepInput should return empty for blank stepFieldRef")
    void extractMembershipFromStepInputShouldReturnEmptyForBlankRef(
            @ForAll("blankStrings") String blankRef) {

        Optional<String> result = TaskAssignationUtils.extractMembershipFromStepInput(
                blankRef, ref -> null, obj -> null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 50)
    @Label("extractMembershipFromStepInput should return empty for invalid format")
    void extractMembershipFromStepInputShouldReturnEmptyForInvalidFormat(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String invalidRef) {

        // Invalid format: no colon separator
        Assume.that(!invalidRef.contains(":"));

        Optional<String> result = TaskAssignationUtils.extractMembershipFromStepInput(
                invalidRef, ref -> null, obj -> null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("extractMembershipFromStepInput should extract value for valid format")
    void extractMembershipFromStepInputShouldExtractValueForValidFormat(
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String stepRef,
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String fieldRef,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String membershipValue) {

        String stepFieldRef = stepRef + ":" + fieldRef;
        String jsonInput = "{\"" + fieldRef + "\": \"" + membershipValue + "\"}";

        Function<String, TestStepInstance> stepFinder = ref ->
                ref.equals(stepRef) ? new TestStepInstance(100L, jsonInput) : null;

        Optional<String> result = TaskAssignationUtils.extractMembershipFromStepInput(
                stepFieldRef, stepFinder, TestStepInstance::getJsonInput, null);

        assertThat(result).isPresent().contains(membershipValue);
    }

    // =========================================================================
    // extractFieldFromJson Properties
    // =========================================================================

    @Example
    @Label("extractFieldFromJson should return empty for null JSON")
    void extractFieldFromJsonShouldReturnEmptyForNullJson() {
        Optional<String> result = TaskAssignationUtils.extractFieldFromJson(null, "field", null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 50)
    @Label("extractFieldFromJson should return empty for blank JSON")
    void extractFieldFromJsonShouldReturnEmptyForBlankJson(
            @ForAll("blankStrings") String blankJson) {

        Optional<String> result = TaskAssignationUtils.extractFieldFromJson(blankJson, "field", null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("extractFieldFromJson should extract field value correctly")
    void extractFieldFromJsonShouldExtractFieldValueCorrectly(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String fieldName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String fieldValue) {

        String json = "{\"" + fieldName + "\": \"" + fieldValue + "\"}";

        Optional<String> result = TaskAssignationUtils.extractFieldFromJson(json, fieldName, null);

        assertThat(result).isPresent().contains(fieldValue);
    }

    @Property(tries = 50)
    @Label("extractFieldFromJson should return empty when field not found")
    void extractFieldFromJsonShouldReturnEmptyWhenFieldNotFound(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String presentField,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String missingField) {

        Assume.that(!presentField.equals(missingField));

        String json = "{\"" + presentField + "\": \"value\"}";

        Optional<String> result = TaskAssignationUtils.extractFieldFromJson(json, missingField, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("extractFieldFromJson should be deterministic")
    void extractFieldFromJsonShouldBeDeterministic(
            @ForAll @AlphaChars @StringLength(min = 1, max = 15) String fieldName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String fieldValue) {

        String json = "{\"" + fieldName + "\": \"" + fieldValue + "\"}";

        Optional<String> result1 = TaskAssignationUtils.extractFieldFromJson(json, fieldName, null);
        Optional<String> result2 = TaskAssignationUtils.extractFieldFromJson(json, fieldName, null);
        Optional<String> result3 = TaskAssignationUtils.extractFieldFromJson(json, fieldName, null);

        assertThat(result1).isEqualTo(result2).isEqualTo(result3);
    }

    // =========================================================================
    // collectAllUserIds Properties
    // =========================================================================

    @Example
    @Label("collectAllUserIds should return empty for null config")
    void collectAllUserIdsShouldReturnEmptyForNullConfig() {
        Set<Long> result = TaskAssignationUtils.collectAllUserIds(
                null, ref -> null, obj -> null, refs -> Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    @Example
    @Label("collectAllUserIds should return empty for config with no sources")
    void collectAllUserIdsShouldReturnEmptyForConfigWithNoSources() {
        Set<Long> result = TaskAssignationUtils.collectAllUserIds(
                UsersConfigRecord.empty(), ref -> null, obj -> null,
                refs -> Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("collectAllUserIds should collect stepUser ID")
    void collectAllUserIdsShouldCollectStepUserId(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepRef,
            @ForAll @LongRange(min = 1, max = 1000000) long userId) {

        UsersConfigRecord config = new UsersConfigRecord(stepRef, null, null, null);
        Function<String, TestStepInstance> stepFinder = ref -> new TestStepInstance(userId);

        Set<Long> result = TaskAssignationUtils.collectAllUserIds(
                config, stepFinder, TestStepInstance::getUserId,
                refs -> Collections.emptyList(), null, null);

        assertThat(result).containsExactly(userId);
    }

    // =========================================================================
    // parseAndCollectUserIds Properties
    // =========================================================================

    @Example
    @Label("parseAndCollectUserIds should return empty for null JSON")
    void parseAndCollectUserIdsShouldReturnEmptyForNullJson() {
        Set<Long> result = TaskAssignationUtils.parseAndCollectUserIds(
                null, ref -> null, obj -> null, refs -> Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 50)
    @Label("parseAndCollectUserIds should return empty for blank JSON")
    void parseAndCollectUserIdsShouldReturnEmptyForBlankJson(
            @ForAll("blankStrings") String blankJson) {

        Set<Long> result = TaskAssignationUtils.parseAndCollectUserIds(
                blankJson, ref -> null, obj -> null, refs -> Collections.emptyList(), null, null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 100)
    @Label("parseAndCollectUserIds should work for valid JSON with stepUser")
    void parseAndCollectUserIdsShouldWorkForValidJson(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String stepRef,
            @ForAll @LongRange(min = 1, max = 1000000) long userId) {

        String json = "{\"users\": {\"stepUser\": \"" + stepRef + "\"}}";
        Function<String, TestStepInstance> stepFinder = ref ->
                ref.equals(stepRef) ? new TestStepInstance(userId) : null;

        Set<Long> result = TaskAssignationUtils.parseAndCollectUserIds(
                json, stepFinder, TestStepInstance::getUserId,
                refs -> Collections.emptyList(), null, null);

        assertThat(result).containsExactly(userId);
    }

    // =========================================================================
    // Providers
    // =========================================================================

    @Provide
    Arbitrary<String> blankStrings() {
        return Arbitraries.of(null, "", "   ", "\t", "\n", "  \t\n  ");
    }

    // =========================================================================
    // Test Helper Class
    // =========================================================================

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
