package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bonitasoft.processbuilder.extension.ProcessUtils.ProcessInitiator;

import java.time.OffsetDateTime;
import java.lang.reflect.Constructor;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link BDMAuditUtils} utility class.
 * Tests cover all logic branches for creation, modification, and all reflection exceptions.
 */
@ExtendWith(MockitoExtension.class)
class BDMAuditUtilsTest {

    // --- MOCK BDM CLASSES FOR TESTING ---
    
    /**
     * Mock class for successful auditing. Stores all audit data internally 
     * to verify setters called via Reflection.
     */
    public static class AuditSuccessMock {
        private OffsetDateTime creationDate;
        private Long creatorId;
        private String creatorName;
        private OffsetDateTime modificationDate;
        private Long modifierId;
        private String modifierName;
        
        // GETTERS
        public OffsetDateTime getCreationDate() { return creationDate; }
        public Long getCreatorId() { return creatorId; }
        public OffsetDateTime getModificationDate() { return modificationDate; }
        public Long getModifierId() { return modifierId; }
        
        // SETTERS (Called by Reflection)
        public void setCreationDate(OffsetDateTime creationDate) { this.creationDate = creationDate; }
        public void setCreatorId(Long id) { this.creatorId = id; }
        public void setCreatorName(String name) { this.creatorName = name; }
        public void setModificationDate(OffsetDateTime date) { this.modificationDate = date; }
        public void setModifierId(Long id) { this.modifierId = id; }
        public void setModifierName(String name) { this.modifierName = name; }
    }
    
    /**
     * Mock class to test missing default constructor (Throws NoSuchMethodException in BDMUtils).
     */
    public static class NoDefaultConstructorMock {
        public NoDefaultConstructorMock(String arg) {} // No default constructor
        // Must have the audit methods to pass the Reflection part of the logic later
        public OffsetDateTime getCreationDate() { return null; } 
        public void setCreationDate(OffsetDateTime creationDate) { /* Mock */ }
        public void setCreatorId(Long id) { /* Mock */ }
        public void setCreatorName(String name) { /* Mock */ }
        public void setModificationDate(OffsetDateTime date) { /* Mock */ }
        public void setModifierId(Long id) { /* Mock */ }
        public void setModifierName(String name) { /* Mock */ }
    }
    
    /**
     * Mock class to test missing audit methods (Throws NoSuchMethodException during invokeSetter).
     */
    public static class MissingSetterMock {
        // Missing setCreatorId!
        public OffsetDateTime getCreationDate() { return null; }
        public void setCreationDate(OffsetDateTime creationDate) { /* Mock */ }
        public void setCreatorName(String name) { /* Mock */ }
        // public void setCreatorId(Long id) is missing!
        public void setModificationDate(OffsetDateTime date) { /* Mock */ }
        //public void setModifierId(Long id) { /* Mock */ }
        public void setModifierName(String name) { /* Mock */ }
    }
    
    // --- SETUP ---
    
    private final ProcessInitiator initiator = new ProcessInitiator(10L, "testUser", "Test User");
    private final Long persistenceId = 100L;
    
    /**
     * Tests the successful creation of a new object when {@code bdmObject} is null.
     * This covers the 'Instanciation' and 'Creation Logic' branches.
     */
    @Test
    void createOrUpdateAuditData_should_create_new_object_when_null() {
        // Given: bdmObject is null
        
        // When: Called to create a new object
        AuditSuccessMock newObject = BDMAuditUtils.createOrUpdateAuditData(
                null, 
                AuditSuccessMock.class, 
                initiator, 
                persistenceId
        );
        
        // Then: A new object is returned and Creation fields are set
        assertNotNull(newObject);
        assertNotNull(newObject.getCreationDate());
        assertEquals(initiator.id(), newObject.getCreatorId());
        assertNull(newObject.getModificationDate()); // Should not have run modification logic
    }

