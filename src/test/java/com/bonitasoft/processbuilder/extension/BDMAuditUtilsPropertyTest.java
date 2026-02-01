package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import com.bonitasoft.processbuilder.records.UserRecord;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link BDMAuditUtils} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("BDMAuditUtils Property-Based Tests")
class BDMAuditUtilsPropertyTest {

    // =========================================================================
    // Mock BDM Class for Testing
    // =========================================================================

    /**
     * Mock class simulating a BDM object with audit fields.
     */
    public static class MockBdmObject {
        private OffsetDateTime creationDate;
        private Long creatorId;
        private String creatorName;
        private OffsetDateTime modificationDate;
        private Long modifierId;
        private String modifierName;

        public OffsetDateTime getCreationDate() { return creationDate; }
        public Long getCreatorId() { return creatorId; }
        public String getCreatorName() { return creatorName; }
        public OffsetDateTime getModificationDate() { return modificationDate; }
        public Long getModifierId() { return modifierId; }
        public String getModifierName() { return modifierName; }

        public void setCreationDate(OffsetDateTime creationDate) { this.creationDate = creationDate; }
        public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }
        public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
        public void setModificationDate(OffsetDateTime modificationDate) { this.modificationDate = modificationDate; }
        public void setModifierId(Long modifierId) { this.modifierId = modifierId; }
        public void setModifierName(String modifierName) { this.modifierName = modifierName; }
    }

    // =========================================================================
    // Creation Path Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("createOrUpdateAuditData should set creation fields for new objects")
    void createOrUpdateAuditData_shouldSetCreationFieldsForNewObjects(
            @ForAll @LongRange(min = 1, max = 10000) long userId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String userName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String fullName) {

        UserRecord initiator = new UserRecord(userId, userName, fullName, null, null, null);
        MockBdmObject newObject = new MockBdmObject();

        MockBdmObject result = BDMAuditUtils.createOrUpdateAuditData(
                null, newObject, MockBdmObject.class, initiator, null);

        // Verify creation fields are set
        assertThat(result).isSameAs(newObject);
        assertThat(result.getCreationDate()).isNotNull();
        assertThat(result.getCreatorId()).isEqualTo(userId);
        assertThat(result.getCreatorName()).isEqualTo(fullName);

        // Verify modification fields are NOT set
        assertThat(result.getModificationDate()).isNull();
        assertThat(result.getModifierId()).isNull();
        assertThat(result.getModifierName()).isNull();
    }

    @Property(tries = 100)
    @Label("createOrUpdateAuditData should set creation date close to current time")
    void createOrUpdateAuditData_shouldSetCreationDateCloseToCurrentTime(
            @ForAll @LongRange(min = 1, max = 10000) long userId) {

        UserRecord initiator = new UserRecord(userId, "user", "User Name", null, null, null);
        MockBdmObject newObject = new MockBdmObject();
        OffsetDateTime before = OffsetDateTime.now();

        MockBdmObject result = BDMAuditUtils.createOrUpdateAuditData(
                null, newObject, MockBdmObject.class, initiator, null);

        OffsetDateTime after = OffsetDateTime.now();

        // Creation date should be between before and after
        assertThat(result.getCreationDate()).isAfterOrEqualTo(before);
        assertThat(result.getCreationDate()).isBeforeOrEqualTo(after);
    }

    // =========================================================================
    // Update Path Properties
    // =========================================================================

    @Property(tries = 100)
    @Label("createOrUpdateAuditData should set modification fields for existing objects")
    void createOrUpdateAuditData_shouldSetModificationFieldsForExistingObjects(
            @ForAll @LongRange(min = 1, max = 10000) long userId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String userName,
            @ForAll @AlphaChars @StringLength(min = 1, max = 100) String fullName,
            @ForAll @LongRange(min = 1, max = 10000) long persistenceId) {

        UserRecord initiator = new UserRecord(userId, userName, fullName, null, null, null);
        MockBdmObject existingObject = new MockBdmObject();
        OffsetDateTime originalCreationDate = OffsetDateTime.now().minusDays(1);
        existingObject.setCreationDate(originalCreationDate);

        MockBdmObject result = BDMAuditUtils.createOrUpdateAuditData(
                existingObject, null, MockBdmObject.class, initiator, persistenceId);

        // Verify existing object is returned
        assertThat(result).isSameAs(existingObject);

        // Verify modification fields are set
        assertThat(result.getModificationDate()).isNotNull();
        assertThat(result.getModifierId()).isEqualTo(userId);
        assertThat(result.getModifierName()).isEqualTo(fullName);

        // Verify creation fields are preserved (not overwritten)
        assertThat(result.getCreationDate()).isEqualTo(originalCreationDate);
    }

    @Property(tries = 100)
    @Label("createOrUpdateAuditData should set modification date close to current time")
    void createOrUpdateAuditData_shouldSetModificationDateCloseToCurrentTime(
            @ForAll @LongRange(min = 1, max = 10000) long userId,
            @ForAll @LongRange(min = 1, max = 10000) long persistenceId) {

        UserRecord initiator = new UserRecord(userId, "user", "User Name", null, null, null);
        MockBdmObject existingObject = new MockBdmObject();
        OffsetDateTime before = OffsetDateTime.now();

        MockBdmObject result = BDMAuditUtils.createOrUpdateAuditData(
                existingObject, null, MockBdmObject.class, initiator, persistenceId);

        OffsetDateTime after = OffsetDateTime.now();

        // Modification date should be between before and after
        assertThat(result.getModificationDate()).isAfterOrEqualTo(before);
        assertThat(result.getModificationDate()).isBeforeOrEqualTo(after);
    }

    // =========================================================================
    // Exception Handling Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("createOrUpdateAuditData should throw IllegalArgumentException when both objects are null")
    void createOrUpdateAuditData_shouldThrowExceptionWhenBothObjectsNull(
            @ForAll @LongRange(min = 1, max = 10000) long userId) {

        UserRecord initiator = new UserRecord(userId, "user", "User Name", null, null, null);

        assertThatThrownBy(() -> BDMAuditUtils.createOrUpdateAuditData(
                null, null, MockBdmObject.class, initiator, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Both bdmObject (existing) and newBdmObject (pre-instantiated) are null");
    }

    // =========================================================================
    // Idempotency and Consistency Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("createOrUpdateAuditData creation should always set user info consistently")
    void createOrUpdateAuditData_shouldSetUserInfoConsistently(
            @ForAll @LongRange(min = 1, max = 10000) long userId,
            @ForAll @AlphaChars @StringLength(min = 1, max = 50) String fullName) {

        UserRecord initiator = new UserRecord(userId, "username", fullName, null, null, null);

        // Test creation path
        MockBdmObject newObject = new MockBdmObject();
        MockBdmObject created = BDMAuditUtils.createOrUpdateAuditData(
                null, newObject, MockBdmObject.class, initiator, null);

        assertThat(created.getCreatorId()).isEqualTo(initiator.id());
        assertThat(created.getCreatorName()).isEqualTo(initiator.fullName());

        // Test update path
        MockBdmObject existingObject = new MockBdmObject();
        MockBdmObject updated = BDMAuditUtils.createOrUpdateAuditData(
                existingObject, null, MockBdmObject.class, initiator, 1L);

        assertThat(updated.getModifierId()).isEqualTo(initiator.id());
        assertThat(updated.getModifierName()).isEqualTo(initiator.fullName());
    }

    @Property(tries = 50)
    @Label("createOrUpdateAuditData should always return the target object")
    void createOrUpdateAuditData_shouldAlwaysReturnTargetObject(
            @ForAll @LongRange(min = 1, max = 10000) long userId,
            @ForAll boolean isNewObject) {

        UserRecord initiator = new UserRecord(userId, "user", "User Name", null, null, null);
        MockBdmObject targetObject = new MockBdmObject();

        MockBdmObject result;
        if (isNewObject) {
            result = BDMAuditUtils.createOrUpdateAuditData(
                    null, targetObject, MockBdmObject.class, initiator, null);
        } else {
            result = BDMAuditUtils.createOrUpdateAuditData(
                    targetObject, null, MockBdmObject.class, initiator, 1L);
        }

        assertThat(result).isSameAs(targetObject);
    }

    // =========================================================================
    // Boundary Value Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("createOrUpdateAuditData should handle boundary user IDs")
    void createOrUpdateAuditData_shouldHandleBoundaryUserIds(
            @ForAll("boundaryLongValues") long userId) {

        UserRecord initiator = new UserRecord(userId, "user", "User Name", null, null, null);
        MockBdmObject newObject = new MockBdmObject();

        MockBdmObject result = BDMAuditUtils.createOrUpdateAuditData(
                null, newObject, MockBdmObject.class, initiator, null);

        assertThat(result.getCreatorId()).isEqualTo(userId);
    }

    @Provide
    Arbitrary<Long> boundaryLongValues() {
        return Arbitraries.of(
                1L,
                Long.MAX_VALUE,
                Long.MAX_VALUE - 1,
                Integer.MAX_VALUE + 1L,
                1000000L
        );
    }

    @Property(tries = 50)
    @Label("createOrUpdateAuditData should handle special characters in names")
    void createOrUpdateAuditData_shouldHandleSpecialCharactersInNames(
            @ForAll @LongRange(min = 1, max = 10000) long userId,
            @ForAll("specialCharacterNames") String fullName) {

        UserRecord initiator = new UserRecord(userId, "user", fullName, null, null, null);
        MockBdmObject newObject = new MockBdmObject();

        MockBdmObject result = BDMAuditUtils.createOrUpdateAuditData(
                null, newObject, MockBdmObject.class, initiator, null);

        assertThat(result.getCreatorName()).isEqualTo(fullName);
    }

    @Provide
    Arbitrary<String> specialCharacterNames() {
        return Arbitraries.of(
                "John Doe",
                "María García",
                "Jean-Pierre Dubois",
                "O'Brien",
                "名前 姓",
                "Имя Фамилия",
                "Name with   spaces",
                "Name\twith\ttabs"
        );
    }

    // =========================================================================
    // Date Ordering Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("createOrUpdateAuditData modification date should be after creation date")
    void createOrUpdateAuditData_modificationDateShouldBeAfterCreationDate(
            @ForAll @LongRange(min = 1, max = 10000) long userId,
            @ForAll @IntRange(min = 1, max = 365) int daysAgo) {

        UserRecord initiator = new UserRecord(userId, "user", "User Name", null, null, null);
        MockBdmObject existingObject = new MockBdmObject();
        OffsetDateTime originalCreationDate = OffsetDateTime.now().minusDays(daysAgo);
        existingObject.setCreationDate(originalCreationDate);

        MockBdmObject result = BDMAuditUtils.createOrUpdateAuditData(
                existingObject, null, MockBdmObject.class, initiator, 1L);

        // Modification date should be after or equal to creation date
        assertThat(result.getModificationDate()).isAfterOrEqualTo(originalCreationDate);
    }
}
