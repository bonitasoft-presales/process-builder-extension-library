package com.bonitasoft.processbuilder.mapper;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import org.bonitasoft.engine.identity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Property-based tests for {@link UserMapper} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("UserMapper Property-Based Tests")
class UserMapperPropertyTest {

    // =========================================================================
    // UTILITY CLASS INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Utility class should not be instantiable")
    void utilityClassShouldNotBeInstantiable() {
        assertThatCode(() -> {
            var constructor = UserMapper.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).doesNotThrowAnyException(); // Constructor doesn't throw, just prevents instantiation
    }

    // =========================================================================
    // toLongIds() SIZE INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("toLongIds should return list with same size as input")
    void toLongIdsShouldReturnSameSizeAsInput(
            @ForAll @Size(min = 1, max = 100) List<@LongRange(min = 1, max = 10000) Long> ids) {
        List<User> users = createMockUsers(ids);

        List<Long> result = UserMapper.toLongIds(users);

        assertThat(result).hasSameSizeAs(users);
    }

    @Property(tries = 100)
    @Label("toLongIds should return empty list for null input")
    void toLongIdsShouldReturnEmptyForNull() {
        List<Long> result = UserMapper.toLongIds(null);

        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    @Property(tries = 100)
    @Label("toLongIds should return empty list for empty input")
    void toLongIdsShouldReturnEmptyForEmptyInput() {
        List<Long> result = UserMapper.toLongIds(Collections.emptyList());

        assertThat(result)
            .isNotNull()
            .isEmpty();
    }

    // =========================================================================
    // toLongIds() ORDER PRESERVATION PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("toLongIds should preserve order of IDs")
    void toLongIdsShouldPreserveOrder(
            @ForAll @Size(min = 2, max = 50) List<@LongRange(min = 1, max = 10000) Long> ids) {
        List<User> users = createMockUsers(ids);

        List<Long> result = UserMapper.toLongIds(users);

        assertThat(result).containsExactlyElementsOf(ids);
    }

    @Property(tries = 200)
    @Label("toLongIds should map first element correctly")
    void toLongIdsShouldMapFirstElementCorrectly(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE / 2) Long firstId,
            @ForAll @Size(min = 0, max = 10) List<@LongRange(min = 1, max = 10000) Long> restIds) {
        List<Long> allIds = new ArrayList<>();
        allIds.add(firstId);
        allIds.addAll(restIds);

        List<User> users = createMockUsers(allIds);

        List<Long> result = UserMapper.toLongIds(users);

        assertThat(result.get(0)).isEqualTo(firstId);
    }

    @Property(tries = 200)
    @Label("toLongIds should map last element correctly")
    void toLongIdsShouldMapLastElementCorrectly(
            @ForAll @Size(min = 0, max = 10) List<@LongRange(min = 1, max = 10000) Long> initialIds,
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE / 2) Long lastId) {
        List<Long> allIds = new ArrayList<>(initialIds);
        allIds.add(lastId);

        List<User> users = createMockUsers(allIds);

        List<Long> result = UserMapper.toLongIds(users);

        assertThat(result.get(result.size() - 1)).isEqualTo(lastId);
    }

    // =========================================================================
    // toLongIds() VALUE MAPPING PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("toLongIds should contain all input IDs")
    void toLongIdsShouldContainAllInputIds(
            @ForAll @Size(min = 1, max = 50) List<@LongRange(min = 1, max = 10000) Long> ids) {
        List<User> users = createMockUsers(ids);

        List<Long> result = UserMapper.toLongIds(users);

        assertThat(result).containsAll(ids);
    }

    @Property(tries = 200)
    @Label("toLongIds should handle boundary Long values")
    void toLongIdsShouldHandleBoundaryValues(
            @ForAll @From("boundaryLongValues") Long boundaryValue) {
        User user = createMockUser(boundaryValue);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        assertThat(result)
            .hasSize(1)
            .containsExactly(boundaryValue);
    }

    @Property(tries = 500)
    @Label("toLongIds result should contain exact mapped values")
    void toLongIdsShouldContainExactMappedValues(
            @ForAll @LongRange(min = -10000, max = 10000) Long id) {
        User user = createMockUser(id);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        assertThat(result.get(0)).isEqualTo(id);
    }

    // =========================================================================
    // toLongIds() NULL SAFETY PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("toLongIds should not throw NullPointerException for null input")
    void toLongIdsShouldNotThrowForNullInput() {
        assertThatCode(() -> UserMapper.toLongIds(null))
            .doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("toLongIds result for null should be immutable")
    void toLongIdsNullResultShouldBeImmutable() {
        List<Long> result = UserMapper.toLongIds(null);

        assertThatThrownBy(() -> result.add(1L))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Property(tries = 100)
    @Label("toLongIds result for empty should be immutable")
    void toLongIdsEmptyResultShouldBeImmutable() {
        List<Long> result = UserMapper.toLongIds(Collections.emptyList());

        assertThatThrownBy(() -> result.add(1L))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // toLongIds() IDEMPOTENCY AND CONSISTENCY PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("toLongIds should be consistent across multiple calls")
    void toLongIdsShouldBeConsistent(
            @ForAll @Size(min = 1, max = 20) List<@LongRange(min = 1, max = 10000) Long> ids) {
        List<User> users = createMockUsers(ids);

        List<Long> result1 = UserMapper.toLongIds(users);
        List<Long> result2 = UserMapper.toLongIds(users);

        assertThat(result1).isEqualTo(result2);
    }

    @Property(tries = 200)
    @Label("toLongIds with single element should return single-element list")
    void toLongIdsWithSingleElementShouldReturnSingleElementList(
            @ForAll @LongRange(min = Long.MIN_VALUE / 2, max = Long.MAX_VALUE / 2) Long id) {
        User user = createMockUser(id);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        assertThat(result)
            .hasSize(1)
            .first()
            .isEqualTo(id);
    }

    // =========================================================================
    // toLongIds() MUTABILITY PROPERTIES (for non-empty input)
    // =========================================================================

    @Property(tries = 100)
    @Label("toLongIds result for non-empty input should be mutable")
    void toLongIdsNonEmptyResultShouldBeMutable(
            @ForAll @LongRange(min = 1, max = 10000) Long id) {
        User user = createMockUser(id);

        List<Long> result = UserMapper.toLongIds(List.of(user));

        // Collectors.toList() returns mutable list
        assertThatCode(() -> result.add(999L))
            .doesNotThrowAnyException();
        assertThat(result).contains(999L);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<Long> boundaryLongValues() {
        return Arbitraries.of(
            Long.MIN_VALUE,
            Long.MIN_VALUE + 1,
            -1L,
            0L,
            1L,
            Long.MAX_VALUE - 1,
            Long.MAX_VALUE
        );
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private User createMockUser(Long id) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(id);
        return user;
    }

    private List<User> createMockUsers(List<Long> ids) {
        List<User> users = new ArrayList<>();
        for (Long id : ids) {
            users.add(createMockUser(id));
        }
        return users;
    }
}
