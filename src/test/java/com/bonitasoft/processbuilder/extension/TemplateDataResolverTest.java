package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.ContactData;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TemplateDataResolver} utility class.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateDataResolver Tests")
class TemplateDataResolverTest {

    @Mock
    private IdentityAPI identityAPI;

    @Mock
    private User mockUser;

    @Mock
    private ContactData mockContactData;

    private static final Long VALID_USER_ID = 123L;
    private static final Long INVALID_USER_ID = -1L;
    private static final Long TASK_ID = 456L;
    private static final String HOST_URL = "https://bonita.example.com";

    // =========================================================================
    // UTILITY CLASS TESTS
    // =========================================================================

    @Nested
    @DisplayName("Utility Class Tests")
    class UtilityClassTests {

        @Test
        @DisplayName("Should throw UnsupportedOperationException on instantiation attempt")
        void shouldThrowExceptionOnInstantiation() throws Exception {
            Constructor<TemplateDataResolver> constructor = TemplateDataResolver.class.getDeclaredConstructor();

            assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

            constructor.setAccessible(true);

            assertThatThrownBy(constructor::newInstance)
                    .isInstanceOf(InvocationTargetException.class)
                    .hasCauseInstanceOf(UnsupportedOperationException.class);
        }
    }

    // =========================================================================
    // getUserFirstName() TESTS
    // =========================================================================

    @Nested
    @DisplayName("getUserFirstName() Tests")
    class GetUserFirstNameTests {

        @Test
        @DisplayName("Should return first name for valid user")
        void shouldReturnFirstNameForValidUser() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getFirstName()).thenReturn("John");

            Optional<String> result = TemplateDataResolver.getUserFirstName(identityAPI, VALID_USER_ID);

