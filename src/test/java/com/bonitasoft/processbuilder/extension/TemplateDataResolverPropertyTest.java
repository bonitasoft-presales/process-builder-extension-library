package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.ContactData;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Property-based tests for {@link TemplateDataResolver} utility class.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("TemplateDataResolver Property-Based Tests")
class TemplateDataResolverPropertyTest {

    // =========================================================================
    // UTILITY CLASS PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Utility class should not be instantiable")
    void utilityClassShouldNotBeInstantiable() {
        assertThatThrownBy(() -> {
            var constructor = TemplateDataResolver.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // generateTaskLink() PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("generateTaskLink should contain task ID in result")
    void generateTaskLinkShouldContainTaskId(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long taskId) {

        String result = TemplateDataResolver.generateTaskLink("https://example.com", taskId);

        assertThat(result).contains(String.valueOf(taskId));
    }

    @Property(tries = 500)
    @Label("generateTaskLink should produce valid HTML anchor")
    void generateTaskLinkShouldProduceValidHtmlAnchor(
            @ForAll @StringLength(min = 10, max = 50) @AlphaChars String host,
            @ForAll @LongRange(min = 1, max = 10000) long taskId) {

        String hostUrl = "https://" + host.toLowerCase() + ".com";
        String result = TemplateDataResolver.generateTaskLink(hostUrl, taskId);

        assertThat(result)
                .startsWith("<a href=\"")
                .contains("taskId=" + taskId)
                .endsWith("\">#" + taskId + "</a>");
    }

    @Property(tries = 300)
    @Label("generateTaskLink should handle trailing slash in host URL")
    void generateTaskLinkShouldHandleTrailingSlash(
            @ForAll @StringLength(min = 5, max = 30) @AlphaChars String host,
            @ForAll @LongRange(min = 1, max = 10000) long taskId) {

        String hostWithSlash = "https://" + host.toLowerCase() + ".com/";
        String hostWithoutSlash = "https://" + host.toLowerCase() + ".com";

        String resultWithSlash = TemplateDataResolver.generateTaskLink(hostWithSlash, taskId);
        String resultWithoutSlash = TemplateDataResolver.generateTaskLink(hostWithoutSlash, taskId);

        assertThat(resultWithSlash).isEqualTo(resultWithoutSlash);
    }

    @Property(tries = 100)
    @Label("generateTaskLink should return hash-taskId for null host")
    void generateTaskLinkShouldReturnHashForNullHost(
            @ForAll @LongRange(min = 1, max = 10000) long taskId) {

        String result = TemplateDataResolver.generateTaskLink(null, taskId);

        assertThat(result).isEqualTo("#" + taskId);
    }

    @Property(tries = 100)
    @Label("generateTaskLink should return invalid-task for invalid taskId")
    void generateTaskLinkShouldReturnInvalidForBadTaskId(
            @ForAll @LongRange(min = Long.MIN_VALUE, max = 0) long taskId) {

        String result = TemplateDataResolver.generateTaskLink("https://example.com", taskId);

        assertThat(result).isEqualTo("#invalid-task");
    }

    // =========================================================================
    // generateTaskUrl() PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("generateTaskUrl should produce valid URL")
    void generateTaskUrlShouldProduceValidUrl(
            @ForAll @StringLength(min = 5, max = 30) @AlphaChars String host,
            @ForAll @LongRange(min = 1, max = 10000) long taskId) {

        String hostUrl = "https://" + host.toLowerCase() + ".com";
        String result = TemplateDataResolver.generateTaskUrl(hostUrl, taskId);

        assertThat(result)
                .startsWith(hostUrl)
                .contains("taskId=" + taskId)
                .doesNotContain("//app"); // No double slashes
    }

    @Property(tries = 300)
    @Label("generateTaskUrl should return null for invalid inputs")
    void generateTaskUrlShouldReturnNullForInvalidInputs(
            @ForAll @LongRange(min = Long.MIN_VALUE, max = 0) long invalidTaskId) {

        String result = TemplateDataResolver.generateTaskUrl("https://example.com", invalidTaskId);

        assertThat(result).isNull();
    }

    @Property(tries = 100)
    @Label("generateTaskUrl should return null for null host")
    void generateTaskUrlShouldReturnNullForNullHost(
            @ForAll @LongRange(min = 1, max = 10000) long taskId) {

        String result = TemplateDataResolver.generateTaskUrl(null, taskId);

        assertThat(result).isNull();
    }

    @Property(tries = 100)
    @Label("generateTaskUrl should return null for blank host")
    void generateTaskUrlShouldReturnNullForBlankHost(
            @ForAll @IntRange(min = 1, max = 10) int spaces,
            @ForAll @LongRange(min = 1, max = 10000) long taskId) {

        String blankHost = " ".repeat(spaces);
        String result = TemplateDataResolver.generateTaskUrl(blankHost, taskId);

        assertThat(result).isNull();
    }

    // =========================================================================
    // USER INFO METHOD PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("getUserFirstName should return empty for null identityAPI")
    void getUserFirstNameShouldReturnEmptyForNullApi(
            @ForAll @LongRange(min = 1, max = 10000) long userId) {

        var result = TemplateDataResolver.getUserFirstName(null, userId);

        assertThat(result).isEmpty();
    }

    @Property(tries = 300)
    @Label("getUserFirstName should return empty for null userId")
    void getUserFirstNameShouldReturnEmptyForNullUserId() throws Exception {
        IdentityAPI mockApi = mock(IdentityAPI.class);

        var result = TemplateDataResolver.getUserFirstName(mockApi, null);

        assertThat(result).isEmpty();
        verifyNoInteractions(mockApi);
    }

    @Property(tries = 300)
    @Label("getUserFirstName should return empty for invalid userId")
    void getUserFirstNameShouldReturnEmptyForInvalidUserId(
            @ForAll @LongRange(min = Long.MIN_VALUE, max = 0) long invalidUserId) throws Exception {

        IdentityAPI mockApi = mock(IdentityAPI.class);

        var result = TemplateDataResolver.getUserFirstName(mockApi, invalidUserId);

        assertThat(result).isEmpty();
        verifyNoInteractions(mockApi);
    }

    @Property(tries = 300)
    @Label("getUserLastName should return empty for null identityAPI")
    void getUserLastNameShouldReturnEmptyForNullApi(
            @ForAll @LongRange(min = 1, max = 10000) long userId) {

        var result = TemplateDataResolver.getUserLastName(null, userId);

        assertThat(result).isEmpty();
    }

    @Property(tries = 300)
    @Label("getUserEmail should return empty for null identityAPI")
    void getUserEmailShouldReturnEmptyForNullApi(
            @ForAll @LongRange(min = 1, max = 10000) long userId) {

        var result = TemplateDataResolver.getUserEmail(null, userId);

        assertThat(result).isEmpty();
    }

    @Property(tries = 300)
    @Label("getUserEmail should return empty for invalid userId")
    void getUserEmailShouldReturnEmptyForInvalidUserId(
            @ForAll @LongRange(min = Long.MIN_VALUE, max = 0) long invalidUserId) throws Exception {

        IdentityAPI mockApi = mock(IdentityAPI.class);

        var result = TemplateDataResolver.getUserEmail(mockApi, invalidUserId);

        assertThat(result).isEmpty();
        verifyNoInteractions(mockApi);
    }

    @Property(tries = 300)
    @Label("getUserFullName should return empty for null identityAPI")
    void getUserFullNameShouldReturnEmptyForNullApi(
            @ForAll @LongRange(min = 1, max = 10000) long userId) {

        var result = TemplateDataResolver.getUserFullName(null, userId);

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // createResolver() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("createResolver should throw NPE for null identityAPI")
    void createResolverShouldThrowNPEForNullApi(
            @ForAll @LongRange(min = 1, max = 10000) long userId) {

        assertThatThrownBy(() -> TemplateDataResolver.createResolver(null, userId, "https://example.com", 123L, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("IdentityAPI");
    }

    @Property(tries = 300)
    @Label("createResolver should return null for unknown dataName without custom resolver")
    void createResolverShouldReturnNullForUnknownDataName(
            @ForAll @StringLength(min = 10, max = 30) @AlphaChars String unknownDataName) throws Exception {

        // Filter out known data names
        Assume.that(!unknownDataName.toLowerCase().contains("recipient"));
        Assume.that(!unknownDataName.toLowerCase().contains("task"));
        Assume.that(!unknownDataName.toLowerCase().contains("step"));

        IdentityAPI mockApi = mock(IdentityAPI.class);

        BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                mockApi, 123L, "https://example.com", 456L, null);

        String result = resolver.apply("ref", unknownDataName);

        assertThat(result).isNull();
    }

    @Property(tries = 300)
    @Label("createResolver should delegate to custom resolver")
    void createResolverShouldDelegateToCustomResolver(
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String refStep,
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String dataName,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String customValue) throws Exception {

        // Filter out standard data names
        Assume.that(!dataName.toLowerCase().contains("recipient"));
        Assume.that(!dataName.toLowerCase().contains("task"));
        Assume.that(!dataName.toLowerCase().contains("step"));

        IdentityAPI mockApi = mock(IdentityAPI.class);

        BiFunction<String, String, String> customResolver = (r, d) -> {
            if (refStep.equals(r) && dataName.equals(d)) {
                return customValue;
            }
            return null;
        };

        BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                mockApi, 123L, "https://example.com", 456L, customResolver);

        String result = resolver.apply(refStep, dataName);

        assertThat(result).isEqualTo(customValue);
    }

    // =========================================================================
    // createStepDataResolver() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("createStepDataResolver should throw NPE for null stepLookup")
    void createStepDataResolverShouldThrowNPEForNullStepLookup() {
        assertThatThrownBy(() -> TemplateDataResolver.createStepDataResolver(
                null,
                (Object s) -> "user",
                (Object s) -> "status"))
                .isInstanceOf(NullPointerException.class);
    }

    @Property(tries = 100)
    @Label("createStepDataResolver should throw NPE for null usernameExtractor")
    void createStepDataResolverShouldThrowNPEForNullUsernameExtractor() {
        assertThatThrownBy(() -> TemplateDataResolver.createStepDataResolver(
                (String ref) -> new Object(),
                null,
                (Object s) -> "status"))
                .isInstanceOf(NullPointerException.class);
    }

    @Property(tries = 100)
    @Label("createStepDataResolver should throw NPE for null statusExtractor")
    void createStepDataResolverShouldThrowNPEForNullStatusExtractor() {
        assertThatThrownBy(() -> TemplateDataResolver.createStepDataResolver(
                (String ref) -> new Object(),
                (Object s) -> "user",
                null))
                .isInstanceOf(NullPointerException.class);
    }

    @Property(tries = 300)
    @Label("createStepDataResolver should return null for null refStep")
    void createStepDataResolverShouldReturnNullForNullRefStep() {
        BiFunction<String, String, String> resolver = TemplateDataResolver.createStepDataResolver(
                (String ref) -> "data",
                (String s) -> "user",
                (String s) -> "status");

        String result = resolver.apply(null, "step_user_name");

        assertThat(result).isNull();
    }

    @Property(tries = 300)
    @Label("createStepDataResolver should return null for non-step dataName")
    void createStepDataResolverShouldReturnNullForNonStepDataName(
            @ForAll @StringLength(min = 5, max = 20) @AlphaChars String dataName) {

        Assume.that(!dataName.equals("step_user_name"));
        Assume.that(!dataName.equals("step_status"));

        BiFunction<String, String, String> resolver = TemplateDataResolver.createStepDataResolver(
                (String ref) -> "data",
                (String s) -> "user",
                (String s) -> "status");

        String result = resolver.apply("step_123", dataName);

        assertThat(result).isNull();
    }

    @Property(tries = 300)
    @Label("createStepDataResolver should correctly extract username")
    void createStepDataResolverShouldExtractUsername(
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String refStep,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String username) {

        Function<String, String> stepLookup = ref -> ref.equals(refStep) ? "step_data" : null;
        Function<String, String> usernameExtractor = step -> username;
        Function<String, String> statusExtractor = step -> "status";

        BiFunction<String, String, String> resolver = TemplateDataResolver.createStepDataResolver(
                stepLookup, usernameExtractor, statusExtractor);

        String result = resolver.apply(refStep, "step_user_name");

        assertThat(result).isEqualTo(username);
    }

    @Property(tries = 300)
    @Label("createStepDataResolver should correctly extract status")
    void createStepDataResolverShouldExtractStatus(
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String refStep,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String status) {

        Function<String, String> stepLookup = ref -> ref.equals(refStep) ? "step_data" : null;
        Function<String, String> usernameExtractor = step -> "user";
        Function<String, String> statusExtractor = step -> status;

        BiFunction<String, String, String> resolver = TemplateDataResolver.createStepDataResolver(
                stepLookup, usernameExtractor, statusExtractor);

        String result = resolver.apply(refStep, "step_status");

        assertThat(result).isEqualTo(status);
    }

    @Property(tries = 300)
    @Label("createStepDataResolver should return null when step not found")
    void createStepDataResolverShouldReturnNullWhenStepNotFound(
            @ForAll @StringLength(min = 1, max = 10) @AlphaChars String refStep) {

        Function<String, String> stepLookup = ref -> null;
        Function<String, String> usernameExtractor = step -> "user";
        Function<String, String> statusExtractor = step -> "status";

        BiFunction<String, String, String> resolver = TemplateDataResolver.createStepDataResolver(
                stepLookup, usernameExtractor, statusExtractor);

        String resultUsername = resolver.apply(refStep, "step_user_name");
        String resultStatus = resolver.apply(refStep, "step_status");

        assertThat(resultUsername).isNull();
        assertThat(resultStatus).isNull();
    }
}
