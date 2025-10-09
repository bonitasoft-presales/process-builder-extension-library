package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bonitasoft.processbuilder.constants.Constants;
import com.bonitasoft.processbuilder.extension.ProcessUtils.ProcessInitiator;

import java.time.OffsetDateTime;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link BDMAuditUtils} utility class.
 * Tests cover all logic branches for creation, modification, and all reflection exceptions.
 */
@ExtendWith(MockitoExtension.class)
class BDMAuditUtilsTest {

    // --- MOCK BDM CLASSES FOR TESTING COVERAGE ---
    
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
        
        // GETTERS (Required for full BDM compliance, but not strictly by this utility method)
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
     * Mock class to test missing audit methods (Throws NoSuchMethodException during invokeSetter).
     */
    public static class MissingSetterMock {
        // NOTE: setCreatorId and setModifierId are intentionally MISSING.
        public OffsetDateTime getCreationDate() { return null; }
        public void setCreationDate(OffsetDateTime creationDate) { /* Mock */ }
        public void setCreatorName(String name) { /* Mock */ }
        public void setModificationDate(OffsetDateTime date) { /* Mock */ }
        public void setModifierName(String name) { /* Mock */ }
    }
    
    /**
     * Mock class to test InvocationTargetException when a setter throws an internal exception.
     */
    public static class ThrowingSetterMock {
        // Must implement all setters/getters required by the audit utility
        
        // Throws a runtime exception during the first setter call in the CREATION path
        public void setCreationDate(OffsetDateTime creationDate) { 
            throw new UnsupportedOperationException("Mock exception during setCreationDate"); 
        }
        public void setCreatorId(Long id) { /* Mock */ }
        public void setCreatorName(String name) { /* Mock */ }
        public void setModificationDate(OffsetDateTime date) { /* Mock */ }
        public void setModifierId(Long id) { /* Mock */ }
        public void setModifierName(String name) { /* Mock */ }
        
        // Minimal getters to prevent other reflection failures
        public OffsetDateTime getCreationDate() { return null; }
    }
    
    // --- SETUP ---
    
    private final ProcessInitiator initiator = new ProcessInitiator(10L, "testUser", "Test User");
    private final Long persistenceId = 100L;
    
    // =========================================================================
    // SECTION 1: SUCCESS PATHS (100% Logic Coverage)
    // =========================================================================
    
    /**
     * Tests the successful creation of a new object.
     * Covers: {@code bdmObject == null} (true) and {@code isNewObject} (true) paths.
     */
    @Test
    void createOrUpdateAuditData_should_handle_creation_path_successfully() {
        // Given: bdmObject is null, and we pass a new object for creation
        AuditSuccessMock newInstance = new AuditSuccessMock();
        
        // When: Called to create a new object
        AuditSuccessMock newObject = BDMAuditUtils.createOrUpdateAuditData(
                null, 
                newInstance, // Pass pre-instantiated object (newBdmObject)
                AuditSuccessMock.class, 
                initiator, 
                null // Use null persistenceId for logging purposes
        );
        
        // Then: The passed instance is returned and Creation fields are set
        assertSame(newInstance, newObject);
        assertNotNull(newObject.getCreationDate());
        assertEquals(initiator.id(), newObject.getCreatorId());
        assertNull(newObject.getModificationDate(), "Modification date must be null on creation.");
    }

    /**
     * Tests the successful update of an existing object.
     * Covers: {@code bdmObject == null} (false) and {@code isNewObject} (false) paths.
     */
    @Test
    void createOrUpdateAuditData_should_handle_update_path_successfully() {
        // Given: An existing object instance and a valid persistence ID
        AuditSuccessMock existingObject = new AuditSuccessMock();
        existingObject.setCreationDate(OffsetDateTime.now().minusDays(1)); 

        // When: Called to update the object
        AuditSuccessMock updatedObject = BDMAuditUtils.createOrUpdateAuditData(
                existingObject, 
                null, // newBdmObject is ignored
                AuditSuccessMock.class, 
                initiator, 
                persistenceId
        );
        
        // Then: The Modification logic was executed
        assertSame(existingObject, updatedObject);
        assertNotNull(updatedObject.getModificationDate(), "Modification date must be set.");
        assertEquals(initiator.id(), updatedObject.getModifierId(), "Modifier ID must be set.");
        assertNotNull(updatedObject.getCreationDate(), "Creation date must be preserved.");
    }
    
    // =========================================================================
    // SECTION 2: EXCEPTION COVERAGE
    // =========================================================================