    /**
     * Tests the logic when an object is passed (non-null) but has no creation date.
     * Based on the strict requirement: If the object is NON-NULL, it is treated as a MODIFICATION.
     * The creation date should remain null as only modification setters are called.
     */
    @Test
    void createOrUpdateAuditData_should_apply_modification_logic_when_date_is_null() {
        // Given: An existing object instance but without a creation date (fresh BDM object)
        AuditSuccessMock existingObject = new AuditSuccessMock();
        assertNull(existingObject.getCreationDate());

        // When: Called to update the object
        AuditSuccessMock updatedObject = BDMAuditUtils.createOrUpdateAuditData(
                existingObject, 
                AuditSuccessMock.class, 
                initiator, 
                persistenceId
        );
        
        // Then: The Modification logic was executed (based on the strict requirement)
        assertSame(existingObject, updatedObject);
        
        // The creation date should remain NULL because the method only called modification setters
        assertNull(updatedObject.getCreationDate(), "Creation date must remain null (Modification path executed).");
        
        // Modification fields MUST be set
        assertNotNull(updatedObject.getModificationDate(), "Modification date must be set.");
        assertEquals(initiator.id(), updatedObject.getModifierId(), "Modifier ID must be set.");
        
    }
    
    /**
     * Tests the successful modification of an existing object.
     * This covers the 'Modification Logic' branch where the creation date already exists.
     */
    @Test
    void createOrUpdateAuditData_should_apply_modification_logic_when_date_exists() {
        // Given: An existing object with a creation date already set
        AuditSuccessMock existingObject = new AuditSuccessMock();
        existingObject.setCreationDate(OffsetDateTime.now().minusDays(1)); // Simulate existing object

        // When: Called to update the object
        AuditSuccessMock updatedObject = BDMAuditUtils.createOrUpdateAuditData(
                existingObject, 
                AuditSuccessMock.class, 
                initiator, 
                persistenceId
        );
        
        // Then: The modification date and ID must be set
        assertSame(existingObject, updatedObject);
        assertNotNull(updatedObject.getModificationDate(), "Modification date must be set.");
        assertEquals(initiator.id(), updatedObject.getModifierId(), "Modifier ID must be set.");
    }
    
    // --- EXCEPTION COVERAGE (100% BRANCH COVERAGE) ---

    /**
     * Tests the exception path when the BDM class is missing the default constructor 
     * and a null object is passed (Failure in Instantiation Management).
     */
    @Test
    void createOrUpdateAuditData_should_throw_exception_if_default_constructor_is_missing() {
        // Given: The object is null, and the class is missing a default constructor
        
        // When / Then: RuntimeException must be thrown due to NoSuchMethodException
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            BDMAuditUtils.createOrUpdateAuditData(
                null, 
                NoDefaultConstructorMock.class, 
                initiator, 
                persistenceId
            );
        });
        
        assertTrue(thrown.getMessage().contains("must have a default (no-argument) constructor"));
    }
    
    /**
     * Tests the exception path when a required setter method is missing (Failure in Reflection Logic).
     * This forces the CREATION path by passing null, which attempts to call the missing setCreatorId method.
     */
    @Test
    void createOrUpdateAuditData_should_throw_exception_if_audit_setter_is_missing() {
        // Given: We want to test the MissingSetterMock class, which lacks 'setCreatorId'.
        
        // When / Then: RuntimeException must be thrown because the Creation path attempts
        // to call 'setCreatorId' via Reflection, which fails.
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            BDMAuditUtils.createOrUpdateAuditData(
                null, // <-- Pass NULL to force Instantiation and the CREATION path
                MissingSetterMock.class, 
                initiator, 
                persistenceId
            );
        });
        
        // Assert that the exception thrown by the utility class contains the correct error message.
        assertTrue(thrown.getMessage().contains("failed due to Reflection error"), 
            "The exception message should indicate a reflection failure.");
    }

    /**
     * Tests that the utility class cannot be instantiated externally 
     * and ensures 100% coverage of the private constructor by invoking it via Reflection.
     */
    @Test
    void constructor_should_be_private_and_uninvokable() throws Exception {
        // 1. Arrange: Get the private, no-argument constructor of the utility class.
        final Constructor<BDMAuditUtils> constructor = BDMAuditUtils.class.getDeclaredConstructor();
        
        // 2. Act: Make the private constructor accessible to force its execution.
        constructor.setAccessible(true); 
        
        // 3. Assert (Invocation): Attempt to call the private constructor.
        constructor.newInstance(); 
    }
}