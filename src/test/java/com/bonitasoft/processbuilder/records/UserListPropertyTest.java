package com.bonitasoft.processbuilder.records;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link UserList} record.
 * Tests invariants that must hold for any valid instance.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("UserList Property-Based Tests")
class UserListPropertyTest {

    // =========================================================================
    // EQUALITY PROPERTIES
    // =========================================================================

    @Property(tries = 1000)
    @Label("Equality should be reflexive: x.equals(x)")
    void equalityShouldBeReflexive(@ForAll @From("validUserLists") UserList record) {
        assertThat(record).isEqualTo(record);
    }

    @Property(tries = 1000)
    @Label("Equality should be symmetric: x.equals(y) == y.equals(x)")
    void equalityShouldBeSymmetric(
            @ForAll @From("validUserLists") UserList record1,
            @ForAll @From("validUserLists") UserList record2) {
        assertThat(record1.equals(record2)).isEqualTo(record2.equals(record1));
    }

    @Property(tries = 500)
    @Label("Equality with same values should be true")
    void equalityWithSameValuesShouldBeTrue(
            @ForAll @LongRange(min = 1, max = 1000) Long persistenceId,
            @ForAll Boolean canLaunchProcess,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String refMemberShip,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String memberShipKey,
            @ForAll @LongRange(min = 1, max = 1000) Long groupId,
            @ForAll @LongRange(min = 1, max = 1000) Long roleId,
            @ForAll @LongRange(min = 1, max = 1000) Long userId,
            @ForAll @LongRange(min = 1, max = 1000) Long pBProcessPersistenceId) {

        UserList record1 = new UserList(persistenceId, canLaunchProcess, refMemberShip,
            memberShipKey, groupId, roleId, userId, pBProcessPersistenceId);
        UserList record2 = new UserList(persistenceId, canLaunchProcess, refMemberShip,
            memberShipKey, groupId, roleId, userId, pBProcessPersistenceId);

        assertThat(record1).isEqualTo(record2);
    }

    @Property(tries = 500)
    @Label("equals(null) should return false")
    void equalsNullShouldReturnFalse(@ForAll @From("validUserLists") UserList record) {
        assertThat(record.equals(null)).isFalse();
    }

    // =========================================================================
    // HASHCODE PROPERTIES
    // =========================================================================

    @Property(tries = 1000)
    @Label("HashCode should be consistent with equals")
    void hashCodeShouldBeConsistentWithEquals(
            @ForAll @From("validUserLists") UserList record1,
            @ForAll @From("validUserLists") UserList record2) {
        if (record1.equals(record2)) {
            assertThat(record1.hashCode()).isEqualTo(record2.hashCode());
        }
    }

    @Property(tries = 500)
    @Label("HashCode should be stable across multiple calls")
    void hashCodeShouldBeStable(@ForAll @From("validUserLists") UserList record) {
        int hash1 = record.hashCode();
        int hash2 = record.hashCode();
        int hash3 = record.hashCode();

        assertThat(hash1).isEqualTo(hash2).isEqualTo(hash3);
    }

    // =========================================================================
    // TOSTRING PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("toString should never throw")
    void toStringShouldNeverThrow(@ForAll @From("userListsWithNulls") UserList record) {
        assertThatCode(() -> record.toString()).doesNotThrowAnyException();
    }

    @Property(tries = 500)
    @Label("toString should never return null")
    void toStringShouldNeverReturnNull(@ForAll @From("validUserLists") UserList record) {
        assertThat(record.toString()).isNotNull();
    }

    @Property(tries = 500)
    @Label("toString should contain class name")
    void toStringShouldContainClassName(@ForAll @From("validUserLists") UserList record) {
        assertThat(record.toString()).contains("UserList");
    }

    // =========================================================================
    // ACCESSOR PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("Accessors should return constructor values")
    void accessorsShouldReturnConstructorValues(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) Long persistenceId,
            @ForAll Boolean canLaunchProcess,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String refMemberShip,
            @ForAll @StringLength(min = 1, max = 20) @AlphaChars String memberShipKey,
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) Long groupId,
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) Long roleId,
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) Long userId,
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) Long pBProcessPersistenceId) {

        UserList record = new UserList(persistenceId, canLaunchProcess, refMemberShip,
            memberShipKey, groupId, roleId, userId, pBProcessPersistenceId);

        assertThat(record.persistenceId()).isEqualTo(persistenceId);
        assertThat(record.canLaunchProcess()).isEqualTo(canLaunchProcess);
        assertThat(record.refMemberShip()).isEqualTo(refMemberShip);
        assertThat(record.memberShipKey()).isEqualTo(memberShipKey);
        assertThat(record.groupId()).isEqualTo(groupId);
        assertThat(record.roleId()).isEqualTo(roleId);
        assertThat(record.userId()).isEqualTo(userId);
        assertThat(record.pBProcessPersistenceId()).isEqualTo(pBProcessPersistenceId);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<UserList> validUserLists() {
        Arbitrary<Long> ids = Arbitraries.longs().greaterOrEqual(1L);
        Arbitrary<Boolean> booleans = Arbitraries.of(true, false);
        Arbitrary<String> refs = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<String> keys = Arbitraries.of("USER", "ROLE", "GROUP", "MEMBERSHIP");

        return Combinators.combine(ids, booleans, refs, keys, ids, ids, ids, ids)
            .as(UserList::new);
    }

    @Provide
    Arbitrary<UserList> userListsWithNulls() {
        Arbitrary<Long> ids = Arbitraries.longs().greaterOrEqual(1L).injectNull(0.2);
        Arbitrary<Boolean> booleans = Arbitraries.of(true, false).injectNull(0.2);
        Arbitrary<String> refs = Arbitraries.strings().alpha().ofMaxLength(50).injectNull(0.2);
        Arbitrary<String> keys = Arbitraries.of("USER", "ROLE", "GROUP").injectNull(0.2);

        return Combinators.combine(ids, booleans, refs, keys, ids, ids, ids, ids)
            .as(UserList::new);
    }
}
