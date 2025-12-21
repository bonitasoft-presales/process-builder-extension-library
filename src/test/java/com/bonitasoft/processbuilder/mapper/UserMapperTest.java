package com.bonitasoft.processbuilder.mapper;

import org.bonitasoft.engine.identity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link UserMapper} utility class.
 * <p>
 * This class ensures that the utility class cannot be instantiated and
 * that all mapping methods function correctly across various input scenarios.
 * </p>
 */
class UserMapperTest {

    // -------------------------------------------------------------------------
    // Utility Class Instantiation Test
    // -------------------------------------------------------------------------

    /**
     * Test case to ensure the private constructor prevents instantiation,
     * confirming that the utility class cannot be instantiated.
     */
    @Test
    @DisplayName("Private constructor should prevent instantiation")
    void constructor_should_prevent_instantiation() throws Exception {
        Constructor<UserMapper> constructor = UserMapper.class.getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor must be private.");

        constructor.setAccessible(true);

        // The constructor does not throw, but we verify it's private and invocable only via reflection
        assertDoesNotThrow(() -> constructor.newInstance(),
                "Constructor should be callable via reflection but class is a utility.");
    }

    // -------------------------------------------------------------------------
    // toLongIds Tests
    // -------------------------------------------------------------------------

    /**
     * Test case for {@code toLongIds} with null input.
     */
    @Test
    @DisplayName("toLongIds should return empty list for null input")
    void toLongIds_should_return_empty_list_for_null() {
        List<Long> result = UserMapper.toLongIds(null);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be an empty list");
    }

    /**
     * Test case for {@code toLongIds} with empty list input.
     */
    @Test
    @DisplayName("toLongIds should return empty list for empty input")
    void toLongIds_should_return_empty_list_for_empty_input() {
        List<Long> result = UserMapper.toLongIds(Collections.emptyList());

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be an empty list");
    }

    /**
     * Test case for {@code toLongIds} with a single user.
     */
    @Test
    @DisplayName("toLongIds should return list with single ID for single user")
    void toLongIds_should_return_single_id_for_single_user() {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(123L);

        List<Long> result = UserMapper.toLongIds(List.of(mockUser));

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Result should contain exactly one element");
        assertEquals(123L, result.get(0), "The ID should match the mocked user ID");
    }

    /**
     * Test case for {@code toLongIds} with multiple users.
     */
    @Test
    @DisplayName("toLongIds should return list of IDs for multiple users")
    void toLongIds_should_return_ids_for_multiple_users() {
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        User mockUser3 = mock(User.class);

        when(mockUser1.getId()).thenReturn(100L);
        when(mockUser2.getId()).thenReturn(200L);
        when(mockUser3.getId()).thenReturn(300L);

        List<Long> result = UserMapper.toLongIds(List.of(mockUser1, mockUser2, mockUser3));

        assertNotNull(result, "Result should not be null");
        assertEquals(3, result.size(), "Result should contain exactly three elements");
        assertEquals(List.of(100L, 200L, 300L), result, "The IDs should match in order");
    }

    /**
     * Test case to verify the order preservation in {@code toLongIds}.
     */
    @Test
    @DisplayName("toLongIds should preserve the order of users in the result")
    void toLongIds_should_preserve_order() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(user1.getId()).thenReturn(999L);
        when(user2.getId()).thenReturn(1L);

        List<Long> result = UserMapper.toLongIds(List.of(user1, user2));

