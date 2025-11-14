package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link IdentityUtils} utility class.
 * <p>
 * This class validates all methods for retrieving user information and managers
 * from the Bonita Identity API, ensuring proper error handling and edge case coverage.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class IdentityUtilsTest {

    @Mock
    private IdentityAPI identityAPI;

    @Mock
    private Logger logger;

    @Mock
    private User user;

    @Mock
    private SearchResult<User> searchResult;

    private static final Long USER_ID = 101L;
    private static final Long MANAGER_ID = 201L;

    // -------------------------------------------------------------------------
    // Utility Class Instantiation Test
    // -------------------------------------------------------------------------

    /**
     * Tests that the private constructor throws an UnsupportedOperationException when accessed via reflection.
     */
    @Test
    @DisplayName("Should throw UnsupportedOperationException on instantiation attempt")
    void shouldThrowExceptionOnInstantiation() throws Exception {
        // Given the IdentityUtils class with private constructor
        // When attempting to get and invoke the constructor via reflection
        Constructor<IdentityUtils> constructor = IdentityUtils.class.getDeclaredConstructor();

        assertThat(Modifier.isPrivate(constructor.getModifiers()))
            .as("Constructor must be private to enforce utility class pattern")
            .isTrue();

        constructor.setAccessible(true);

        // Then an InvocationTargetException wrapping UnsupportedOperationException should be thrown
        InvocationTargetException thrown = org.junit.jupiter.api.Assertions.assertThrows(
            InvocationTargetException.class,
            constructor::newInstance,
            "The constructor call must throw an exception");

        assertThat(thrown.getCause())
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessageContaining("IdentityUtils");
    }

    // -------------------------------------------------------------------------
    // getUserManager Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getUserManager returns the manager ID when the user exists and has a manager.
     */
    @Test
    @DisplayName("getUserManager should return manager ID when user has a manager")
    void getUserManager_should_return_manager_id_when_user_has_manager() throws Exception {
        // Given a user with a manager
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(MANAGER_ID);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI, logger);

        // Then the manager ID should be returned
        assertThat(result).isEqualTo(MANAGER_ID);

        // Verify logging
        verify(logger).debug("Searching for manager of user ID: {}", USER_ID);
        verify(logger).debug("Found manager ID {} for user ID {}", MANAGER_ID, USER_ID);
    }

    /**
     * Tests that getUserManager returns null when the user has no manager assigned.
     */
    @Test
    @DisplayName("getUserManager should return null when user has no manager")
    void getUserManager_should_return_null_when_user_has_no_manager() throws Exception {
        // Given a user with no manager (0 indicates no manager for primitive long)
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(0L);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI, logger);

        // Then null should be returned
        assertThat(result).isNull();

        // Verify logging
        verify(logger).debug("User ID {} has no manager assigned", USER_ID);
    }

    /**
     * Tests that getUserManager returns null when the manager ID is zero.
     */
    @Test
    @DisplayName("getUserManager should return null when manager ID is zero")
    void getUserManager_should_return_null_when_manager_id_is_zero() throws Exception {
        // Given a user with manager ID of 0
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(0L);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI, logger);

        // Then null should be returned
        assertThat(result).isNull();

        // Verify logging
        verify(logger).debug("User ID {} has no manager assigned", USER_ID);
    }

    /**
     * Tests that getUserManager returns null when the manager ID is negative.
     */
    @Test
    @DisplayName("getUserManager should return null when manager ID is negative")
    void getUserManager_should_return_null_when_manager_id_is_negative() throws Exception {
        // Given a user with negative manager ID
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(-1L);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI, logger);

        // Then null should be returned
        assertThat(result).isNull();

        // Verify logging
        verify(logger).debug("User ID {} has no manager assigned", USER_ID);
    }

    /**
     * Tests that getUserManager returns null when the user is not found.
     */
    @Test
    @DisplayName("getUserManager should return null when user not found")
    void getUserManager_should_return_null_when_user_not_found() throws Exception {
        // Given a user that doesn't exist
        when(identityAPI.getUser(USER_ID)).thenReturn(null);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI, logger);

        // Then null should be returned
        assertThat(result).isNull();

        // Verify logging
        verify(logger).warn("User not found for ID: {}", USER_ID);
    }

    /**
     * Tests that getUserManager returns null when an exception occurs.
     */
    @Test
    @DisplayName("getUserManager should return null when exception occurs")
    void getUserManager_should_return_null_when_exception_occurs() throws Exception {
        // Given an exception when getting the user
        Exception exception = new RuntimeException("Database error");
        when(identityAPI.getUser(USER_ID)).thenThrow(exception);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI, logger);

        // Then null should be returned
        assertThat(result).isNull();

        // Verify error logging
        verify(logger).error(eq("Error getting manager for user ID {}: {}"), eq(USER_ID), eq("Database error"), eq(exception));
    }

    // -------------------------------------------------------------------------
    // getUsersByMemberships Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getUsersByMemberships returns users for valid memberships with both group and role.
     */
    @Test
    @DisplayName("getUsersByMemberships should return users for valid memberships")
    void getUsersByMemberships_should_return_users_for_valid_memberships() throws Exception {
        // Given membership objects with group and role IDs
        MockMembership membership1 = new MockMembership(10L, 20L);
        MockMembership membership2 = new MockMembership(30L, 40L);
        List<MockMembership> memberships = Arrays.asList(membership1, membership2);

        // Mock users
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        when(user1.getId()).thenReturn(100L);
        when(user2.getId()).thenReturn(200L);

        when(searchResult.getResult()).thenReturn(Arrays.asList(user1, user2));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI, logger);

        // Then user IDs should be returned
        assertThat(result).containsExactlyInAnyOrder(100L, 200L);

        // Verify logging
        verify(logger).debug("Found {} users from {} memberships", 2, 2);
    }

    /**
     * Tests that getUsersByMemberships handles memberships with only group ID.
     */
    @Test
    @DisplayName("getUsersByMemberships should handle memberships with only group ID")
    void getUsersByMemberships_should_handle_memberships_with_only_group_id() throws Exception {
        // Given membership with only group ID
        MockMembership membership = new MockMembership(10L, null);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);

        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI, logger);

        // Then user IDs should be returned
        assertThat(result).containsExactly(100L);
    }

    /**
     * Tests that getUsersByMemberships handles memberships with only role ID.
     */
    @Test
    @DisplayName("getUsersByMemberships should handle memberships with only role ID")
    void getUsersByMemberships_should_handle_memberships_with_only_role_id() throws Exception {
        // Given membership with only role ID
        MockMembership membership = new MockMembership(null, 20L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);

        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI, logger);

        // Then user IDs should be returned
        assertThat(result).containsExactly(100L);
    }

    /**
     * Tests that getUsersByMemberships skips memberships with both null group and role.
     */
    @Test
    @DisplayName("getUsersByMemberships should skip memberships with both null values")
    void getUsersByMemberships_should_skip_memberships_with_both_null() throws Exception {
        // Given membership with both null values
        MockMembership membership = new MockMembership(null, null);
        List<MockMembership> memberships = Collections.singletonList(membership);

        when(searchResult.getResult()).thenReturn(Collections.emptyList());
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI, logger);

        // Then empty set should be returned
        assertThat(result).isEmpty();

        // Verify warning logged
        verify(logger).warn("Skipping membership object with both null groupId and roleId");
    }

    /**
     * Tests that getUsersByMemberships returns empty set for null membership list.
     */
    @Test
    @DisplayName("getUsersByMemberships should return empty set for null membership list")
    void getUsersByMemberships_should_return_empty_set_for_null_list() {
        // Given a null membership list
        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(null, identityAPI, logger);

        // Then empty set should be returned
        assertThat(result).isEmpty();

        // Verify logging
        verify(logger).debug("Empty membership list provided, returning empty user set");
    }

    /**
     * Tests that getUsersByMemberships returns empty set for empty membership list.
     */
    @Test
    @DisplayName("getUsersByMemberships should return empty set for empty membership list")
    void getUsersByMemberships_should_return_empty_set_for_empty_list() {
        // Given an empty membership list
        List<MockMembership> memberships = Collections.emptyList();

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI, logger);

        // Then empty set should be returned
        assertThat(result).isEmpty();

        // Verify logging
        verify(logger).debug("Empty membership list provided, returning empty user set");
    }

    /**
     * Tests that getUsersByMemberships handles objects without getter methods gracefully.
     */
    @Test
    @DisplayName("getUsersByMemberships should handle objects without getter methods")
    void getUsersByMemberships_should_handle_objects_without_getter_methods() throws Exception {
        // Given an object without getGroupId/getRoleId methods
        String invalidObject = "Not a membership object";
        List<String> memberships = Collections.singletonList(invalidObject);

        when(searchResult.getResult()).thenReturn(Collections.emptyList());
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI, logger);

        // Then empty set should be returned
        assertThat(result).isEmpty();

        // Verify warnings logged
        verify(logger, atLeastOnce()).warn(anyString(), anyString(), anyString());
    }

    /**
     * Tests that getUsersByMemberships returns empty set when exception occurs.
     */
    @Test
    @DisplayName("getUsersByMemberships should return empty set when exception occurs")
    void getUsersByMemberships_should_return_empty_set_when_exception_occurs() throws Exception {
        // Given an exception when searching users
        MockMembership membership = new MockMembership(10L, 20L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        Exception exception = new RuntimeException("Search error");
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenThrow(exception);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI, logger);

        // Then empty set should be returned
        assertThat(result).isEmpty();

        // Verify error logging
        verify(logger).error(eq("An error occurred during user search by membership: {}"), eq("Search error"), eq(exception));
    }

    /**
     * Tests that getUsersByMemberships handles mixed valid and invalid memberships.
     */
    @Test
    @DisplayName("getUsersByMemberships should handle mixed valid and invalid memberships")
    void getUsersByMemberships_should_handle_mixed_memberships() throws Exception {
        // Given a mix of valid and invalid memberships
        MockMembership validMembership = new MockMembership(10L, 20L);
        MockMembership nullMembership = new MockMembership(null, null);
        List<Object> memberships = Arrays.asList(validMembership, nullMembership);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);

        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI, logger);

        // Then only valid users should be returned
        assertThat(result).containsExactly(100L);
    }

    /**
     * Tests that getUsersByMemberships returns no duplicate user IDs.
     */
    @Test
    @DisplayName("getUsersByMemberships should return unique user IDs")
    void getUsersByMemberships_should_return_unique_user_ids() throws Exception {
        // Given memberships that might return duplicate users
        MockMembership membership = new MockMembership(10L, 20L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        User user3 = mock(User.class);
        when(user1.getId()).thenReturn(100L);
        when(user2.getId()).thenReturn(200L);
        when(user3.getId()).thenReturn(100L); // Duplicate

        when(searchResult.getResult()).thenReturn(Arrays.asList(user1, user2, user3));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI, logger);

        // Then unique user IDs should be returned
        assertThat(result).containsExactlyInAnyOrder(100L, 200L);
    }

    // -------------------------------------------------------------------------
    // Mock Helper Class
    // -------------------------------------------------------------------------

    /**
     * Mock class to simulate a membership object with getGroupId() and getRoleId() methods.
     * This class is used for testing the reflection-based extraction in getUsersByMemberships.
     */
    private static class MockMembership {
        private final Long groupId;
        private final Long roleId;

        public MockMembership(Long groupId, Long roleId) {
            this.groupId = groupId;
            this.roleId = roleId;
        }

        public Long getGroupId() {
            return groupId;
        }

        public Long getRoleId() {
            return roleId;
        }
    }
}
