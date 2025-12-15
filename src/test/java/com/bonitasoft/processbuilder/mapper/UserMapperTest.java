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
}