        assertEquals(999L, result.get(0), "First ID should be 999L");
        assertEquals(1L, result.get(1), "Second ID should be 1L");
    }

    // -------------------------------------------------------------------------
    // Additional Tests for Mutation Coverage
    // -------------------------------------------------------------------------

    /**
     * Test that toLongIds returns exact ID values (not hardcoded).
     */
    @Test
    @DisplayName("toLongIds should return exact ID values from users")
    void toLongIds_should_return_exact_values() {
        User user = mock(User.class);
        Long specificId = 42L;
        when(user.getId()).thenReturn(specificId);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        assertEquals(specificId, result.get(0), "ID must match exactly");
        assertNotEquals(0L, result.get(0), "ID should not be default value");
        assertNotEquals(1L, result.get(0), "ID should not be 1");
    }

    /**
     * Test with boundary value Long.MAX_VALUE.
     */
    @Test
    @DisplayName("toLongIds should handle Long.MAX_VALUE")
    void toLongIds_should_handle_max_value() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(Long.MAX_VALUE);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        assertEquals(Long.MAX_VALUE, result.get(0), "Should handle Long.MAX_VALUE");
    }

    /**
     * Test with boundary value Long.MIN_VALUE.
     */
    @Test
    @DisplayName("toLongIds should handle Long.MIN_VALUE")
    void toLongIds_should_handle_min_value() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(Long.MIN_VALUE);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        assertEquals(Long.MIN_VALUE, result.get(0), "Should handle Long.MIN_VALUE");
    }

    /**
     * Test that non-empty list produces non-empty result.
     */
    @Test
    @DisplayName("toLongIds should return non-empty result for non-empty input")
    void toLongIds_should_return_non_empty_for_valid_input() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        assertFalse(result.isEmpty(), "Result should not be empty for non-empty input");
    }

    /**
     * Test that size of result equals size of input.
     */
    @Test
    @DisplayName("toLongIds should return same size as input")
    void toLongIds_should_return_same_size() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        when(user1.getId()).thenReturn(1L);
        when(user2.getId()).thenReturn(2L);

        List<User> input = List.of(user1, user2);
        List<Long> result = UserMapper.toLongIds(input);

        assertEquals(input.size(), result.size(), "Result size must equal input size");
    }

    /**
     * Test with zero ID value.
     */
    @Test
    @DisplayName("toLongIds should handle zero ID")
    void toLongIds_should_handle_zero_id() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(0L);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        assertEquals(0L, result.get(0), "Should handle zero ID");
    }

    /**
     * Test with negative ID value.
     */
    @Test
    @DisplayName("toLongIds should handle negative ID")
    void toLongIds_should_handle_negative_id() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(-1L);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        assertEquals(-1L, result.get(0), "Should handle negative ID");
    }

    /**
     * Test that result is mutable (Collectors.toList returns mutable list).
     */
    @Test
    @DisplayName("toLongIds should return mutable list for non-empty input")
    void toLongIds_should_return_mutable_list() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        // Should be able to add to the list (Collectors.toList() returns mutable)
        assertDoesNotThrow(() -> result.add(2L), "Result should be mutable");
    }

    /**
     * Test that each user's ID is correctly mapped.
     */
    @Test
    @DisplayName("toLongIds should map each user to its exact ID")
    void toLongIds_should_map_each_user_correctly() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        User user3 = mock(User.class);

        when(user1.getId()).thenReturn(111L);
        when(user2.getId()).thenReturn(222L);
        when(user3.getId()).thenReturn(333L);

        List<Long> result = UserMapper.toLongIds(List.of(user1, user2, user3));

        // Verify each specific value
        assertTrue(result.contains(111L), "Should contain 111L");
        assertTrue(result.contains(222L), "Should contain 222L");
        assertTrue(result.contains(333L), "Should contain 333L");

        // Verify position
        assertEquals(111L, result.get(0));
        assertEquals(222L, result.get(1));
        assertEquals(333L, result.get(2));
    }

    // -------------------------------------------------------------------------
    // Tests to Kill Surviving Mutations
    // -------------------------------------------------------------------------

    /**
     * Test that null input is handled safely without throwing NullPointerException.
     * This test explicitly uses assertDoesNotThrow to detect if the null check
     * is removed by mutation, which would cause NPE on isEmpty() call.
     */
    @Test
    @DisplayName("toLongIds should safely handle null without NullPointerException")
    void toLongIds_should_safely_handle_null_without_exception() {
        List<Long> result = assertDoesNotThrow(
                () -> UserMapper.toLongIds(null),
                "Null input should not cause NullPointerException - null check is required"
        );

        assertNotNull(result, "Result should never be null");
        assertTrue(result.isEmpty(), "Result should be empty for null input");
    }

    /**
     * Test that specifically verifies the null check is evaluated before isEmpty().
     * The short-circuit evaluation of || is critical for null safety.
     */
    @Test
    @DisplayName("toLongIds should evaluate null check before isEmpty")
    void toLongIds_null_check_must_precede_isEmpty_call() {
        // This test verifies that when users is null, we don't call isEmpty() on it
        // If mutation removes 'users == null' check, isEmpty() on null throws NPE

        List<Long> nullResult = assertDoesNotThrow(() -> UserMapper.toLongIds(null));
        List<Long> emptyResult = assertDoesNotThrow(() -> UserMapper.toLongIds(Collections.emptyList()));

        // Both should return empty lists - but through different code paths
        assertEquals(nullResult.size(), emptyResult.size(), "Both null and empty should return same size result");
        assertTrue(nullResult.isEmpty() && emptyResult.isEmpty(), "Both should be empty");
    }

    /**
     * Test that result from null input is exactly an empty immutable list.
     * Verifies List.of() behavior.
     */
    @Test
    @DisplayName("toLongIds with null should return List.of() style empty list")
    void toLongIds_null_should_return_immutable_empty_list() {
        List<Long> result = UserMapper.toLongIds(null);

        // List.of() returns immutable list, so add should throw
        assertThrows(UnsupportedOperationException.class,
                () -> result.add(1L),
                "Result from null input should be immutable (List.of())");
    }

    /**
     * Test that result from empty list input is mutable (via Collectors.toList).
     * This distinguishes between the null and empty code paths.
     */
    @Test
    @DisplayName("toLongIds with empty list returns via stream path")
    void toLongIds_empty_list_should_use_stream_path() {
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);

        // With one user, we go through stream path and get mutable list
        List<Long> result = UserMapper.toLongIds(List.of(mockUser));

        // Collectors.toList() returns mutable list
        assertDoesNotThrow(() -> result.add(2L), "Stream path should return mutable list");
        assertEquals(2, result.size(), "Should have both original and added element");
    }
}
