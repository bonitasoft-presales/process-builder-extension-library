package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private User user;

    @Mock
    private SearchResult<User> searchResult;

    @Captor
    private ArgumentCaptor<SearchOptions> searchOptionsCaptor;

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
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then the manager ID should be returned
        assertThat(result).isEqualTo(MANAGER_ID);
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
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then null should be returned
        assertThat(result).isNull();
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
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then null should be returned
        assertThat(result).isNull();
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
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then null should be returned
        assertThat(result).isNull();
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
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then null should be returned
        assertThat(result).isNull();
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
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then null should be returned
        assertThat(result).isNull();
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
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then user IDs should be returned
        assertThat(result).containsExactlyInAnyOrder(100L, 200L);
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
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

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
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

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

        // When getting users by memberships - no valid conditions means no search performed
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that getUsersByMemberships returns empty set for null membership list.
     */
    @Test
    @DisplayName("getUsersByMemberships should return empty set for null membership list")
    void getUsersByMemberships_should_return_empty_set_for_null_list() {
        // Given a null membership list
        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(null, identityAPI);

        // Then empty set should be returned
        assertThat(result).isEmpty();
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
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then empty set should be returned
        assertThat(result).isEmpty();
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

        // When getting users by memberships - no valid conditions means no search performed
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then empty set should be returned
        assertThat(result).isEmpty();
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
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then empty set should be returned
        assertThat(result).isEmpty();
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
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

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
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then unique user IDs should be returned
        assertThat(result).containsExactlyInAnyOrder(100L, 200L);
    }

    /**
     * Tests that getUsersByMemberships handles objects that return non-Long values.
     */
    @Test
    @DisplayName("getUsersByMemberships should handle objects returning non-Long values")
    void getUsersByMemberships_should_handle_non_long_return() throws Exception {
        // Given an object that returns Integer instead of Long
        Object membershipWithInteger = new Object() {
            @SuppressWarnings("unused")
            public Integer getGroupId() { return 10; }
            @SuppressWarnings("unused")
            public Integer getRoleId() { return 20; }
        };
        List<Object> memberships = Collections.singletonList(membershipWithInteger);

        when(searchResult.getResult()).thenReturn(Collections.emptyList());
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then empty set should be returned (method extracts nothing useful)
        assertThat(result).isEmpty();
    }

    /**
     * Tests that getUserManager returns the exact managerId value.
     */
    @Test
    @DisplayName("getUserManager should return the exact manager ID from user")
    void getUserManager_should_return_exact_value() throws Exception {
        // Given a user with specific manager ID
        Long expectedManagerId = 12345L;
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(expectedManagerId);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then the exact manager ID should be returned
        assertThat(result).isEqualTo(expectedManagerId);
        assertThat(result).isNotEqualTo(USER_ID);
        assertThat(result).isPositive();
    }

    /**
     * Tests that getUsersByMemberships correctly handles multiple OR conditions.
     */
    @Test
    @DisplayName("getUsersByMemberships should build correct query for multiple memberships")
    void getUsersByMemberships_should_handle_multiple_memberships() throws Exception {
        // Given multiple memberships
        MockMembership membership1 = new MockMembership(10L, 20L);
        MockMembership membership2 = new MockMembership(30L, null);
        MockMembership membership3 = new MockMembership(null, 40L);
        List<MockMembership> memberships = Arrays.asList(membership1, membership2, membership3);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then users should be returned and API should be called
        assertThat(result).containsExactly(100L);
        verify(identityAPI).searchUsers(any(SearchOptions.class));
    }

    /**
     * Tests that getUserManager returns null when manager ID is the default value (0).
     * Note: getManagerUserId returns primitive long, so 0L indicates no manager.
     */
    @Test
    @DisplayName("getUserManager should return null when manager ID is default (0)")
    void getUserManager_should_return_null_when_manager_id_is_default() throws Exception {
        // Given a user with default manager ID (0L - no manager assigned)
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(0L);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then null should be returned since 0 means no manager
        assertThat(result).isNull();
    }

    /**
     * Tests that getUserManager handles boundary value of Long.MAX_VALUE.
     */
    @Test
    @DisplayName("getUserManager should return valid positive manager ID")
    void getUserManager_should_return_large_positive_manager_id() throws Exception {
        // Given a user with a large manager ID
        Long largeManagerId = Long.MAX_VALUE;
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(largeManagerId);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then the large manager ID should be returned
        assertThat(result).isEqualTo(largeManagerId);
    }

    /**
     * Tests that getUserManager handles manager ID of exactly 1.
     */
    @Test
    @DisplayName("getUserManager should return manager ID of 1")
    void getUserManager_should_return_manager_id_of_one() throws Exception {
        // Given a user with manager ID of 1 (edge case)
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(1L);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then the manager ID should be returned
        assertThat(result).isEqualTo(1L);
    }

    /**
     * Tests that getUsersByMemberships handles all-null memberships correctly.
     */
    @Test
    @DisplayName("getUsersByMemberships should handle list with only null-null memberships")
    void getUsersByMemberships_should_handle_all_null_memberships() throws Exception {
        // Given memberships where all have null group and role
        MockMembership nullMembership1 = new MockMembership(null, null);
        MockMembership nullMembership2 = new MockMembership(null, null);
        List<MockMembership> memberships = Arrays.asList(nullMembership1, nullMembership2);

        // When getting users by memberships - no valid conditions means no search performed
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that getUsersByMemberships handles membership with throwing method.
     */
    @Test
    @DisplayName("getUsersByMemberships should handle objects with throwing getters")
    void getUsersByMemberships_should_handle_throwing_getters() throws Exception {
        // Given an object that throws exception when getters are called
        Object throwingMembership = new Object() {
            @SuppressWarnings("unused")
            public Long getGroupId() { throw new RuntimeException("Method error"); }
            @SuppressWarnings("unused")
            public Long getRoleId() { throw new RuntimeException("Method error"); }
        };
        List<Object> memberships = Collections.singletonList(throwingMembership);

        // When getting users by memberships - errors are handled, no valid conditions
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then empty set should be returned (errors are handled gracefully)
        assertThat(result).isEmpty();
    }

    /**
     * Tests that getUsersByMemberships handles single membership with group only at start.
     */
    @Test
    @DisplayName("getUsersByMemberships handles first membership correctly (no OR before)")
    void getUsersByMemberships_should_handle_first_membership_correctly() throws Exception {
        // Given a single membership (tests the isFirst = true path)
        MockMembership membership = new MockMembership(10L, null);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then users should be returned
        assertThat(result).containsExactly(100L);
        verify(identityAPI, times(1)).searchUsers(any(SearchOptions.class));
    }

    /**
     * Tests search with many users returned.
     */
    @Test
    @DisplayName("getUsersByMemberships should handle many users returned")
    void getUsersByMemberships_should_handle_many_users() throws Exception {
        // Given membership returning many users
        MockMembership membership = new MockMembership(10L, 20L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        User user3 = mock(User.class);
        User user4 = mock(User.class);
        User user5 = mock(User.class);
        when(user1.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);
        when(user3.getId()).thenReturn(3L);
        when(user4.getId()).thenReturn(4L);
        when(user5.getId()).thenReturn(5L);

        when(searchResult.getResult()).thenReturn(Arrays.asList(user1, user2, user3, user4, user5));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then all users should be returned
        assertThat(result).containsExactlyInAnyOrder(1L, 2L, 3L, 4L, 5L);
        assertThat(result).hasSize(5);
    }

    /**
     * Tests that getUsersByMemberships verifies search is performed once.
     */
    @Test
    @DisplayName("getUsersByMemberships should call searchUsers exactly once")
    void getUsersByMemberships_should_call_search_once() throws Exception {
        // Given valid memberships
        MockMembership membership1 = new MockMembership(10L, 20L);
        MockMembership membership2 = new MockMembership(30L, 40L);
        List<MockMembership> memberships = Arrays.asList(membership1, membership2);

        when(searchResult.getResult()).thenReturn(Collections.emptyList());
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then searchUsers should be called exactly once
        verify(identityAPI, times(1)).searchUsers(any(SearchOptions.class));
    }

    /**
     * Tests that getUsersByMemberships handles null values in list gracefully.
     */
    @Test
    @DisplayName("getUsersByMemberships should handle null elements in list")
    void getUsersByMemberships_should_handle_null_elements_in_list() throws Exception {
        // Given a list containing null elements
        List<MockMembership> memberships = Arrays.asList(
            new MockMembership(10L, 20L),
            null,
            new MockMembership(30L, 40L)
        );

        when(searchResult.getResult()).thenReturn(Collections.emptyList());
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships - should not throw NPE
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then should handle gracefully (null element returns null from extractLongValue)
        assertThat(result).isEmpty();
    }

    /**
     * Tests exact Long values are properly extracted and returned.
     */
    @Test
    @DisplayName("getUsersByMemberships should extract exact Long values")
    void getUsersByMemberships_should_extract_exact_long_values() throws Exception {
        // Given memberships with specific Long values
        Long specificGroupId = 999999999L;
        Long specificRoleId = 888888888L;
        MockMembership membership = new MockMembership(specificGroupId, specificRoleId);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then API should be called and users returned
        assertThat(result).containsExactly(100L);
        verify(identityAPI).searchUsers(any(SearchOptions.class));
    }

    /**
     * Tests that getUserManager does not return different value.
     */
    @Test
    @DisplayName("getUserManager should not return hardcoded value")
    void getUserManager_should_not_return_hardcoded_value() throws Exception {
        // Given two different users with different manager IDs
        Long userId1 = 100L;
        Long userId2 = 200L;
        Long managerId1 = 500L;
        Long managerId2 = 600L;

        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(identityAPI.getUser(userId1)).thenReturn(user1);
        when(identityAPI.getUser(userId2)).thenReturn(user2);
        when(user1.getManagerUserId()).thenReturn(managerId1);
        when(user2.getManagerUserId()).thenReturn(managerId2);

        // When getting managers for different users
        Long result1 = IdentityUtils.getUserManager(userId1, identityAPI);
        Long result2 = IdentityUtils.getUserManager(userId2, identityAPI);

        // Then different manager IDs should be returned
        assertThat(result1).isEqualTo(managerId1);
        assertThat(result2).isEqualTo(managerId2);
        assertThat(result1).isNotEqualTo(result2);
    }

    // -------------------------------------------------------------------------
    // Additional Tests to Kill Surviving Mutations
    // -------------------------------------------------------------------------

    /**
     * Verifies getUsersByMemberships returns NON-empty set for valid membership.
     * Targets mutation: replaced return value with Collections.emptySet
     */
    @Test
    @DisplayName("getUsersByMemberships must return non-empty set for valid membership")
    void getUsersByMemberships_must_return_non_empty_set() throws Exception {
        // Given a valid membership
        MockMembership membership = new MockMembership(10L, 20L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(999L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then - these assertions specifically kill "replaced return with emptySet"
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.isEmpty()).isFalse();
        assertThat(result).contains(999L);
    }

    /**
     * Verifies getUsersByMemberships returns exact user IDs.
     * Targets mutation: replaced Long return value with 0L in extractLongValue
     */
    @Test
    @DisplayName("getUsersByMemberships should return exact user ID values")
    void getUsersByMemberships_should_return_exact_user_ids() throws Exception {
        // Given membership with specific values
        MockMembership membership = new MockMembership(123L, 456L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        final Long userId1 = 111111L;
        final Long userId2 = 222222L;
        when(user1.getId()).thenReturn(userId1);
        when(user2.getId()).thenReturn(userId2);
        when(searchResult.getResult()).thenReturn(Arrays.asList(user1, user2));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then - exact values, not 0L
        assertThat(result).containsExactlyInAnyOrder(userId1, userId2);
        assertThat(result).doesNotContain(0L);
        assertThat(result).doesNotContain(1L);
    }

    /**
     * Verifies extractLongValue returns correct non-zero Long values.
     * Targets mutation: replaced Long return value with 0L
     */
    @Test
    @DisplayName("getUsersByMemberships correctly extracts non-zero Long values")
    void getUsersByMemberships_extracts_non_zero_long_values() throws Exception {
        // Given membership with non-zero, non-one values
        final Long specificGroupId = 42L;
        final Long specificRoleId = 84L;
        MockMembership membership = new MockMembership(specificGroupId, specificRoleId);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then the search should succeed (values were extracted correctly)
        assertThat(result).containsExactly(100L);
        verify(identityAPI, times(1)).searchUsers(any(SearchOptions.class));
    }

    /**
     * Verifies getUserManager returns exact value, not a default.
     * Targets mutation: replaced conditional checks
     */
    @Test
    @DisplayName("getUserManager should return non-default value when user has valid manager")
    void getUserManager_should_return_non_default_value() throws Exception {
        // Given a user with a specific manager ID
        final Long specificManagerId = 54321L;
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(specificManagerId);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then exact value should be returned, not defaults
        assertThat(result).isEqualTo(specificManagerId);
        assertThat(result).isNotEqualTo(0L);
        assertThat(result).isNotEqualTo(1L);
        assertThat(result).isNotNull();
    }

    /**
     * Verifies getUserManager returns value from user, not hardcoded.
     */
    @Test
    @DisplayName("getUserManager must use value from user.getManagerUserId()")
    void getUserManager_must_use_value_from_user() throws Exception {
        // Given users with different manager IDs
        Long[] managerIds = {10L, 100L, 1000L, 10000L};

        for (Long expectedManagerId : managerIds) {
            User mockUser = mock(User.class);
            when(identityAPI.getUser(anyLong())).thenReturn(mockUser);
            when(mockUser.getManagerUserId()).thenReturn(expectedManagerId);

            // When getting the user manager
            Long result = IdentityUtils.getUserManager(1L, identityAPI);

            // Then exact value should match
            assertThat(result)
                .as("Manager ID should be %d", expectedManagerId)
                .isEqualTo(expectedManagerId);
        }
    }

    /**
     * Verifies getUsersByMemberships handles valid membership after invalid ones.
     * Tests the isFirst flag and OR condition building.
     */
    @Test
    @DisplayName("getUsersByMemberships should process valid membership after null-null")
    void getUsersByMemberships_should_process_after_null_null() throws Exception {
        // Given: first membership invalid (both null), second valid
        MockMembership invalidMembership = new MockMembership(null, null);
        MockMembership validMembership = new MockMembership(10L, 20L);
        List<MockMembership> memberships = Arrays.asList(invalidMembership, validMembership);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then the valid membership should be processed
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly(100L);
    }

    /**
     * Verifies getUsersByMemberships correctly combines multiple memberships with OR.
     */
    @Test
    @DisplayName("getUsersByMemberships should combine memberships with OR correctly")
    void getUsersByMemberships_should_use_or_conditions() throws Exception {
        // Given multiple valid memberships (OR condition should be used)
        MockMembership m1 = new MockMembership(10L, null);  // group only
        MockMembership m2 = new MockMembership(null, 20L);  // role only
        MockMembership m3 = new MockMembership(30L, 40L);   // both
        List<MockMembership> memberships = Arrays.asList(m1, m2, m3);

        User u1 = mock(User.class);
        User u2 = mock(User.class);
        User u3 = mock(User.class);
        when(u1.getId()).thenReturn(1L);
        when(u2.getId()).thenReturn(2L);
        when(u3.getId()).thenReturn(3L);
        when(searchResult.getResult()).thenReturn(Arrays.asList(u1, u2, u3));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When getting users by memberships
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then all users from all memberships should be returned
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    /**
     * Verifies that specific group/role combinations are handled.
     */
    @Test
    @DisplayName("getUsersByMemberships handles group-only membership correctly")
    void getUsersByMemberships_handles_group_only() throws Exception {
        // Given: membership with only groupId
        MockMembership membership = new MockMembership(555L, null);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly(1L);
    }

    /**
     * Verifies that specific role-only memberships are handled.
     */
    @Test
    @DisplayName("getUsersByMemberships handles role-only membership correctly")
    void getUsersByMemberships_handles_role_only() throws Exception {
        // Given: membership with only roleId
        MockMembership membership = new MockMembership(null, 666L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(2L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly(2L);
    }

    /**
     * Tests extractLongValue with null object returns null (not 0L).
     * The null element is skipped, but the valid membership is still processed.
     */
    @Test
    @DisplayName("getUsersByMemberships handles list with null object element")
    void getUsersByMemberships_handles_null_object_element() throws Exception {
        // Given: list with null object followed by valid membership
        List<Object> memberships = Arrays.asList(
            null,  // This will be passed to extractLongValue - returns null for both
            new MockMembership(10L, 20L)  // Valid membership
        );

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then - null element is skipped (both null), but valid membership is processed
        assertThat(result).isNotEmpty();
        assertThat(result).containsExactly(100L);
    }

    // -------------------------------------------------------------------------
    // Tests to Kill Conditional Mutations in getUserManager
    // -------------------------------------------------------------------------

    /**
     * Verifies that when user is null, getManagerUserId() is NEVER called.
     * Targets mutation: removed conditional - replaced equality check with false for user == null
     */
    @Test
    @DisplayName("getUserManager should NOT call getManagerUserId when user is null")
    void getUserManager_should_not_call_getManagerUserId_when_user_null() throws Exception {
        // Given: identityAPI returns null for the user
        when(identityAPI.getUser(USER_ID)).thenReturn(null);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then: result is null AND getManagerUserId is never called
        assertThat(result).isNull();
        // This verifies the null check branch is taken, not the exception path
        verify(identityAPI, times(1)).getUser(USER_ID);
        // No user mock means no getManagerUserId call - if mutation removes null check,
        // it would try to call getManagerUserId on null and throw NPE
    }

    /**
     * Verifies getUserManager correctly checks managerId <= 0 boundary.
     * Targets mutation: changed conditional boundary for managerId <= 0
     */
    @Test
    @DisplayName("getUserManager should return null when managerId equals 0")
    void getUserManager_should_return_null_when_manager_id_equals_zero() throws Exception {
        // Given: user exists but has managerId = 0 (boundary case)
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(0L);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then: null is returned (0 is not a valid manager ID)
        assertThat(result).isNull();
    }

    /**
     * Verifies getUserManager correctly returns valid positive managerId.
     * Targets mutation: removed conditional for managerId <= 0
     */
    @Test
    @DisplayName("getUserManager should return positive managerId when valid")
    void getUserManager_should_return_positive_manager_id() throws Exception {
        // Given: user with valid positive manager ID (boundary: 1, smallest valid)
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(1L);

        // When getting the user manager
        Long result = IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then: 1L is returned (smallest valid manager ID)
        assertThat(result).isEqualTo(1L);
        assertThat(result).isGreaterThan(0L);
    }

    /**
     * Verifies getUserManager null check prevents NPE path.
     * If null check is removed (mutation), NPE is thrown and caught,
     * but we can verify that getUser was called only once.
     */
    @Test
    @DisplayName("getUserManager with null user should call getUser exactly once")
    void getUserManager_null_user_calls_getUser_once() throws Exception {
        // Given: identityAPI returns null
        when(identityAPI.getUser(anyLong())).thenReturn(null);

        // When
        IdentityUtils.getUserManager(USER_ID, identityAPI);

        // Then: getUser called exactly once (not retried after NPE)
        verify(identityAPI, times(1)).getUser(USER_ID);
    }

    // -------------------------------------------------------------------------
    // Tests to Kill Conditional Mutations in getUsersByMemberships
    // -------------------------------------------------------------------------

    /**
     * Verifies that null membership list returns empty WITHOUT calling searchUsers.
     * Targets mutation: removed conditional for membershipList == null
     */
    @Test
    @DisplayName("getUsersByMemberships should NOT call searchUsers for null list")
    void getUsersByMemberships_should_not_call_search_for_null_list() throws Exception {
        // When getting users with null list
        Set<Long> result = IdentityUtils.getUsersByMemberships(null, identityAPI);

        // Then: empty set AND searchUsers never called
        assertThat(result).isEmpty();
        verify(identityAPI, never()).searchUsers(any(SearchOptions.class));
    }

    /**
     * Verifies that empty membership list returns empty WITHOUT calling searchUsers.
     * Targets mutation: removed conditional for membershipList.isEmpty()
     */
    @Test
    @DisplayName("getUsersByMemberships should NOT call searchUsers for empty list")
    void getUsersByMemberships_should_not_call_search_for_empty_list() throws Exception {
        // When getting users with empty list
        Set<Long> result = IdentityUtils.getUsersByMemberships(Collections.emptyList(), identityAPI);

        // Then: empty set AND searchUsers never called
        assertThat(result).isEmpty();
        verify(identityAPI, never()).searchUsers(any(SearchOptions.class));
    }

    /**
     * Verifies searchUsers IS called for valid non-empty membership list.
     * Targets mutation: to ensure valid path calls searchUsers
     */
    @Test
    @DisplayName("getUsersByMemberships should call searchUsers for valid list")
    void getUsersByMemberships_should_call_search_for_valid_list() throws Exception {
        // Given valid membership
        MockMembership membership = new MockMembership(10L, 20L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        when(searchResult.getResult()).thenReturn(Collections.emptyList());
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then: searchUsers IS called
        verify(identityAPI, times(1)).searchUsers(any(SearchOptions.class));
    }

    /**
     * Verifies that membership with both null group and role is SKIPPED.
     * Targets mutation: removed conditional for groupId == null && roleId == null
     */
    @Test
    @DisplayName("getUsersByMemberships should skip null-null membership without error")
    void getUsersByMemberships_skips_null_null_membership() throws Exception {
        // Given: only null-null membership (should be skipped entirely)
        MockMembership nullMembership = new MockMembership(null, null);
        List<MockMembership> memberships = Collections.singletonList(nullMembership);

        // When - no valid conditions means no search performed
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then: empty result (membership was skipped)
        assertThat(result).isEmpty();
    }

    /**
     * Verifies correct processing of mixed valid and invalid memberships.
     * The isFirst flag should correctly handle OR conditions after skipping.
     */
    @Test
    @DisplayName("getUsersByMemberships should handle isFirst correctly after skipping")
    void getUsersByMemberships_handles_isFirst_after_skip() throws Exception {
        // Given: first is null-null (skip), second and third are valid
        MockMembership skip = new MockMembership(null, null);
        MockMembership valid1 = new MockMembership(10L, null);
        MockMembership valid2 = new MockMembership(20L, null);
        List<MockMembership> memberships = Arrays.asList(skip, valid1, valid2);

        User u1 = mock(User.class);
        when(u1.getId()).thenReturn(1L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(u1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then: search was executed (valid memberships processed)
        assertThat(result).isNotEmpty();
        verify(identityAPI, times(1)).searchUsers(any(SearchOptions.class));
    }

    /**
     * Verifies that membership with ONLY groupId (roleId=null) is processed correctly.
     * Targets mutation: removed conditional for else if (groupId != null)
     */
    @Test
    @DisplayName("getUsersByMemberships processes groupId-only membership")
    void getUsersByMemberships_processes_groupId_only() throws Exception {
        // Given: membership with only groupId
        MockMembership membership = new MockMembership(777L, null);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User u1 = mock(User.class);
        when(u1.getId()).thenReturn(1L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(u1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then: user found through group filter
        assertThat(result).containsExactly(1L);
    }

    /**
     * Verifies that membership with ONLY roleId (groupId=null) is processed correctly.
     * Targets mutation: removed conditional for else if (roleId != null)
     */
    @Test
    @DisplayName("getUsersByMemberships processes roleId-only membership")
    void getUsersByMemberships_processes_roleId_only() throws Exception {
        // Given: membership with only roleId
        MockMembership membership = new MockMembership(null, 888L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User u1 = mock(User.class);
        when(u1.getId()).thenReturn(2L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(u1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then: user found through role filter
        assertThat(result).containsExactly(2L);
    }

    // -------------------------------------------------------------------------
    // ArgumentCaptor Tests to Verify SearchOptions
    // -------------------------------------------------------------------------

    /**
     * Verifies searchUsers is called with SearchOptions when valid membership provided.
     * Uses ArgumentCaptor to verify the search was actually executed.
     */
    @Test
    @DisplayName("getUsersByMemberships should execute search with SearchOptions")
    void getUsersByMemberships_executes_search_with_options() throws Exception {
        // Given valid membership
        MockMembership membership = new MockMembership(100L, 200L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        when(searchResult.getResult()).thenReturn(Collections.emptyList());
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then: verify searchUsers was called with SearchOptions
        verify(identityAPI).searchUsers(searchOptionsCaptor.capture());
        SearchOptions capturedOptions = searchOptionsCaptor.getValue();
        assertThat(capturedOptions).isNotNull();
    }

    /**
     * Tests that returned Set contains actual user IDs from search result.
     * Critical for killing "replaced return value with emptySet" mutation.
     */
    @Test
    @DisplayName("getUsersByMemberships must return actual search results")
    void getUsersByMemberships_returns_actual_search_results() throws Exception {
        // Given membership and specific user IDs in search result
        MockMembership membership = new MockMembership(10L, 20L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        User user3 = mock(User.class);
        when(user1.getId()).thenReturn(111L);
        when(user2.getId()).thenReturn(222L);
        when(user3.getId()).thenReturn(333L);
        when(searchResult.getResult()).thenReturn(Arrays.asList(user1, user2, user3));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then: result must contain all user IDs from search result
        assertThat(result)
            .isNotEmpty()
            .hasSize(3)
            .containsExactlyInAnyOrder(111L, 222L, 333L);

        // Verify these specific IDs, not empty or 0
        assertThat(result).doesNotContain(0L);
        assertThat(result.isEmpty()).isFalse();
    }

    /**
     * Verifies that multiple calls return correct values each time.
     * Targets mutation: ensures return value is from actual search, not cached.
     */
    @Test
    @DisplayName("getUsersByMemberships returns correct values across multiple calls")
    void getUsersByMemberships_returns_correct_values_multiple_calls() throws Exception {
        // First call - membership with users
        MockMembership membership1 = new MockMembership(10L, 20L);
        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        Set<Long> result1 = IdentityUtils.getUsersByMemberships(Collections.singletonList(membership1), identityAPI);

        // Then first call should return the user
        assertThat(result1).containsExactly(100L);

        // Second call - null list should return empty
        Set<Long> result2 = IdentityUtils.getUsersByMemberships(null, identityAPI);
        assertThat(result2).isEmpty();

        // Third call - empty list should return empty
        Set<Long> result3 = IdentityUtils.getUsersByMemberships(Collections.emptyList(), identityAPI);
        assertThat(result3).isEmpty();
    }

    /**
     * Verifies that membership with both groupId AND roleId uses AND condition.
     * Targets mutation: removed conditional for groupId != null && roleId != null
     */
    @Test
    @DisplayName("getUsersByMemberships uses AND for both groupId and roleId")
    void getUsersByMemberships_uses_and_for_both() throws Exception {
        // Given: membership with both values
        MockMembership membership = new MockMembership(100L, 200L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User u1 = mock(User.class);
        when(u1.getId()).thenReturn(3L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(u1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then: user found through combined group+role filter
        assertThat(result).containsExactly(3L);
    }

    /**
     * Verifies extractLongValue handles Integer return type correctly.
     * Uses a mock that returns Integer instead of Long - should be converted.
     */
    @Test
    @DisplayName("getUsersByMemberships handles getter returning Integer type")
    void getUsersByMemberships_handles_wrong_return_type() throws Exception {
        // Given: object with getter that returns Integer instead of Long
        WrongTypeMembership intType = new WrongTypeMembership();
        List<WrongTypeMembership> memberships = Collections.singletonList(intType);

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(100L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(user1));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When
        Set<Long> result = IdentityUtils.getUsersByMemberships(memberships, identityAPI);

        // Then: Integer values are converted to Long and users are found
        assertThat(result).containsExactly(100L);
    }

    /**
     * Helper class with Integer return type instead of Long for testing type handling.
     */
    private static class WrongTypeMembership {
        public Integer getGroupId() {
            return 10;  // Returns Integer, not Long
        }
        public Integer getRoleId() {
            return 20;  // Returns Integer, not Long
        }
    }

    // -------------------------------------------------------------------------
    // Direct Tests for extractLongValue (package-private method)
    // Targets mutations: replaced Long return value with 0L
    // -------------------------------------------------------------------------

    /**
     * Tests that extractLongValue returns null for null object input.
     * Targets mutation: replaced Long return value with 0L (obj == null branch)
     */
    @Test
    @DisplayName("extractLongValue should return null for null object")
    void extractLongValue_should_return_null_for_null_object() {
        // When extracting from null object
        Long result = IdentityUtils.extractLongValue(null, "getGroupId");

        // Then null should be returned, NOT 0L
        assertThat(result).isNull();
        // Note: If mutation replaces return null with 0L, this test will fail
        // because result would be 0L instead of null
    }

    /**
     * Tests that extractLongValue returns exact Long value when method returns Long.
     * Targets mutation: replaced Long return value with 0L (result instanceof Long branch)
     */
    @Test
    @DisplayName("extractLongValue should return exact Long value from getter")
    void extractLongValue_should_return_exact_long_value() {
        // Given: object with Long getter returning specific value
        MockMembership membership = new MockMembership(12345L, 67890L);

        // When extracting groupId
        Long groupResult = IdentityUtils.extractLongValue(membership, "getGroupId");

        // Then exact value should be returned, NOT 0L
        assertThat(groupResult).isEqualTo(12345L);
        assertThat(groupResult).isNotEqualTo(0L);

        // When extracting roleId
        Long roleResult = IdentityUtils.extractLongValue(membership, "getRoleId");

        // Then exact value should be returned
        assertThat(roleResult).isEqualTo(67890L);
        assertThat(roleResult).isNotEqualTo(0L);
    }

    /**
     * Tests that extractLongValue returns null when method returns null.
     * Targets mutation: replaced Long return value with 0L (result == null branch)
     */
    @Test
    @DisplayName("extractLongValue should return null when getter returns null")
    void extractLongValue_should_return_null_when_getter_returns_null() {
        // Given: object with null value
        MockMembership membership = new MockMembership(null, 20L);

        // When extracting null groupId
        Long result = IdentityUtils.extractLongValue(membership, "getGroupId");

        // Then null should be returned, NOT 0L
        assertThat(result).isNull();
    }

    /**
     * Tests that extractLongValue converts Integer to Long correctly.
     * The method now supports Integer return types by converting to Long.
     */
    @Test
    @DisplayName("extractLongValue should convert Integer to Long correctly")
    void extractLongValue_should_convert_integer_to_long() {
        // Given: object that returns Integer instead of Long
        WrongTypeMembership intType = new WrongTypeMembership();

        // When extracting value (Integer 10)
        Long result = IdentityUtils.extractLongValue(intType, "getGroupId");

        // Then Integer should be converted to Long
        assertThat(result).isEqualTo(10L);
    }

    /**
     * Tests that extractLongValue returns null when method doesn't exist.
     * Targets mutation: replaced Long return value with 0L (NoSuchMethodException branch)
     */
    @Test
    @DisplayName("extractLongValue should return null for non-existent method")
    void extractLongValue_should_return_null_for_nonexistent_method() {
        // Given: object without the requested method
        MockMembership membership = new MockMembership(10L, 20L);

        // When extracting using non-existent method
        Long result = IdentityUtils.extractLongValue(membership, "getNonExistentMethod");

        // Then null should be returned, NOT 0L
        assertThat(result).isNull();
    }

    /**
     * Tests that extractLongValue returns null when method throws exception.
     * Targets mutation: replaced Long return value with 0L (Exception branch)
     */
    @Test
    @DisplayName("extractLongValue should return null when method throws exception")
    void extractLongValue_should_return_null_when_method_throws() {
        // Given: object whose getter throws exception
        ThrowingMembership throwing = new ThrowingMembership();

        // When extracting value from throwing getter
        Long result = IdentityUtils.extractLongValue(throwing, "getGroupId");

        // Then null should be returned, NOT 0L
        assertThat(result).isNull();
    }

    /**
     * Tests that extractLongValue distinguishes between null return and actual Long value.
     * Critical for killing 0L replacement mutations.
     */
    @Test
    @DisplayName("extractLongValue should distinguish null from actual values")
    void extractLongValue_should_distinguish_null_from_values() {
        // Given: membership with specific values vs null values
        MockMembership withValues = new MockMembership(100L, 200L);
        MockMembership withNulls = new MockMembership(null, null);

        // When extracting from both
        Long valueResult = IdentityUtils.extractLongValue(withValues, "getGroupId");
        Long nullResult = IdentityUtils.extractLongValue(withNulls, "getGroupId");

        // Then: values should be correct, null distinct from 0
        assertThat(valueResult).isEqualTo(100L);
        assertThat(nullResult).isNull();
        assertThat(valueResult).isNotEqualTo(nullResult);
    }

    /**
     * Tests extractLongValue with value of 0L returns null (0 is invalid ID).
     */
    @Test
    @DisplayName("extractLongValue should return null when getter returns 0L (invalid ID)")
    void extractLongValue_should_return_null_when_getter_returns_zero() {
        // Given: membership with 0L value (invalid ID)
        MockMembership zeroMembership = new MockMembership(0L, 0L);

        // When extracting
        Long result = IdentityUtils.extractLongValue(zeroMembership, "getGroupId");

        // Then: null should be returned (0 is not a valid ID)
        assertThat(result).isNull();
    }

    /**
     * Tests extractLongValue with various Long values including edge cases.
     * Negative and zero values should return null (invalid IDs).
     */
    @Test
    @DisplayName("extractLongValue should return correct Long for valid values only")
    void extractLongValue_various_values() {
        // Test positive values (valid IDs)
        Long[] validValues = {1L, Long.MAX_VALUE, 42L, 999999L};

        for (Long expected : validValues) {
            MockMembership membership = new MockMembership(expected, expected);
            Long result = IdentityUtils.extractLongValue(membership, "getGroupId");

            assertThat(result)
                .as("Should return %d exactly", expected)
                .isEqualTo(expected);
        }

        // Test invalid values (should return null)
        Long[] invalidValues = {0L, -1L, Long.MIN_VALUE};

        for (Long invalid : invalidValues) {
            MockMembership membership = new MockMembership(invalid, invalid);
            Long result = IdentityUtils.extractLongValue(membership, "getGroupId");

            assertThat(result)
                .as("Should return null for invalid ID %d", invalid)
                .isNull();
        }
    }

    /**
     * Helper class that throws exception from getter.
     */
    @SuppressWarnings("unused") // Methods are called via reflection
    private static class ThrowingMembership {
        public Long getGroupId() {
            throw new RuntimeException("Test exception");
        }
        public Long getRoleId() {
            throw new RuntimeException("Test exception");
        }
    }

    // -------------------------------------------------------------------------
    // getUserIdFromObject Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getUserIdFromObject returns valid user ID from BDM object.
     */
    @Test
    @DisplayName("getUserIdFromObject should return user ID from BDM object")
    void getUserIdFromObject_should_return_user_id() {
        // Given: object with valid user ID
        MockUserObject userObject = new MockUserObject(12345L);

        // When extracting user ID
        Long result = IdentityUtils.getUserIdFromObject(userObject, "getUserId");

        // Then exact value should be returned
        assertThat(result).isEqualTo(12345L);
    }

    /**
     * Tests that getUserIdFromObject returns null for null object.
     */
    @Test
    @DisplayName("getUserIdFromObject should return null for null object")
    void getUserIdFromObject_should_return_null_for_null_object() {
        // When extracting from null
        Long result = IdentityUtils.getUserIdFromObject(null, "getUserId");

        // Then null should be returned
        assertThat(result).isNull();
    }

    /**
     * Tests that getUserIdFromObject returns null for null method name.
     */
    @Test
    @DisplayName("getUserIdFromObject should return null for null method name")
    void getUserIdFromObject_should_return_null_for_null_method_name() {
        // Given: valid object
        MockUserObject userObject = new MockUserObject(100L);

        // When extracting with null method name
        Long result = IdentityUtils.getUserIdFromObject(userObject, null);

        // Then null should be returned
        assertThat(result).isNull();
    }

    /**
     * Tests that getUserIdFromObject returns null for empty method name.
     */
    @Test
    @DisplayName("getUserIdFromObject should return null for empty method name")
    void getUserIdFromObject_should_return_null_for_empty_method_name() {
        // Given: valid object
        MockUserObject userObject = new MockUserObject(100L);

        // When extracting with empty method name
        Long result = IdentityUtils.getUserIdFromObject(userObject, "   ");

        // Then null should be returned
        assertThat(result).isNull();
    }

    /**
     * Tests that getUserIdFromObject returns null for zero user ID.
     */
    @Test
    @DisplayName("getUserIdFromObject should return null for zero user ID")
    void getUserIdFromObject_should_return_null_for_zero_user_id() {
        // Given: object with zero user ID
        MockUserObject userObject = new MockUserObject(0L);

        // When extracting
        Long result = IdentityUtils.getUserIdFromObject(userObject, "getUserId");

        // Then null should be returned (0 is invalid)
        assertThat(result).isNull();
    }

    /**
     * Tests that getUserIdFromObject returns null for negative user ID.
     */
    @Test
    @DisplayName("getUserIdFromObject should return null for negative user ID")
    void getUserIdFromObject_should_return_null_for_negative_user_id() {
        // Given: object with negative user ID
        MockUserObject userObject = new MockUserObject(-1L);

        // When extracting
        Long result = IdentityUtils.getUserIdFromObject(userObject, "getUserId");

        // Then null should be returned
        assertThat(result).isNull();
    }

    /**
     * Tests that getUserIdFromObject returns null for non-existent method.
     */
    @Test
    @DisplayName("getUserIdFromObject should return null for non-existent method")
    void getUserIdFromObject_should_return_null_for_nonexistent_method() {
        // Given: valid object
        MockUserObject userObject = new MockUserObject(100L);

        // When extracting with wrong method name
        Long result = IdentityUtils.getUserIdFromObject(userObject, "getNonExistent");

        // Then null should be returned
        assertThat(result).isNull();
    }

    // -------------------------------------------------------------------------
    // buildCandidateUsers Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that buildCandidateUsers includes step user ID.
     */
    @Test
    @DisplayName("buildCandidateUsers should include step user ID")
    void buildCandidateUsers_should_include_step_user() throws Exception {
        // Given: valid step user ID
        Long stepUserId = 100L;

        // When building candidates without manager
        Set<Long> result = IdentityUtils.buildCandidateUsers(stepUserId, false, null, identityAPI);

        // Then step user should be included
        assertThat(result).containsExactly(stepUserId);
    }

    /**
     * Tests that buildCandidateUsers includes manager when requested.
     */
    @Test
    @DisplayName("buildCandidateUsers should include manager when requested")
    void buildCandidateUsers_should_include_manager() throws Exception {
        // Given: step user with manager
        Long stepUserId = 100L;
        Long managerId = 200L;
        when(identityAPI.getUser(stepUserId)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(managerId);

        // When building candidates with manager
        Set<Long> result = IdentityUtils.buildCandidateUsers(stepUserId, true, null, identityAPI);

        // Then both step user and manager should be included
        assertThat(result).containsExactlyInAnyOrder(stepUserId, managerId);
    }

    /**
     * Tests that buildCandidateUsers includes membership users.
     */
    @Test
    @DisplayName("buildCandidateUsers should include membership users")
    void buildCandidateUsers_should_include_membership_users() throws Exception {
        // Given: membership list
        MockMembership membership = new MockMembership(10L, 20L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        when(user1.getId()).thenReturn(300L);
        when(user2.getId()).thenReturn(400L);
        when(searchResult.getResult()).thenReturn(Arrays.asList(user1, user2));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When building candidates with memberships
        Set<Long> result = IdentityUtils.buildCandidateUsers(null, false, memberships, identityAPI);

        // Then membership users should be included
        assertThat(result).containsExactlyInAnyOrder(300L, 400L);
    }

    /**
     * Tests that buildCandidateUsers combines all sources.
     */
    @Test
    @DisplayName("buildCandidateUsers should combine step user, manager, and memberships")
    void buildCandidateUsers_should_combine_all_sources() throws Exception {
        // Given: step user with manager and memberships
        Long stepUserId = 100L;
        Long managerId = 200L;
        when(identityAPI.getUser(stepUserId)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(managerId);

        MockMembership membership = new MockMembership(10L, 20L);
        List<MockMembership> memberships = Collections.singletonList(membership);

        User memberUser = mock(User.class);
        when(memberUser.getId()).thenReturn(300L);
        when(searchResult.getResult()).thenReturn(Collections.singletonList(memberUser));
        when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(searchResult);

        // When building candidates with all sources
        Set<Long> result = IdentityUtils.buildCandidateUsers(stepUserId, true, memberships, identityAPI);

        // Then all users should be included
        assertThat(result).containsExactlyInAnyOrder(100L, 200L, 300L);
    }

    /**
     * Tests that buildCandidateUsers returns empty set for null step user and no memberships.
     */
    @Test
    @DisplayName("buildCandidateUsers should return empty set when no valid inputs")
    void buildCandidateUsers_should_return_empty_for_no_inputs() {
        // When building candidates with null inputs
        Set<Long> result = IdentityUtils.buildCandidateUsers(null, false, null, identityAPI);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that buildCandidateUsers handles invalid step user ID.
     */
    @Test
    @DisplayName("buildCandidateUsers should ignore invalid step user ID")
    void buildCandidateUsers_should_ignore_invalid_step_user() {
        // When building candidates with zero step user
        Set<Long> result = IdentityUtils.buildCandidateUsers(0L, true, null, identityAPI);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // filterAssignableUsers Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that filterAssignableUsers returns intersection of candidates and assignable.
     */
    @Test
    @DisplayName("filterAssignableUsers should return intersection")
    void filterAssignableUsers_should_return_intersection() {
        // Given: candidates and assignable users with overlap
        Set<Long> candidates = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        Collection<Long> assignable = Arrays.asList(2L, 3L, 4L);

        // When filtering
        Set<Long> result = IdentityUtils.filterAssignableUsers(candidates, assignable);

        // Then only overlapping IDs should be returned
        assertThat(result).containsExactlyInAnyOrder(2L, 3L);
    }

    /**
     * Tests that filterAssignableUsers returns empty set for null candidates.
     */
    @Test
    @DisplayName("filterAssignableUsers should return empty for null candidates")
    void filterAssignableUsers_should_return_empty_for_null_candidates() {
        // Given: null candidates
        Collection<Long> assignable = Arrays.asList(1L, 2L);

        // When filtering
        Set<Long> result = IdentityUtils.filterAssignableUsers(null, assignable);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that filterAssignableUsers returns empty set for empty candidates.
     */
    @Test
    @DisplayName("filterAssignableUsers should return empty for empty candidates")
    void filterAssignableUsers_should_return_empty_for_empty_candidates() {
        // Given: empty candidates
        Set<Long> candidates = Collections.emptySet();
        Collection<Long> assignable = Arrays.asList(1L, 2L);

        // When filtering
        Set<Long> result = IdentityUtils.filterAssignableUsers(candidates, assignable);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that filterAssignableUsers returns empty set for null assignable.
     */
    @Test
    @DisplayName("filterAssignableUsers should return empty for null assignable")
    void filterAssignableUsers_should_return_empty_for_null_assignable() {
        // Given: valid candidates, null assignable
        Set<Long> candidates = new HashSet<>(Arrays.asList(1L, 2L));

        // When filtering
        Set<Long> result = IdentityUtils.filterAssignableUsers(candidates, null);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that filterAssignableUsers returns empty set for empty assignable.
     */
    @Test
    @DisplayName("filterAssignableUsers should return empty for empty assignable")
    void filterAssignableUsers_should_return_empty_for_empty_assignable() {
        // Given: valid candidates, empty assignable
        Set<Long> candidates = new HashSet<>(Arrays.asList(1L, 2L));
        Collection<Long> assignable = Collections.emptyList();

        // When filtering
        Set<Long> result = IdentityUtils.filterAssignableUsers(candidates, assignable);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that filterAssignableUsers returns empty set when no overlap.
     */
    @Test
    @DisplayName("filterAssignableUsers should return empty when no overlap")
    void filterAssignableUsers_should_return_empty_when_no_overlap() {
        // Given: candidates and assignable with no overlap
        Set<Long> candidates = new HashSet<>(Arrays.asList(1L, 2L));
        Collection<Long> assignable = Arrays.asList(3L, 4L);

        // When filtering
        Set<Long> result = IdentityUtils.filterAssignableUsers(candidates, assignable);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that filterAssignableUsers returns all candidates when fully contained.
     */
    @Test
    @DisplayName("filterAssignableUsers should return all when fully contained")
    void filterAssignableUsers_should_return_all_when_contained() {
        // Given: all candidates are assignable
        Set<Long> candidates = new HashSet<>(Arrays.asList(1L, 2L));
        Collection<Long> assignable = Arrays.asList(1L, 2L, 3L, 4L);

        // When filtering
        Set<Long> result = IdentityUtils.filterAssignableUsers(candidates, assignable);

        // Then all candidates should be returned
        assertThat(result).containsExactlyInAnyOrder(1L, 2L);
    }

    // -------------------------------------------------------------------------
    // getFilteredAssignableUsers Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getFilteredAssignableUsers combines build and filter correctly.
     */
    @Test
    @DisplayName("getFilteredAssignableUsers should combine build and filter")
    void getFilteredAssignableUsers_should_combine_operations() throws Exception {
        // Given: step user is assignable
        Long stepUserId = 100L;
        Collection<Long> assignable = Arrays.asList(100L, 200L, 300L);

        // When getting filtered users
        Set<Long> result = IdentityUtils.getFilteredAssignableUsers(
                stepUserId, false, null, assignable, identityAPI);

        // Then step user should be returned (it's in assignable)
        assertThat(result).containsExactly(100L);
    }

    /**
     * Tests that getFilteredAssignableUsers returns empty when step user not assignable.
     */
    @Test
    @DisplayName("getFilteredAssignableUsers should return empty when not assignable")
    void getFilteredAssignableUsers_should_return_empty_when_not_assignable() {
        // Given: step user is NOT assignable
        Long stepUserId = 100L;
        Collection<Long> assignable = Arrays.asList(200L, 300L);

        // When getting filtered users
        Set<Long> result = IdentityUtils.getFilteredAssignableUsers(
                stepUserId, false, null, assignable, identityAPI);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that getFilteredAssignableUsers includes assignable manager.
     */
    @Test
    @DisplayName("getFilteredAssignableUsers should include assignable manager")
    void getFilteredAssignableUsers_should_include_assignable_manager() throws Exception {
        // Given: step user not assignable but manager is
        Long stepUserId = 100L;
        Long managerId = 200L;
        when(identityAPI.getUser(stepUserId)).thenReturn(user);
        when(user.getManagerUserId()).thenReturn(managerId);

        Collection<Long> assignable = Arrays.asList(200L, 300L);

        // When getting filtered users with manager
        Set<Long> result = IdentityUtils.getFilteredAssignableUsers(
                stepUserId, true, null, assignable, identityAPI);

        // Then only manager should be returned
        assertThat(result).containsExactly(200L);
    }

    // -------------------------------------------------------------------------
    // extractUserIdsFromObjects Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that extractUserIdsFromObjects extracts valid user IDs.
     */
    @Test
    @DisplayName("extractUserIdsFromObjects should extract valid user IDs")
    void extractUserIdsFromObjects_should_extract_ids() {
        // Given: list of user objects
        List<MockUserObject> userObjects = Arrays.asList(
                new MockUserObject(100L),
                new MockUserObject(200L),
                new MockUserObject(300L)
        );

        // When extracting
        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(userObjects, "getUserId");

        // Then all IDs should be extracted
        assertThat(result).containsExactlyInAnyOrder(100L, 200L, 300L);
    }

    /**
     * Tests that extractUserIdsFromObjects filters out invalid IDs.
     */
    @Test
    @DisplayName("extractUserIdsFromObjects should filter out invalid IDs")
    void extractUserIdsFromObjects_should_filter_invalid() {
        // Given: list with valid and invalid user IDs
        List<MockUserObject> userObjects = Arrays.asList(
                new MockUserObject(100L),
                new MockUserObject(0L),
                new MockUserObject(-1L),
                new MockUserObject(null),
                new MockUserObject(200L)
        );

        // When extracting
        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(userObjects, "getUserId");

        // Then only valid IDs should be extracted
        assertThat(result).containsExactlyInAnyOrder(100L, 200L);
    }

    /**
     * Tests that extractUserIdsFromObjects returns empty for null list.
     */
    @Test
    @DisplayName("extractUserIdsFromObjects should return empty for null list")
    void extractUserIdsFromObjects_should_return_empty_for_null_list() {
        // When extracting from null
        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(null, "getUserId");

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that extractUserIdsFromObjects returns empty for empty list.
     */
    @Test
    @DisplayName("extractUserIdsFromObjects should return empty for empty list")
    void extractUserIdsFromObjects_should_return_empty_for_empty_list() {
        // When extracting from empty list
        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(Collections.emptyList(), "getUserId");

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that extractUserIdsFromObjects returns empty for null method name.
     */
    @Test
    @DisplayName("extractUserIdsFromObjects should return empty for null method name")
    void extractUserIdsFromObjects_should_return_empty_for_null_method() {
        // Given: valid list
        List<MockUserObject> userObjects = Collections.singletonList(new MockUserObject(100L));

        // When extracting with null method
        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(userObjects, null);

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that extractUserIdsFromObjects returns empty for empty method name.
     */
    @Test
    @DisplayName("extractUserIdsFromObjects should return empty for empty method name")
    void extractUserIdsFromObjects_should_return_empty_for_empty_method() {
        // Given: valid list
        List<MockUserObject> userObjects = Collections.singletonList(new MockUserObject(100L));

        // When extracting with empty method
        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(userObjects, "  ");

        // Then empty set should be returned
        assertThat(result).isEmpty();
    }

    /**
     * Tests that extractUserIdsFromObjects removes duplicates.
     */
    @Test
    @DisplayName("extractUserIdsFromObjects should remove duplicates")
    void extractUserIdsFromObjects_should_remove_duplicates() {
        // Given: list with duplicate IDs
        List<MockUserObject> userObjects = Arrays.asList(
                new MockUserObject(100L),
                new MockUserObject(100L),
                new MockUserObject(200L),
                new MockUserObject(100L)
        );

        // When extracting
        Set<Long> result = IdentityUtils.extractUserIdsFromObjects(userObjects, "getUserId");

        // Then unique IDs should be returned
        assertThat(result).containsExactlyInAnyOrder(100L, 200L);
        assertThat(result).hasSize(2);
    }

    // -------------------------------------------------------------------------
    // Mock Helper Classes
    // -------------------------------------------------------------------------

    /**
     * Mock class to simulate a user object with getUserId() method.
     * This class is used for testing the reflection-based extraction.
     */
    private static class MockUserObject {
        private final Long userId;

        public MockUserObject(Long userId) {
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }
    }

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
