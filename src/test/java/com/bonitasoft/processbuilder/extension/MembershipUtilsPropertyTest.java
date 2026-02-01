package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link MembershipUtils} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("MembershipUtils Property-Based Tests")
class MembershipUtilsPropertyTest {

    // =========================================================================
    // buildMembershipKey Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("buildMembershipKey should return correctly formatted key when both IDs are valid")
    void buildMembershipKey_shouldReturnCorrectlyFormattedKey(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long groupId,
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long roleId) {

        String result = MembershipUtils.buildMembershipKey(groupId, roleId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(groupId + "$" + roleId);
        assertThat(result).contains("$");
    }

    @Property(tries = 100)
    @Label("buildMembershipKey should return null when groupId is null")
    void buildMembershipKey_shouldReturnNullWhenGroupIdIsNull(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long roleId) {

        String result = MembershipUtils.buildMembershipKey(null, roleId);

        assertThat(result).isNull();
    }

    @Property(tries = 100)
    @Label("buildMembershipKey should return null when roleId is null")
    void buildMembershipKey_shouldReturnNullWhenRoleIdIsNull(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long groupId) {

        String result = MembershipUtils.buildMembershipKey(groupId, null);

        assertThat(result).isNull();
    }

    @Property(tries = 50)
    @Label("buildMembershipKey should return null when both IDs are null")
    void buildMembershipKey_shouldReturnNullWhenBothIdsAreNull() {
        String result = MembershipUtils.buildMembershipKey(null, null);

        assertThat(result).isNull();
    }

    // =========================================================================
    // Key Format Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("buildMembershipKey should use dollar sign as separator")
    void buildMembershipKey_shouldUseDollarSignAsSeparator(
            @ForAll @LongRange(min = 1, max = 1000000) long groupId,
            @ForAll @LongRange(min = 1, max = 1000000) long roleId) {

        String result = MembershipUtils.buildMembershipKey(groupId, roleId);

        assertThat(result).isNotNull();
        assertThat(result.split("\\$")).hasSize(2);
        assertThat(result.split("\\$")[0]).isEqualTo(String.valueOf(groupId));
        assertThat(result.split("\\$")[1]).isEqualTo(String.valueOf(roleId));
    }

    @Property(tries = 100)
    @Label("buildMembershipKey key should be parseable back to original IDs")
    void buildMembershipKey_keyShouldBeParseableBackToOriginalIds(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long groupId,
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long roleId) {

        String result = MembershipUtils.buildMembershipKey(groupId, roleId);

        assertThat(result).isNotNull();
        String[] parts = result.split("\\$");
        assertThat(Long.parseLong(parts[0])).isEqualTo(groupId);
        assertThat(Long.parseLong(parts[1])).isEqualTo(roleId);
    }

    // =========================================================================
    // Determinism Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("buildMembershipKey should be deterministic (same input, same output)")
    void buildMembershipKey_shouldBeDeterministic(
            @ForAll @LongRange(min = 1, max = 1000000) long groupId,
            @ForAll @LongRange(min = 1, max = 1000000) long roleId) {

        String result1 = MembershipUtils.buildMembershipKey(groupId, roleId);
        String result2 = MembershipUtils.buildMembershipKey(groupId, roleId);
        String result3 = MembershipUtils.buildMembershipKey(groupId, roleId);

        assertThat(result1).isEqualTo(result2).isEqualTo(result3);
    }

    @Property(tries = 100)
    @Label("buildMembershipKey different IDs should produce different keys")
    void buildMembershipKey_differentIdsShouldProduceDifferentKeys(
            @ForAll @LongRange(min = 1, max = 1000) long groupId1,
            @ForAll @LongRange(min = 1001, max = 2000) long groupId2,
            @ForAll @LongRange(min = 1, max = 1000) long roleId) {

        Assume.that(groupId1 != groupId2);

        String result1 = MembershipUtils.buildMembershipKey(groupId1, roleId);
        String result2 = MembershipUtils.buildMembershipKey(groupId2, roleId);

        assertThat(result1).isNotEqualTo(result2);
    }

    // =========================================================================
    // Boundary Value Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("buildMembershipKey should handle boundary Long values")
    void buildMembershipKey_shouldHandleBoundaryLongValues(
            @ForAll("boundaryLongValues") long groupId,
            @ForAll("boundaryLongValues") long roleId) {

        String result = MembershipUtils.buildMembershipKey(groupId, roleId);

        assertThat(result).isNotNull();
        assertThat(result).contains("$");
    }

    @Provide
    Arbitrary<Long> boundaryLongValues() {
        return Arbitraries.of(
                1L,
                Long.MAX_VALUE,
                Long.MAX_VALUE - 1,
                Integer.MAX_VALUE + 1L,
                0L,
                -1L,
                Long.MIN_VALUE
        );
    }

    @Property(tries = 50)
    @Label("buildMembershipKey should handle zero values")
    void buildMembershipKey_shouldHandleZeroValues() {
        String result = MembershipUtils.buildMembershipKey(0L, 0L);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("0$0");
    }

    @Property(tries = 50)
    @Label("buildMembershipKey should handle negative values")
    void buildMembershipKey_shouldHandleNegativeValues(
            @ForAll @LongRange(min = Long.MIN_VALUE, max = -1) long groupId,
            @ForAll @LongRange(min = Long.MIN_VALUE, max = -1) long roleId) {

        String result = MembershipUtils.buildMembershipKey(groupId, roleId);

        assertThat(result).isNotNull();
        assertThat(result).contains("$");
        assertThat(result).startsWith("-"); // Should start with negative sign
    }

    // =========================================================================
    // Uniqueness Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("buildMembershipKey order of IDs matters (not commutative)")
    void buildMembershipKey_orderOfIdsMatters(
            @ForAll @LongRange(min = 1, max = 1000) long id1,
            @ForAll @LongRange(min = 1001, max = 2000) long id2) {

        String keyAB = MembershipUtils.buildMembershipKey(id1, id2);
        String keyBA = MembershipUtils.buildMembershipKey(id2, id1);

        // Keys should be different when IDs are swapped
        assertThat(keyAB).isNotEqualTo(keyBA);
    }

    @Property(tries = 100)
    @Label("buildMembershipKey key length should be consistent with ID lengths")
    void buildMembershipKey_keyLengthShouldBeConsistentWithIdLengths(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long groupId,
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long roleId) {

        String result = MembershipUtils.buildMembershipKey(groupId, roleId);
        int expectedLength = String.valueOf(groupId).length() + 1 + String.valueOf(roleId).length();

        assertThat(result).hasSize(expectedLength);
    }
}