    /**
     * Tests the exception path when both objects are null (Failure in Safety Validation).
     * Covers: {@code targetObject == null} internal check (Both are null).
     */
    @Test
    void createOrUpdateAuditData_should_throw_illegal_argument_exception_when_both_objects_are_null() {
        // Given: Both objects are null
        
        // When / Then: IllegalArgumentException must be thrown
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            BDMAuditUtils.createOrUpdateAuditData(
                null, 
                null, 
                AuditSuccessMock.class, // Class is used for logging/error message
                initiator, 
                persistenceId
            );
        });
        
        assertTrue(thrown.getMessage().contains("Both bdmObject (existing) and newBdmObject (pre-instantiated) are null"));
    }
    
    /**
     * Tests the Reflection failure on the CREATION path (missing setter).
     * Covers: {@code isNewObject} (true) and the generic {@code Exception} catch (specifically {@code NoSuchMethodException}).
     */
    @Test
    void createOrUpdateAuditData_should_throw_reflection_exception_on_creation_if_setter_is_missing() {
        // Given: MissingSetterMock lacks 'setCreatorId' method
        
        // When / Then: RuntimeException must be thrown because the Creation path fails on the second setter call.
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            BDMAuditUtils.createOrUpdateAuditData(
                null, // Force CREATION path
                new MissingSetterMock(), 
                MissingSetterMock.class, 
                initiator, 
                null
            );
        });
        
        // Assert that the exception indicates a reflection failure and cause.
        assertTrue(thrown.getMessage().contains("failed due to Reflection error"));
        assertTrue(thrown.getCause() instanceof NoSuchMethodException);
    }

    /**
     * Tests the Reflection failure on the UPDATE path (missing setter).
     * Covers: {@code isNewObject} (false) and the generic {@code Exception} catch (specifically {@code NoSuchMethodException}).
     */
    @Test
    void createOrUpdateAuditData_should_throw_reflection_exception_on_update_if_setter_is_missing() {
        // Given: MissingSetterMock lacks 'setModifierId' method and an existing object is passed
        
        // When / Then: RuntimeException must be thrown because the Update path fails on the second setter call.
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            BDMAuditUtils.createOrUpdateAuditData(
                new MissingSetterMock(), // Force UPDATE path
                null, 
                MissingSetterMock.class, 
                initiator, 
                persistenceId // Non-null ID
            );
        });
        
        // Assert that the exception indicates a reflection failure and cause.
        assertTrue(thrown.getMessage().contains("failed due to Reflection error"));
        assertTrue(thrown.getCause() instanceof NoSuchMethodException);
    }

    /**
     * Tests the exception path when a setter method throws an exception internally (BDM business logic error).
     * Covers: {@code InvocationTargetException} catch.
     */
    @Test
    void createOrUpdateAuditData_should_throw_runtime_exception_if_setter_throws_exception() {
        // Given: ThrowingSetterMock throws an UnsupportedOperationException during setCreationDate
        
        // When / Then: RuntimeException must be thrown, wrapping the internal exception.
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            BDMAuditUtils.createOrUpdateAuditData(
                null, // Force CREATION path
                new ThrowingSetterMock(), 
                ThrowingSetterMock.class, 
                initiator, 
                null
            );
        });
        
        // Assert the outer RuntimeException message and the root cause type
        assertTrue(thrown.getMessage().contains("Error during BDM method call"));
        assertTrue(thrown.getCause() instanceof UnsupportedOperationException);
    }
    
    // =========================================================================
    // SECTION 3: UTILITY CLASS COVERAGE
    // =========================================================================
    
    /**
     * Tests the private constructor to ensure the utility class cannot be instantiated, 
     * enforcing its static nature and achieving code coverage on the constructor.
     */
    @Test
    void constructor_should_throw_unsupported_operation_exception() throws Exception {
        // 1. Retrieve the Constructor object for the class.
        Constructor<BDMAuditUtils> constructor = BDMAuditUtils.class.getDeclaredConstructor();
        
        // 2. VERIFICATION: Use getModifiers() to ensure the constructor is PRIVATE.
        // This confirms we are testing the correct, restricted constructor.
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), 
                "The constructor must be declared as private to prevent instantiation.");
        
        // 3. FORCE ACCESSIBILITY: Override the 'private' restriction for testing purposes.
        // This is necessary for the newInstance() method to be invokable.
        constructor.setAccessible(true);
        
        // 4. Invoke the constructor and expect the wrapper exception (InvocationTargetException).
        InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, () -> {
            // The call must be 'newInstance()', which is the reflection invocation method.
            constructor.newInstance();
        }, "Invoking the private constructor should wrap the internal exception in InvocationTargetException.");
        
        // 5. Verify the actual cause is the expected exception (UnsupportedOperationException).
        Throwable actualCause = thrownException.getCause();
        assertTrue(actualCause instanceof UnsupportedOperationException, 
                "The internal exception (cause) must be UnsupportedOperationException.");
                
        final String expectedMessage = "This is a "+this.getClass().getSimpleName().replace(Constants.TEST, "")+" class and cannot be instantiated.";
        assertEquals(expectedMessage, actualCause.getMessage(),
                    "The constructor's message should match the expected text.");
        
        // Optional: Revert the accessibility change after the test
        constructor.setAccessible(false);
    }

}