            assertThat(result).isPresent().hasValue("John");
        }

        @Test
        @DisplayName("Should return empty for null identityAPI")
        void shouldReturnEmptyForNullIdentityAPI() {
            Optional<String> result = TemplateDataResolver.getUserFirstName(null, VALID_USER_ID);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for null userId")
        void shouldReturnEmptyForNullUserId() {
            Optional<String> result = TemplateDataResolver.getUserFirstName(identityAPI, null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for invalid userId")
        void shouldReturnEmptyForInvalidUserId() {
            Optional<String> result = TemplateDataResolver.getUserFirstName(identityAPI, INVALID_USER_ID);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when user not found")
        void shouldReturnEmptyWhenUserNotFound() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenThrow(new UserNotFoundException("not found"));

            Optional<String> result = TemplateDataResolver.getUserFirstName(identityAPI, VALID_USER_ID);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // getUserLastName() TESTS
    // =========================================================================

    @Nested
    @DisplayName("getUserLastName() Tests")
    class GetUserLastNameTests {

        @Test
        @DisplayName("Should return last name for valid user")
        void shouldReturnLastNameForValidUser() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getLastName()).thenReturn("Doe");

            Optional<String> result = TemplateDataResolver.getUserLastName(identityAPI, VALID_USER_ID);

            assertThat(result).isPresent().hasValue("Doe");
        }

        @Test
        @DisplayName("Should return empty for null identityAPI")
        void shouldReturnEmptyForNullIdentityAPI() {
            Optional<String> result = TemplateDataResolver.getUserLastName(null, VALID_USER_ID);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // getUserEmail() TESTS
    // =========================================================================

    @Nested
    @DisplayName("getUserEmail() Tests")
    class GetUserEmailTests {

        @Test
        @DisplayName("Should return email for valid user")
        void shouldReturnEmailForValidUser() throws Exception {
            when(identityAPI.getUserContactData(VALID_USER_ID, false)).thenReturn(mockContactData);
            when(mockContactData.getEmail()).thenReturn("john@example.com");

            Optional<String> result = TemplateDataResolver.getUserEmail(identityAPI, VALID_USER_ID);

            assertThat(result).isPresent().hasValue("john@example.com");
        }

        @Test
        @DisplayName("Should return empty for null email")
        void shouldReturnEmptyForNullEmail() throws Exception {
            when(identityAPI.getUserContactData(VALID_USER_ID, false)).thenReturn(mockContactData);
            when(mockContactData.getEmail()).thenReturn(null);

            Optional<String> result = TemplateDataResolver.getUserEmail(identityAPI, VALID_USER_ID);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for blank email")
        void shouldReturnEmptyForBlankEmail() throws Exception {
            when(identityAPI.getUserContactData(VALID_USER_ID, false)).thenReturn(mockContactData);
            when(mockContactData.getEmail()).thenReturn("   ");

            Optional<String> result = TemplateDataResolver.getUserEmail(identityAPI, VALID_USER_ID);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for null contact data")
        void shouldReturnEmptyForNullContactData() throws Exception {
            when(identityAPI.getUserContactData(VALID_USER_ID, false)).thenReturn(null);

            Optional<String> result = TemplateDataResolver.getUserEmail(identityAPI, VALID_USER_ID);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when user not found")
        void shouldReturnEmptyWhenUserNotFound() throws Exception {
            when(identityAPI.getUserContactData(VALID_USER_ID, false))
                    .thenThrow(new UserNotFoundException("not found"));

            Optional<String> result = TemplateDataResolver.getUserEmail(identityAPI, VALID_USER_ID);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty on general exception")
        void shouldReturnEmptyOnGeneralException() throws Exception {
            when(identityAPI.getUserContactData(VALID_USER_ID, false))
                    .thenThrow(new RuntimeException("error"));

            Optional<String> result = TemplateDataResolver.getUserEmail(identityAPI, VALID_USER_ID);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for null identityAPI")
        void shouldReturnEmptyForNullIdentityAPI() {
            Optional<String> result = TemplateDataResolver.getUserEmail(null, VALID_USER_ID);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty for invalid userId")
        void shouldReturnEmptyForInvalidUserId() {
            Optional<String> result = TemplateDataResolver.getUserEmail(identityAPI, INVALID_USER_ID);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // getUserFullName() TESTS
    // =========================================================================

    @Nested
    @DisplayName("getUserFullName() Tests")
    class GetUserFullNameTests {

        @Test
        @DisplayName("Should return full name for valid user")
        void shouldReturnFullNameForValidUser() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getFirstName()).thenReturn("John");
            when(mockUser.getLastName()).thenReturn("Doe");

            Optional<String> result = TemplateDataResolver.getUserFullName(identityAPI, VALID_USER_ID);

            assertThat(result).isPresent().hasValue("John Doe");
        }

        @Test
        @DisplayName("Should return first name only when last name is null")
        void shouldReturnFirstNameOnlyWhenLastNameNull() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getFirstName()).thenReturn("John");
            when(mockUser.getLastName()).thenReturn(null);

            Optional<String> result = TemplateDataResolver.getUserFullName(identityAPI, VALID_USER_ID);

            assertThat(result).isPresent().hasValue("John");
        }

        @Test
        @DisplayName("Should return last name only when first name is null")
        void shouldReturnLastNameOnlyWhenFirstNameNull() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getFirstName()).thenReturn(null);
            when(mockUser.getLastName()).thenReturn("Doe");

            Optional<String> result = TemplateDataResolver.getUserFullName(identityAPI, VALID_USER_ID);

            assertThat(result).isPresent().hasValue("Doe");
        }

        @Test
        @DisplayName("Should return username when both names are null")
        void shouldReturnUsernameWhenBothNamesNull() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getFirstName()).thenReturn(null);
            when(mockUser.getLastName()).thenReturn(null);
            when(mockUser.getUserName()).thenReturn("johndoe");

            Optional<String> result = TemplateDataResolver.getUserFullName(identityAPI, VALID_USER_ID);

            assertThat(result).isPresent().hasValue("johndoe");
        }
    }

    // =========================================================================
    // generateTaskLink() TESTS
    // =========================================================================

    @Nested
    @DisplayName("generateTaskLink() Tests")
    class GenerateTaskLinkTests {

        @Test
        @DisplayName("Should generate correct task link")
        void shouldGenerateCorrectTaskLink() {
            String result = TemplateDataResolver.generateTaskLink(HOST_URL, TASK_ID);

            assertThat(result)
                    .isEqualTo("<a href=\"https://bonita.example.com/app/process-builder?taskId=456\">#456</a>");
        }

        @Test
        @DisplayName("Should handle host URL with trailing slash")
        void shouldHandleHostUrlWithTrailingSlash() {
            String result = TemplateDataResolver.generateTaskLink(HOST_URL + "/", TASK_ID);

            assertThat(result)
                    .isEqualTo("<a href=\"https://bonita.example.com/app/process-builder?taskId=456\">#456</a>");
        }

        @Test
        @DisplayName("Should return hash with taskId for null host URL")
        void shouldReturnHashForNullHostUrl() {
            String result = TemplateDataResolver.generateTaskLink(null, TASK_ID);

            assertThat(result).isEqualTo("#456");
        }

        @Test
        @DisplayName("Should return hash with taskId for blank host URL")
        void shouldReturnHashForBlankHostUrl() {
            String result = TemplateDataResolver.generateTaskLink("   ", TASK_ID);

            assertThat(result).isEqualTo("#456");
        }

        @Test
        @DisplayName("Should return invalid-task for null taskId")
        void shouldReturnInvalidTaskForNullTaskId() {
            String result = TemplateDataResolver.generateTaskLink(HOST_URL, null);

            assertThat(result).isEqualTo("#invalid-task");
        }

        @Test
        @DisplayName("Should return invalid-task for zero taskId")
        void shouldReturnInvalidTaskForZeroTaskId() {
            String result = TemplateDataResolver.generateTaskLink(HOST_URL, 0L);

            assertThat(result).isEqualTo("#invalid-task");
        }

        @Test
        @DisplayName("Should return invalid-task for negative taskId")
        void shouldReturnInvalidTaskForNegativeTaskId() {
            String result = TemplateDataResolver.generateTaskLink(HOST_URL, -1L);

            assertThat(result).isEqualTo("#invalid-task");
        }
    }

    // =========================================================================
    // generateTaskUrl() TESTS
    // =========================================================================

    @Nested
    @DisplayName("generateTaskUrl() Tests")
    class GenerateTaskUrlTests {

        @Test
        @DisplayName("Should generate correct task URL")
        void shouldGenerateCorrectTaskUrl() {
            String result = TemplateDataResolver.generateTaskUrl(HOST_URL, TASK_ID);

            assertThat(result).isEqualTo("https://bonita.example.com/app/process-builder?taskId=456");
        }

        @Test
        @DisplayName("Should return null for null host URL")
        void shouldReturnNullForNullHostUrl() {
            String result = TemplateDataResolver.generateTaskUrl(null, TASK_ID);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null for blank host URL")
        void shouldReturnNullForBlankHostUrl() {
            String result = TemplateDataResolver.generateTaskUrl("   ", TASK_ID);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null for null taskId")
        void shouldReturnNullForNullTaskId() {
            String result = TemplateDataResolver.generateTaskUrl(HOST_URL, null);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null for invalid taskId")
        void shouldReturnNullForInvalidTaskId() {
            String result = TemplateDataResolver.generateTaskUrl(HOST_URL, 0L);

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should handle host URL with trailing slash")
        void shouldHandleHostUrlWithTrailingSlash() {
            String result = TemplateDataResolver.generateTaskUrl(HOST_URL + "/", TASK_ID);

            assertThat(result).isEqualTo("https://bonita.example.com/app/process-builder?taskId=456");
        }
    }

    // =========================================================================
    // createResolver() TESTS
    // =========================================================================

    @Nested
    @DisplayName("createResolver() Tests")
    class CreateResolverTests {

        @Test
        @DisplayName("Should throw NPE for null identityAPI")
        void shouldThrowNPEForNullIdentityAPI() {
            assertThatThrownBy(() -> TemplateDataResolver.createResolver(null, VALID_USER_ID, HOST_URL, TASK_ID, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("IdentityAPI cannot be null");
        }

        @Test
        @DisplayName("Should resolve recipient_firstname")
        void shouldResolveRecipientFirstname() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getFirstName()).thenReturn("Jane");

            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID, null);

            String result = resolver.apply(null, "recipient_firstname");

            assertThat(result).isEqualTo("Jane");
        }

        @Test
        @DisplayName("Should resolve recipient_lastname")
        void shouldResolveRecipientLastname() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getLastName()).thenReturn("Smith");

            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID, null);

            String result = resolver.apply(null, "recipient_lastname");

            assertThat(result).isEqualTo("Smith");
        }

        @Test
        @DisplayName("Should resolve recipient_email")
        void shouldResolveRecipientEmail() throws Exception {
            when(identityAPI.getUserContactData(VALID_USER_ID, false)).thenReturn(mockContactData);
            when(mockContactData.getEmail()).thenReturn("jane@example.com");

            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID, null);

            String result = resolver.apply(null, "recipient_email");

            assertThat(result).isEqualTo("jane@example.com");
        }

        @Test
        @DisplayName("Should resolve task_link")
        void shouldResolveTaskLink() {
            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID, null);

            String result = resolver.apply(null, "task_link");

            assertThat(result).contains("href=").contains("456");
        }

        @Test
        @DisplayName("Should delegate to custom resolver for unknown variables")
        void shouldDelegateToCustomResolver() {
            BiFunction<String, String, String> customResolver = (refStep, dataName) -> {
                if ("step_123".equals(refStep) && "custom_field".equals(dataName)) {
                    return "custom_value";
                }
                return null;
            };

            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID, customResolver);

            String result = resolver.apply("step_123", "custom_field");

            assertThat(result).isEqualTo("custom_value");
        }

        @Test
        @DisplayName("Should return null for unknown variable without custom resolver")
        void shouldReturnNullForUnknownVariableWithoutCustomResolver() {
            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID, null);

            String result = resolver.apply("step_123", "unknown_field");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null for step_user_name without custom resolver")
        void shouldReturnNullForStepUserNameWithoutCustomResolver() {
            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID, null);

            String result = resolver.apply("step_123", "step_user_name");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null for step_status without custom resolver")
        void shouldReturnNullForStepStatusWithoutCustomResolver() {
            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID, null);

            String result = resolver.apply("step_123", "step_status");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should handle null dataName")
        void shouldHandleNullDataName() {
            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID, null);

            String result = resolver.apply("step_123", null);

            assertThat(result).isNull();
        }
    }

    // =========================================================================
    // createRecipientResolver() TESTS
    // =========================================================================

    @Nested
    @DisplayName("createRecipientResolver() Tests")
    class CreateRecipientResolverTests {

        @Test
        @DisplayName("Should resolve recipient variables")
        void shouldResolveRecipientVariables() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getFirstName()).thenReturn("Alice");

            BiFunction<String, String, String> resolver = TemplateDataResolver.createRecipientResolver(
                    identityAPI, VALID_USER_ID);

            String result = resolver.apply(null, "recipient_firstname");

            assertThat(result).isEqualTo("Alice");
        }

        @Test
        @DisplayName("Should return fallback for task_link without host")
        void shouldReturnFallbackForTaskLinkWithoutHost() {
            BiFunction<String, String, String> resolver = TemplateDataResolver.createRecipientResolver(
                    identityAPI, VALID_USER_ID);

            String result = resolver.apply(null, "task_link");

            assertThat(result).isEqualTo("#null");
        }
    }

    // =========================================================================
    // createResolverWithTaskLink() TESTS
    // =========================================================================

    @Nested
    @DisplayName("createResolverWithTaskLink() Tests")
    class CreateResolverWithTaskLinkTests {

        @Test
        @DisplayName("Should resolve task_link")
        void shouldResolveTaskLink() {
            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolverWithTaskLink(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID);

            String result = resolver.apply(null, "task_link");

            assertThat(result).contains("href=").contains("456");
        }
    }

    // =========================================================================
    // createStepDataResolver() TESTS
    // =========================================================================

    @Nested
    @DisplayName("createStepDataResolver() Tests")
    class CreateStepDataResolverTests {

        @Test
        @DisplayName("Should throw NPE for null stepLookup")
        void shouldThrowNPEForNullStepLookup() {
            assertThatThrownBy(() -> TemplateDataResolver.createStepDataResolver(
                    null,
                    (Object s) -> "user",
                    (Object s) -> "status"))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw NPE for null usernameExtractor")
        void shouldThrowNPEForNullUsernameExtractor() {
            assertThatThrownBy(() -> TemplateDataResolver.createStepDataResolver(
                    (String refStep) -> new Object(),
                    null,
                    (Object s) -> "status"))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw NPE for null statusExtractor")
        void shouldThrowNPEForNullStatusExtractor() {
            assertThatThrownBy(() -> TemplateDataResolver.createStepDataResolver(
                    (String refStep) -> new Object(),
                    (Object s) -> "user",
                    null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should return null for null refStep")
        void shouldReturnNullForNullRefStep() {
            BiFunction<String, String, String> resolver = TemplateDataResolver.createStepDataResolver(
                    (String refStep) -> "step_data",
                    (String s) -> "username",
                    (String s) -> "status");

            String result = resolver.apply(null, "step_user_name");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return null for non-step dataName")
        void shouldReturnNullForNonStepDataName() {
            BiFunction<String, String, String> resolver = TemplateDataResolver.createStepDataResolver(
                    (String refStep) -> "step_data",
                    (String s) -> "username",
                    (String s) -> "status");

            String result = resolver.apply("step_123", "unknown_field");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should return username for step_user_name")
        void shouldReturnUsernameForStepUserName() {
            Function<String, String> stepLookup = refStep -> "mock_step";
            Function<String, String> usernameExtractor = step -> "JohnDoe";
            Function<String, String> statusExtractor = step -> "Completed";

            BiFunction<String, String, String> resolver = TemplateDataResolver.createStepDataResolver(
                    stepLookup, usernameExtractor, statusExtractor);

            String result = resolver.apply("step_123", "step_user_name");

            assertThat(result).isEqualTo("JohnDoe");
        }

        @Test
        @DisplayName("Should return status for step_status")
        void shouldReturnStatusForStepStatus() {
            Function<String, String> stepLookup = refStep -> "mock_step";
            Function<String, String> usernameExtractor = step -> "JohnDoe";
            Function<String, String> statusExtractor = step -> "Completed";

            BiFunction<String, String, String> resolver = TemplateDataResolver.createStepDataResolver(
                    stepLookup, usernameExtractor, statusExtractor);

            String result = resolver.apply("step_123", "step_status");

            assertThat(result).isEqualTo("Completed");
        }

        @Test
        @DisplayName("Should return null when step not found")
        void shouldReturnNullWhenStepNotFound() {
            Function<String, String> stepLookup = refStep -> null;
            Function<String, String> usernameExtractor = step -> "JohnDoe";
            Function<String, String> statusExtractor = step -> "Completed";

            BiFunction<String, String, String> resolver = TemplateDataResolver.createStepDataResolver(
                    stepLookup, usernameExtractor, statusExtractor);

            String result = resolver.apply("step_123", "step_user_name");

            assertThat(result).isNull();
        }
    }

    // =========================================================================
    // INTEGRATION TESTS WITH PBStringUtils
    // =========================================================================

    @Nested
    @DisplayName("Integration Tests with PBStringUtils")
    class IntegrationTests {

        @Test
        @DisplayName("Should resolve template with resolver")
        void shouldResolveTemplateWithResolver() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getFirstName()).thenReturn("Alice");
            when(mockUser.getLastName()).thenReturn("Wonder");

            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolverWithTaskLink(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID);

            String template = "Hello {{recipient_firstname}} {{recipient_lastname}}!";
            String result = PBStringUtils.resolveTemplateVariables(template, resolver);

            assertThat(result).isEqualTo("Hello Alice Wonder!");
        }

        @Test
        @DisplayName("Should resolve mixed template with custom resolver")
        void shouldResolveMixedTemplateWithCustomResolver() throws Exception {
            when(identityAPI.getUser(VALID_USER_ID)).thenReturn(mockUser);
            when(mockUser.getFirstName()).thenReturn("Bob");

            BiFunction<String, String, String> customResolver = (refStep, dataName) -> {
                if ("step_1".equals(refStep) && "step_user_name".equals(dataName)) {
                    return "Admin";
                }
                return null;
            };

            BiFunction<String, String, String> resolver = TemplateDataResolver.createResolver(
                    identityAPI, VALID_USER_ID, HOST_URL, TASK_ID, customResolver);

            String template = "Dear {{recipient_firstname}}, assigned by {{step_1:step_user_name}}";
            String result = PBStringUtils.resolveTemplateVariables(template, resolver);

            assertThat(result).isEqualTo("Dear Bob, assigned by Admin");
        }
    }
}
