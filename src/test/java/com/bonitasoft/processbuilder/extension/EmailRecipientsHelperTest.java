package com.bonitasoft.processbuilder.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.ContactData;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EmailRecipientsHelper}.
 * <p>
 * Tests cover all public methods including:
 * - Email retrieval by user ID and manager ID
 * - JSON parameter extraction methods
 * - DAO-independent extraction methods
 * - Utility and validation methods
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class EmailRecipientsHelperTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock
    private IdentityAPI identityAPI;

    @Mock
    private ContactData contactData;

    @Mock
    private User user;

    // =========================================================================
    // CONSTRUCTOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Private constructor should prevent instantiation")
        void constructor_should_be_private() throws Exception {
            Constructor<EmailRecipientsHelper> constructor =
                    EmailRecipientsHelper.class.getDeclaredConstructor();
            assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));

            // Verify we can access it via reflection (for coverage)
            constructor.setAccessible(true);
            assertDoesNotThrow(() -> constructor.newInstance());
        }
    }

    // =========================================================================
    // EMAIL RETRIEVAL TESTS
    // =========================================================================

    @Nested
    @DisplayName("getEmailByUserId Tests")
    class GetEmailByUserIdTests {

        @Test
        @DisplayName("Should return email for valid user")
        void should_return_email_for_valid_user() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("user@example.com");

            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 1L);

            assertTrue(result.isPresent());
            assertEquals("user@example.com", result.get());
        }

        @Test
        @DisplayName("Should return empty for null userId")
        void should_return_empty_for_null_userId() {
            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty for zero userId")
        void should_return_empty_for_zero_userId() {
            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 0L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty for negative userId")
        void should_return_empty_for_negative_userId() {
            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, -1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when user not found")
        void should_return_empty_when_user_not_found() throws Exception {
            when(identityAPI.getUserContactData(999L, false)).thenThrow(new UserNotFoundException("not found"));

            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 999L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when exception occurs")
        void should_return_empty_when_exception_occurs() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenThrow(new RuntimeException("error"));

            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when contact data is null")
        void should_return_empty_when_contact_data_is_null() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(null);

            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when email is blank")
        void should_return_empty_when_email_is_blank() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("   ");

            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw NullPointerException when identityAPI is null")
        void should_throw_when_identityAPI_is_null() {
            assertThrows(NullPointerException.class,
                    () -> EmailRecipientsHelper.getEmailByUserId(null, 1L));
        }
    }

    @Nested
    @DisplayName("getManagerEmailByUserId Tests")
    class GetManagerEmailByUserIdTests {

        @Test
        @DisplayName("Should return manager email for valid user with manager")
        void should_return_manager_email() throws Exception {
            when(identityAPI.getUser(1L)).thenReturn(user);
            when(user.getManagerUserId()).thenReturn(2L);
            when(identityAPI.getUserContactData(2L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("manager@example.com");

            Optional<String> result = EmailRecipientsHelper.getManagerEmailByUserId(identityAPI, 1L);

            assertTrue(result.isPresent());
            assertEquals("manager@example.com", result.get());
        }

        @Test
        @DisplayName("Should return empty when user has no manager")
        void should_return_empty_when_no_manager() throws Exception {
            when(identityAPI.getUser(1L)).thenReturn(user);
            when(user.getManagerUserId()).thenReturn(0L);

            Optional<String> result = EmailRecipientsHelper.getManagerEmailByUserId(identityAPI, 1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when manager has negative ID")
        void should_return_empty_when_manager_id_negative() throws Exception {
            when(identityAPI.getUser(1L)).thenReturn(user);
            when(user.getManagerUserId()).thenReturn(-1L);

            Optional<String> result = EmailRecipientsHelper.getManagerEmailByUserId(identityAPI, 1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty for invalid userId")
        void should_return_empty_for_invalid_userId() {
            Optional<String> result = EmailRecipientsHelper.getManagerEmailByUserId(identityAPI, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when user not found")
        void should_return_empty_when_user_not_found() throws Exception {
            when(identityAPI.getUser(999L)).thenThrow(new UserNotFoundException("not found"));

            Optional<String> result = EmailRecipientsHelper.getManagerEmailByUserId(identityAPI, 999L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty when exception occurs")
        void should_return_empty_when_exception_occurs() throws Exception {
            when(identityAPI.getUser(1L)).thenThrow(new RuntimeException("error"));

            Optional<String> result = EmailRecipientsHelper.getManagerEmailByUserId(identityAPI, 1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw NullPointerException when identityAPI is null")
        void should_throw_when_identityAPI_is_null() {
            assertThrows(NullPointerException.class,
                    () -> EmailRecipientsHelper.getManagerEmailByUserId(null, 1L));
        }
    }

    @Nested
    @DisplayName("getEmailsByUserIds Tests")
    class GetEmailsByUserIdsTests {

        @Test
        @DisplayName("Should return emails for valid user IDs")
        void should_return_emails_for_valid_userIds() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(identityAPI.getUserContactData(2L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("user1@example.com", "user2@example.com");

            Set<String> result = EmailRecipientsHelper.getEmailsByUserIds(identityAPI, List.of(1L, 2L));

            assertEquals(2, result.size());
            assertTrue(result.contains("user1@example.com"));
            assertTrue(result.contains("user2@example.com"));
        }

        @Test
        @DisplayName("Should filter out invalid user IDs")
        void should_filter_out_invalid_userIds() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("user@example.com");

            Set<String> result = EmailRecipientsHelper.getEmailsByUserIds(
                    identityAPI, Arrays.asList(1L, null, -1L, 0L));

            assertEquals(1, result.size());
            assertTrue(result.contains("user@example.com"));
        }

        @Test
        @DisplayName("Should return empty set for null collection")
        void should_return_empty_for_null_collection() {
            Set<String> result = EmailRecipientsHelper.getEmailsByUserIds(identityAPI, null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty set for empty collection")
        void should_return_empty_for_empty_collection() {
            Set<String> result = EmailRecipientsHelper.getEmailsByUserIds(identityAPI, Collections.emptyList());
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw NullPointerException when identityAPI is null")
        void should_throw_when_identityAPI_is_null() {
            assertThrows(NullPointerException.class,
                    () -> EmailRecipientsHelper.getEmailsByUserIds(null, List.of(1L)));
        }

        @Test
        @DisplayName("Should handle duplicates and preserve order")
        void should_handle_duplicates_and_preserve_order() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("user@example.com");

            Set<String> result = EmailRecipientsHelper.getEmailsByUserIds(identityAPI, List.of(1L, 1L, 1L));

            assertEquals(1, result.size());
            assertTrue(result instanceof LinkedHashSet);
        }
    }

    // =========================================================================
    // JSON PARAMETER EXTRACTION TESTS
    // =========================================================================

    @Nested
    @DisplayName("extractUserIdsFromParameters Tests")
    class ExtractUserIdsFromParametersTests {

        @Test
        @DisplayName("Should extract user IDs from valid array")
        void should_extract_userIds_from_valid_array() {
            ObjectNode params = MAPPER.createObjectNode();
            ObjectNode recipients = params.putObject("recipients");
            ArrayNode userIds = recipients.putArray("userIds");
            userIds.add(1L);
            userIds.add(2L);
            userIds.add(3L);

            List<Long> result = EmailRecipientsHelper.extractUserIdsFromParameters(params);

            assertEquals(3, result.size());
            assertTrue(result.containsAll(List.of(1L, 2L, 3L)));
        }

        @Test
        @DisplayName("Should filter out invalid user IDs")
        void should_filter_out_invalid_userIds() {
            ObjectNode params = MAPPER.createObjectNode();
            ObjectNode recipients = params.putObject("recipients");
            ArrayNode userIds = recipients.putArray("userIds");
            userIds.add(1L);
            userIds.add(0L);
            userIds.add(-1L);

            List<Long> result = EmailRecipientsHelper.extractUserIdsFromParameters(params);

            assertEquals(1, result.size());
            assertEquals(1L, result.get(0));
        }

        @Test
        @DisplayName("Should return empty list when node is null")
        void should_return_empty_when_node_is_null() {
            List<Long> result = EmailRecipientsHelper.extractUserIdsFromParameters(null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty list when userIds is not an array")
        void should_return_empty_when_not_array() {
            ObjectNode params = MAPPER.createObjectNode();
            ObjectNode recipients = params.putObject("recipients");
            recipients.put("userIds", "not an array");

            List<Long> result = EmailRecipientsHelper.extractUserIdsFromParameters(params);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("extractMembershipRefs Tests")
    class ExtractMembershipRefsTests {

        @Test
        @DisplayName("Should extract membership refs from valid array")
        void should_extract_membership_refs() {
            ObjectNode params = MAPPER.createObjectNode();
            ObjectNode recipients = params.putObject("recipients");
            ArrayNode membershipIds = recipients.putArray("membershipIds");
            membershipIds.add("group1/role1");
            membershipIds.add("group2/role2");

            String[] result = EmailRecipientsHelper.extractMembershipRefs(params);

            assertEquals(2, result.length);
            assertEquals("group1/role1", result[0]);
            assertEquals("group2/role2", result[1]);
        }

        @Test
        @DisplayName("Should filter out blank strings")
        void should_filter_out_blank_strings() {
            ObjectNode params = MAPPER.createObjectNode();
            ObjectNode recipients = params.putObject("recipients");
            ArrayNode membershipIds = recipients.putArray("membershipIds");
            membershipIds.add("valid");
            membershipIds.add("");
            membershipIds.add("   ");

            String[] result = EmailRecipientsHelper.extractMembershipRefs(params);

            assertEquals(1, result.length);
            assertEquals("valid", result[0]);
        }

        @Test
        @DisplayName("Should return empty array when node is null")
        void should_return_empty_when_null() {
            String[] result = EmailRecipientsHelper.extractMembershipRefs(null);
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("Should return empty array when not an array")
        void should_return_empty_when_not_array() {
            ObjectNode params = MAPPER.createObjectNode();
            ObjectNode recipients = params.putObject("recipients");
            recipients.put("membershipIds", "not array");

            String[] result = EmailRecipientsHelper.extractMembershipRefs(params);
            assertEquals(0, result.length);
        }
    }

    @Nested
    @DisplayName("extractSpecificEmails Tests")
    class ExtractSpecificEmailsTests {

        @Test
        @DisplayName("Should extract specific emails from valid array")
        void should_extract_specific_emails() {
            ObjectNode params = MAPPER.createObjectNode();
            ObjectNode recipients = params.putObject("recipients");
            ArrayNode emails = recipients.putArray("specificEmails");
            emails.add("user1@example.com");
            emails.add("user2@example.com");

            Set<String> result = EmailRecipientsHelper.extractSpecificEmails(params);

            assertEquals(2, result.size());
            assertTrue(result.contains("user1@example.com"));
            assertTrue(result.contains("user2@example.com"));
        }

        @Test
        @DisplayName("Should filter out invalid emails")
        void should_filter_out_invalid_emails() {
            ObjectNode params = MAPPER.createObjectNode();
            ObjectNode recipients = params.putObject("recipients");
            ArrayNode emails = recipients.putArray("specificEmails");
            emails.add("valid@example.com");
            emails.add("");
            emails.add("   ");

            Set<String> result = EmailRecipientsHelper.extractSpecificEmails(params);

            assertEquals(1, result.size());
            assertTrue(result.contains("valid@example.com"));
        }

        @Test
        @DisplayName("Should return empty set when null")
        void should_return_empty_when_null() {
            Set<String> result = EmailRecipientsHelper.extractSpecificEmails(null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty set when not an array")
        void should_return_empty_when_not_array() {
            ObjectNode params = MAPPER.createObjectNode();
            ObjectNode recipients = params.putObject("recipients");
            recipients.put("specificEmails", "not array");

            Set<String> result = EmailRecipientsHelper.extractSpecificEmails(params);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getStepIdParameterKey Tests")
    class GetStepIdParameterKeyTests {

        @Test
        @DisplayName("Should return correct step ID parameter key")
        void should_return_correct_key() {
            String result = EmailRecipientsHelper.getStepIdParameterKey();
            assertEquals("recipients.stepId", result);
        }
    }

    // =========================================================================
    // PROCESSING METHODS TESTS
    // =========================================================================

    @Nested
    @DisplayName("processUsersRecipients Tests")
    class ProcessUsersRecipientsTests {

        @Test
        @DisplayName("Should process users and return their emails")
        void should_process_users_and_return_emails() throws Exception {
            ObjectNode params = MAPPER.createObjectNode();
            ObjectNode recipients = params.putObject("recipients");
            ArrayNode userIds = recipients.putArray("userIds");
            userIds.add(1L);

            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("user@example.com");

            Set<String> result = EmailRecipientsHelper.processUsersRecipients(identityAPI, params);

            assertEquals(1, result.size());
            assertTrue(result.contains("user@example.com"));
        }
    }

    @Nested
    @DisplayName("addEmailForUser Tests")
    class AddEmailForUserTests {

        @Test
        @DisplayName("Should add user email to set")
        void should_add_user_email_to_set() throws Exception {
            Set<String> emails = new HashSet<>();
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("user@example.com");

            EmailRecipientsHelper.addEmailForUser(emails, identityAPI, 1L, false);

            assertEquals(1, emails.size());
            assertTrue(emails.contains("user@example.com"));
        }

        @Test
        @DisplayName("Should add manager email when fetchManager is true")
        void should_add_manager_email_when_fetchManager_true() throws Exception {
            Set<String> emails = new HashSet<>();
            when(identityAPI.getUser(1L)).thenReturn(user);
            when(user.getManagerUserId()).thenReturn(2L);
            when(identityAPI.getUserContactData(2L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("manager@example.com");

            EmailRecipientsHelper.addEmailForUser(emails, identityAPI, 1L, true);

            assertEquals(1, emails.size());
            assertTrue(emails.contains("manager@example.com"));
        }

        @Test
        @DisplayName("Should not add email for invalid userId")
        void should_not_add_for_invalid_userId() {
            Set<String> emails = new HashSet<>();

            EmailRecipientsHelper.addEmailForUser(emails, identityAPI, null, false);
            EmailRecipientsHelper.addEmailForUser(emails, identityAPI, 0L, false);
            EmailRecipientsHelper.addEmailForUser(emails, identityAPI, -1L, false);

            assertTrue(emails.isEmpty());
        }
    }

    // =========================================================================
    // UTILITY METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("filterValidEmails Tests")
    class FilterValidEmailsTests {

        @Test
        @DisplayName("Should filter valid emails")
        void should_filter_valid_emails() {
            Set<String> result = EmailRecipientsHelper.filterValidEmails(
                    Arrays.asList("valid@example.com", null, "", "   ", "another@example.com"));

            assertEquals(2, result.size());
            assertTrue(result.contains("valid@example.com"));
            assertTrue(result.contains("another@example.com"));
        }

        @Test
        @DisplayName("Should return empty set for null collection")
        void should_return_empty_for_null() {
            Set<String> result = EmailRecipientsHelper.filterValidEmails(null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty set for empty collection")
        void should_return_empty_for_empty_collection() {
            Set<String> result = EmailRecipientsHelper.filterValidEmails(Collections.emptyList());
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("joinEmails Tests")
    class JoinEmailsTests {

        @Test
        @DisplayName("Should join emails with comma separator")
        void should_join_emails() {
            String result = EmailRecipientsHelper.joinEmails(
                    Arrays.asList("a@example.com", "b@example.com"));

            assertEquals("a@example.com, b@example.com", result);
        }

        @Test
        @DisplayName("Should filter invalid emails before joining")
        void should_filter_invalid_before_joining() {
            String result = EmailRecipientsHelper.joinEmails(
                    Arrays.asList("valid@example.com", "", null, "another@example.com"));

            assertEquals("valid@example.com, another@example.com", result);
        }

        @Test
        @DisplayName("Should remove duplicates")
        void should_remove_duplicates() {
            String result = EmailRecipientsHelper.joinEmails(
                    Arrays.asList("same@example.com", "same@example.com"));

            assertEquals("same@example.com", result);
        }

        @Test
        @DisplayName("Should return empty string for null collection")
        void should_return_empty_for_null() {
            String result = EmailRecipientsHelper.joinEmails(null);
            assertEquals("", result);
        }

        @Test
        @DisplayName("Should return empty string for empty collection")
        void should_return_empty_for_empty_collection() {
            String result = EmailRecipientsHelper.joinEmails(Collections.emptyList());
            assertEquals("", result);
        }
    }

    @Nested
    @DisplayName("isValidUserId Tests")
    class IsValidUserIdTests {

        @Test
        @DisplayName("Should return true for positive userId")
        void should_return_true_for_positive() {
            assertTrue(EmailRecipientsHelper.isValidUserId(1L));
            assertTrue(EmailRecipientsHelper.isValidUserId(Long.MAX_VALUE));
        }

        @Test
        @DisplayName("Should return false for null")
        void should_return_false_for_null() {
            assertFalse(EmailRecipientsHelper.isValidUserId(null));
        }

        @Test
        @DisplayName("Should return false for zero")
        void should_return_false_for_zero() {
            assertFalse(EmailRecipientsHelper.isValidUserId(0L));
        }

        @Test
        @DisplayName("Should return false for negative")
        void should_return_false_for_negative() {
            assertFalse(EmailRecipientsHelper.isValidUserId(-1L));
            assertFalse(EmailRecipientsHelper.isValidUserId(Long.MIN_VALUE));
        }
    }

    @Nested
    @DisplayName("isValidEmail Tests")
    class IsValidEmailTests {

        @Test
        @DisplayName("Should return true for valid email")
        void should_return_true_for_valid() {
            assertTrue(EmailRecipientsHelper.isValidEmail("test@example.com"));
            assertTrue(EmailRecipientsHelper.isValidEmail("a"));
        }

        @Test
        @DisplayName("Should return false for null")
        void should_return_false_for_null() {
            assertFalse(EmailRecipientsHelper.isValidEmail(null));
        }

        @Test
        @DisplayName("Should return false for empty string")
        void should_return_false_for_empty() {
            assertFalse(EmailRecipientsHelper.isValidEmail(""));
        }

        @Test
        @DisplayName("Should return false for blank string")
        void should_return_false_for_blank() {
            assertFalse(EmailRecipientsHelper.isValidEmail("   "));
            assertFalse(EmailRecipientsHelper.isValidEmail("\t\n"));
        }
    }

    // =========================================================================
    // DAO-INDEPENDENT EXTRACTION TESTS
    // =========================================================================

    @Nested
    @DisplayName("extractUserIdFromFirstStep Tests")
    class ExtractUserIdFromFirstStepTests {

        @Test
        @DisplayName("Should extract userId from first step")
        void should_extract_userId_from_first_step() {
            List<TestStep> steps = List.of(
                    new TestStep(100L),
                    new TestStep(200L)
            );

            Long result = EmailRecipientsHelper.extractUserIdFromFirstStep(steps, TestStep::getUserId);

            assertEquals(100L, result);
        }

        @Test
        @DisplayName("Should return null for null list")
        void should_return_null_for_null_list() {
            Long result = EmailRecipientsHelper.extractUserIdFromFirstStep(null, step -> 1L);
            assertNull(result);
        }

        @Test
        @DisplayName("Should return null for empty list")
        void should_return_null_for_empty_list() {
            Long result = EmailRecipientsHelper.extractUserIdFromFirstStep(
                    Collections.emptyList(), step -> 1L);
            assertNull(result);
        }

        @Test
        @DisplayName("Should throw NullPointerException when extractor is null")
        void should_throw_when_extractor_is_null() {
            List<TestStep> steps = List.of(new TestStep(1L));
            assertThrows(NullPointerException.class,
                    () -> EmailRecipientsHelper.extractUserIdFromFirstStep(steps, null));
        }

        @Test
        @DisplayName("Should return null if extractor returns null")
        void should_return_null_if_extractor_returns_null() {
            List<TestStep> steps = List.of(new TestStep(null));

            Long result = EmailRecipientsHelper.extractUserIdFromFirstStep(steps, TestStep::getUserId);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("extractUserIdsFromMembershipResults Tests")
    class ExtractUserIdsFromMembershipResultsTests {

        @Test
        @DisplayName("Should extract unique user IDs from membership results")
        void should_extract_unique_userIds() {
            List<TestUserList> userLists = List.of(
                    new TestUserList(1L),
                    new TestUserList(2L),
                    new TestUserList(1L), // duplicate
                    new TestUserList(3L)
            );

            Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(
                    userLists, TestUserList::getUserId);

            assertEquals(3, result.size());
            assertTrue(result.containsAll(Set.of(1L, 2L, 3L)));
        }

        @Test
        @DisplayName("Should filter out invalid user IDs")
        void should_filter_out_invalid_userIds() {
            List<TestUserList> userLists = List.of(
                    new TestUserList(1L),
                    new TestUserList(null),
                    new TestUserList(0L),
                    new TestUserList(-1L)
            );

            Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(
                    userLists, TestUserList::getUserId);

            assertEquals(1, result.size());
            assertTrue(result.contains(1L));
        }

        @Test
        @DisplayName("Should return empty set for null collection")
        void should_return_empty_for_null_collection() {
            Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(
                    null, userList -> 1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty set for empty collection")
        void should_return_empty_for_empty_collection() {
            Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(
                    Collections.emptyList(), userList -> 1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw NullPointerException when extractor is null")
        void should_throw_when_extractor_is_null() {
            List<TestUserList> userLists = List.of(new TestUserList(1L));
            assertThrows(NullPointerException.class,
                    () -> EmailRecipientsHelper.extractUserIdsFromMembershipResults(userLists, null));
        }
    }

    @Nested
    @DisplayName("processStepBasedRecipients Tests")
    class ProcessStepBasedRecipientsTests {

        @Test
        @DisplayName("Should process step user and return email")
        void should_process_step_user_and_return_email() throws Exception {
            List<TestStep> steps = List.of(new TestStep(1L));
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("user@example.com");

            Set<String> result = EmailRecipientsHelper.processStepBasedRecipients(
                    identityAPI, steps, TestStep::getUserId, false);

            assertEquals(1, result.size());
            assertTrue(result.contains("user@example.com"));
        }

        @Test
        @DisplayName("Should process step manager and return manager email")
        void should_process_step_manager_and_return_email() throws Exception {
            List<TestStep> steps = List.of(new TestStep(1L));
            when(identityAPI.getUser(1L)).thenReturn(user);
            when(user.getManagerUserId()).thenReturn(2L);
            when(identityAPI.getUserContactData(2L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("manager@example.com");

            Set<String> result = EmailRecipientsHelper.processStepBasedRecipients(
                    identityAPI, steps, TestStep::getUserId, true);

            assertEquals(1, result.size());
            assertTrue(result.contains("manager@example.com"));
        }

        @Test
        @DisplayName("Should return empty set for null steps")
        void should_return_empty_for_null_steps() {
            Set<String> result = EmailRecipientsHelper.processStepBasedRecipients(
                    identityAPI, null, step -> 1L, false);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty set for empty steps")
        void should_return_empty_for_empty_steps() {
            Set<String> result = EmailRecipientsHelper.processStepBasedRecipients(
                    identityAPI, Collections.emptyList(), step -> 1L, false);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty set when extracted userId is invalid")
        void should_return_empty_when_userId_invalid() {
            List<TestStep> steps = List.of(new TestStep(null));

            Set<String> result = EmailRecipientsHelper.processStepBasedRecipients(
                    identityAPI, steps, TestStep::getUserId, false);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw NullPointerException when identityAPI is null")
        void should_throw_when_identityAPI_is_null() {
            List<TestStep> steps = List.of(new TestStep(1L));
            assertThrows(NullPointerException.class,
                    () -> EmailRecipientsHelper.processStepBasedRecipients(
                            null, steps, TestStep::getUserId, false));
        }
    }

    @Nested
    @DisplayName("processMembershipBasedRecipients Tests")
    class ProcessMembershipBasedRecipientsTests {

        @Test
        @DisplayName("Should process membership users and return emails")
        void should_process_membership_and_return_emails() throws Exception {
            List<TestUserList> userLists = List.of(
                    new TestUserList(1L),
                    new TestUserList(2L)
            );

            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(identityAPI.getUserContactData(2L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("user1@example.com", "user2@example.com");

            Set<String> result = EmailRecipientsHelper.processMembershipBasedRecipients(
                    identityAPI, userLists, TestUserList::getUserId);

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should return empty set for null collection")
        void should_return_empty_for_null_collection() {
            Set<String> result = EmailRecipientsHelper.processMembershipBasedRecipients(
                    identityAPI, null, userList -> 1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty set for empty collection")
        void should_return_empty_for_empty_collection() {
            Set<String> result = EmailRecipientsHelper.processMembershipBasedRecipients(
                    identityAPI, Collections.emptyList(), userList -> 1L);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return empty set when all user IDs are invalid")
        void should_return_empty_when_all_userIds_invalid() {
            List<TestUserList> userLists = List.of(
                    new TestUserList(null),
                    new TestUserList(0L)
            );

            Set<String> result = EmailRecipientsHelper.processMembershipBasedRecipients(
                    identityAPI, userLists, TestUserList::getUserId);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should throw NullPointerException when identityAPI is null")
        void should_throw_when_identityAPI_is_null() {
            List<TestUserList> userLists = List.of(new TestUserList(1L));
            assertThrows(NullPointerException.class,
                    () -> EmailRecipientsHelper.processMembershipBasedRecipients(
                            null, userLists, TestUserList::getUserId));
        }
    }

    // =========================================================================
    // MUTATION TESTING - ADDITIONAL TESTS TO KILL SURVIVING MUTATIONS
    // =========================================================================

    @Nested
    @DisplayName("Mutation Killing Tests - Conditional Verifications")
    class MutationKillingTests {

        // --- getEmailByUserId mutations ---

        @Test
        @DisplayName("getEmailByUserId should NOT call getUserContactData for invalid userId")
        void getEmailByUserId_should_not_call_api_for_invalid_userId() throws Exception {
            // When calling with null userId
            EmailRecipientsHelper.getEmailByUserId(identityAPI, null);

            // Then API should never be called (conditional short-circuits)
            verify(identityAPI, never()).getUserContactData(anyLong(), anyBoolean());
        }

        @Test
        @DisplayName("getEmailByUserId should NOT call getUserContactData for zero userId")
        void getEmailByUserId_should_not_call_api_for_zero_userId() throws Exception {
            // When calling with 0 userId
            EmailRecipientsHelper.getEmailByUserId(identityAPI, 0L);

            // Then API should never be called
            verify(identityAPI, never()).getUserContactData(anyLong(), anyBoolean());
        }

        @Test
        @DisplayName("getEmailByUserId should call getUserContactData for valid userId")
        void getEmailByUserId_should_call_api_for_valid_userId() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("test@test.com");

            // When calling with valid userId
            EmailRecipientsHelper.getEmailByUserId(identityAPI, 1L);

            // Then API should be called
            verify(identityAPI, times(1)).getUserContactData(1L, false);
        }

        // --- getManagerEmailByUserId mutations ---

        @Test
        @DisplayName("getManagerEmailByUserId should NOT call getUser for invalid userId")
        void getManagerEmailByUserId_should_not_call_api_for_invalid_userId() throws Exception {
            // When calling with null userId
            EmailRecipientsHelper.getManagerEmailByUserId(identityAPI, null);

            // Then API should never be called
            verify(identityAPI, never()).getUser(anyLong());
        }

        @Test
        @DisplayName("getManagerEmailByUserId boundary: managerId=0 returns empty")
        void getManagerEmailByUserId_boundary_manager_id_zero() throws Exception {
            when(identityAPI.getUser(1L)).thenReturn(user);
            when(user.getManagerUserId()).thenReturn(0L);  // Boundary: exactly 0

            Optional<String> result = EmailRecipientsHelper.getManagerEmailByUserId(identityAPI, 1L);

            assertTrue(result.isEmpty());
            // Should NOT try to get email for manager ID 0
            verify(identityAPI, never()).getUserContactData(eq(0L), anyBoolean());
        }

        @Test
        @DisplayName("getManagerEmailByUserId boundary: managerId=1 returns email")
        void getManagerEmailByUserId_boundary_manager_id_one() throws Exception {
            when(identityAPI.getUser(1L)).thenReturn(user);
            when(user.getManagerUserId()).thenReturn(1L);  // Boundary: smallest valid
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("mgr@test.com");

            Optional<String> result = EmailRecipientsHelper.getManagerEmailByUserId(identityAPI, 1L);

            assertTrue(result.isPresent());
            assertEquals("mgr@test.com", result.get());
        }

        // --- getEmailsByUserIds mutations ---

        @Test
        @DisplayName("getEmailsByUserIds should NOT call API for null collection")
        void getEmailsByUserIds_should_not_call_api_for_null() throws Exception {
            Set<String> result = EmailRecipientsHelper.getEmailsByUserIds(identityAPI, null);

            assertTrue(result.isEmpty());
            verify(identityAPI, never()).getUserContactData(anyLong(), anyBoolean());
        }

        @Test
        @DisplayName("getEmailsByUserIds should NOT call API for empty collection")
        void getEmailsByUserIds_should_not_call_api_for_empty() throws Exception {
            Set<String> result = EmailRecipientsHelper.getEmailsByUserIds(identityAPI, Collections.emptySet());

            assertTrue(result.isEmpty());
            verify(identityAPI, never()).getUserContactData(anyLong(), anyBoolean());
        }

        @Test
        @DisplayName("getEmailsByUserIds should call API for valid collection")
        void getEmailsByUserIds_should_call_api_for_valid() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("test@test.com");

            Set<String> result = EmailRecipientsHelper.getEmailsByUserIds(identityAPI, Set.of(1L));

            assertFalse(result.isEmpty());
            verify(identityAPI, times(1)).getUserContactData(1L, false);
        }

        // --- processStepBasedRecipients mutations ---

        @Test
        @DisplayName("processStepBasedRecipients should NOT call API for null steps")
        void processStepBasedRecipients_should_not_call_api_for_null_steps() throws Exception {
            Set<String> result = EmailRecipientsHelper.processStepBasedRecipients(
                    identityAPI, null, step -> 1L, false);

            assertTrue(result.isEmpty());
            verify(identityAPI, never()).getUserContactData(anyLong(), anyBoolean());
        }

        @Test
        @DisplayName("processStepBasedRecipients should NOT call API for empty steps")
        void processStepBasedRecipients_should_not_call_api_for_empty_steps() throws Exception {
            Set<String> result = EmailRecipientsHelper.processStepBasedRecipients(
                    identityAPI, Collections.emptyList(), step -> 1L, false);

            assertTrue(result.isEmpty());
            verify(identityAPI, never()).getUserContactData(anyLong(), anyBoolean());
        }

        @Test
        @DisplayName("processStepBasedRecipients should NOT call API when userId invalid")
        void processStepBasedRecipients_should_not_call_api_for_invalid_userId() throws Exception {
            List<TestStep> steps = List.of(new TestStep(null));

            Set<String> result = EmailRecipientsHelper.processStepBasedRecipients(
                    identityAPI, steps, TestStep::getUserId, false);

            assertTrue(result.isEmpty());
            verify(identityAPI, never()).getUserContactData(anyLong(), anyBoolean());
        }

        @Test
        @DisplayName("processStepBasedRecipients should call API for valid step user")
        void processStepBasedRecipients_should_call_api_for_valid_step() throws Exception {
            List<TestStep> steps = List.of(new TestStep(1L));
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("user@test.com");

            Set<String> result = EmailRecipientsHelper.processStepBasedRecipients(
                    identityAPI, steps, TestStep::getUserId, false);

            assertFalse(result.isEmpty());
            verify(identityAPI, times(1)).getUserContactData(1L, false);
        }

        @Test
        @DisplayName("processStepBasedRecipients fetchManager=true should call getUser then getContactData")
        void processStepBasedRecipients_manager_path() throws Exception {
            List<TestStep> steps = List.of(new TestStep(1L));
            when(identityAPI.getUser(1L)).thenReturn(user);
            when(user.getManagerUserId()).thenReturn(2L);
            when(identityAPI.getUserContactData(2L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("manager@test.com");

            Set<String> result = EmailRecipientsHelper.processStepBasedRecipients(
                    identityAPI, steps, TestStep::getUserId, true);

            assertFalse(result.isEmpty());
            verify(identityAPI, times(1)).getUser(1L);
            verify(identityAPI, times(1)).getUserContactData(2L, false);
        }

        // --- processMembershipBasedRecipients mutations ---

        @Test
        @DisplayName("processMembershipBasedRecipients should NOT call API for null collection")
        void processMembershipBasedRecipients_should_not_call_api_for_null() throws Exception {
            Set<String> result = EmailRecipientsHelper.processMembershipBasedRecipients(
                    identityAPI, null, u -> 1L);

            assertTrue(result.isEmpty());
            verify(identityAPI, never()).getUserContactData(anyLong(), anyBoolean());
        }

        @Test
        @DisplayName("processMembershipBasedRecipients should NOT call API for empty collection")
        void processMembershipBasedRecipients_should_not_call_api_for_empty() throws Exception {
            Set<String> result = EmailRecipientsHelper.processMembershipBasedRecipients(
                    identityAPI, Collections.emptyList(), u -> 1L);

            assertTrue(result.isEmpty());
            verify(identityAPI, never()).getUserContactData(anyLong(), anyBoolean());
        }

        @Test
        @DisplayName("processMembershipBasedRecipients should call API for valid collection")
        void processMembershipBasedRecipients_should_call_api_for_valid() throws Exception {
            List<TestUserList> userLists = List.of(new TestUserList(1L));
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("test@test.com");

            Set<String> result = EmailRecipientsHelper.processMembershipBasedRecipients(
                    identityAPI, userLists, TestUserList::getUserId);

            assertFalse(result.isEmpty());
            verify(identityAPI, times(1)).getUserContactData(1L, false);
        }

        // --- extractUserIdsFromMembershipResults mutations ---

        @Test
        @DisplayName("extractUserIdsFromMembershipResults should return empty for null")
        void extractUserIdsFromMembershipResults_returns_empty_for_null() {
            Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(
                    null, u -> 1L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("extractUserIdsFromMembershipResults should return empty for empty")
        void extractUserIdsFromMembershipResults_returns_empty_for_empty() {
            Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(
                    Collections.emptyList(), u -> 1L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("extractUserIdsFromMembershipResults should return non-empty for valid")
        void extractUserIdsFromMembershipResults_returns_non_empty_for_valid() {
            List<TestUserList> userLists = List.of(new TestUserList(42L));

            Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(
                    userLists, TestUserList::getUserId);

            assertFalse(result.isEmpty());
            assertTrue(result.contains(42L));
        }

        // --- extractEmail mutations (private but tested through public methods) ---

        @Test
        @DisplayName("getEmailByUserId with null contactData returns empty")
        void extractEmail_null_contactData() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(null);

            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 1L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("getEmailByUserId with blank email returns empty")
        void extractEmail_blank_email() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("");

            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 1L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("getEmailByUserId with whitespace email returns empty")
        void extractEmail_whitespace_email() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("   ");

            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 1L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("getEmailByUserId with null email returns empty")
        void extractEmail_null_email() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn(null);

            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 1L);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("getEmailByUserId with valid email returns email")
        void extractEmail_valid_email() throws Exception {
            when(identityAPI.getUserContactData(1L, false)).thenReturn(contactData);
            when(contactData.getEmail()).thenReturn("valid@test.com");

            Optional<String> result = EmailRecipientsHelper.getEmailByUserId(identityAPI, 1L);

            assertTrue(result.isPresent());
            assertEquals("valid@test.com", result.get());
        }
    }

    // =========================================================================
    // TEST HELPER CLASSES
    // =========================================================================

    /**
     * Test helper class simulating a step process instance.
     */
    private static class TestStep {
        private final Long userId;

        TestStep(Long userId) {
            this.userId = userId;
        }

        Long getUserId() {
            return userId;
        }
    }

    /**
     * Test helper class simulating a user list entry.
     */
    private static class TestUserList {
        private final Long userId;

        TestUserList(Long userId) {
            this.userId = userId;
        }

        Long getUserId() {
            return userId;
        }
    }
}
