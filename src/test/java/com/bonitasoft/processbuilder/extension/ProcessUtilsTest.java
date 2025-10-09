package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstanceNotFoundException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.bonitasoft.processbuilder.extension.ProcessUtils.ProcessInitiator;
import com.bonitasoft.processbuilder.constants.Constants;
import com.bonitasoft.processbuilder.enums.ActionType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link ProcessUtils} utility class, designed for maximum code coverage.
 * This class uses Mockito to isolate dependencies on Bonita APIs and static utility methods.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProcessUtilsTest {

    // --- Mocks for Bonita API Access ---
    @Mock
    private APIAccessor apiAccessor;
    @Mock
    private ProcessAPI processApi;
    @Mock
    private IdentityAPI identityAPI;
    @Mock
    private ProcessInstance processInstance;
    @Mock
    private User user;

    // --- Mock for BDM Search Function ---
    @Mock
    private Function<Long, MockBDM> searchFunction;

    // --- Constants ---
    private static final long PROCESS_INSTANCE_ID = 100L;
    private static final long USER_ID = 123L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String USER_NAME = "jdoe";
    private static final String OBJECT_TYPE = "TestObject";
    private static final long PERSISTENCE_ID = 50L;

    /**
     * Mock class representing a BDM object for search/validation tests.
     */
    private static class MockBDM {
        private final Long id;
        public MockBDM(Long id) { this.id = id; }
        public Long getId() { return id; }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Setup general behavior for Process Initiator (Happy Path)
        when(apiAccessor.getProcessAPI()).thenReturn(processApi);
        when(apiAccessor.getIdentityAPI()).thenReturn(identityAPI);
        
        when(processApi.getProcessInstance(PROCESS_INSTANCE_ID)).thenReturn(processInstance);
        when(processInstance.getStartedBy()).thenReturn(USER_ID);
        
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getFirstName()).thenReturn(FIRST_NAME);
        when(user.getLastName()).thenReturn(LAST_NAME);
        when(user.getUserName()).thenReturn(USER_NAME);

        // General BDM stubbing (Happy Path)
        // Stub the search function to return a mock object for any ID, used as the default behavior.
        when(searchFunction.apply(anyLong())).thenReturn(new MockBDM(PERSISTENCE_ID));
    }

    // =========================================================================
    // SECTION 1: getProcessInitiator TESTS
    // =========================================================================

    /**
     * Tests the successful retrieval of the process initiator.
     */
    @Test
    void getProcessInitiator_should_return_valid_initiator_on_success() {
        // When
        ProcessInitiator result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertEquals(USER_ID, result.id());
        assertEquals(USER_NAME, result.userName());
        assertEquals(FIRST_NAME + " " + LAST_NAME, result.fullName());
    }

    /**
     * Tests the graceful handling of a UserNotFoundException (covers UserNotFoundException catch branch).
     */
    @Test
    void getProcessInitiator_should_return_unknown_user_on_UserNotFoundException() throws Exception {
        // Given
        when(identityAPI.getUser(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        // When
        ProcessInitiator result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("unknown_user", result.userName());
    }

    /**
     * Tests the graceful handling of a ProcessInstanceNotFoundException (covers general Exception catch branch).
     */
    @Test
    void getProcessInitiator_should_return_unknown_user_on_ProcessInstanceNotFoundException() throws Exception {
        // Given
        when(processApi.getProcessInstance(anyLong())).thenThrow(new ProcessInstanceNotFoundException("Process instance not found"));

        // When
        ProcessInitiator result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("unknown_user", result.userName());
    }

    /**
     * Tests the graceful handling of any other unexpected exception (covers general Exception catch branch).
     */
    @Test
    void getProcessInitiator_should_return_unknown_user_on_any_other_exception() throws Exception {
        // Given: Simulate an error deep inside the logic (e.g., getting startedBy fails)
        when(processInstance.getStartedBy()).thenThrow(new RuntimeException("Unexpected API error"));

        // When
        ProcessInitiator result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("unknown_user", result.userName());
    }

    // =========================================================================
    // SECTION 2: searchAndValidateId TESTS (Covers String ID parsing and internal validation)
    // =========================================================================

    /**
     * Tests successful retrieval when a valid ID string is passed.
     */
    @Test
    void searchAndValidateId_should_return_object_on_valid_id() {
        // Given
        MockBDM expectedObject = new MockBDM(PERSISTENCE_ID);
        when(searchFunction.apply(PERSISTENCE_ID)).thenReturn(expectedObject);

        // When
        MockBDM result = ProcessUtils.searchAndValidateId(String.valueOf(PERSISTENCE_ID), searchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertSame(expectedObject, result);
        verify(searchFunction, times(1)).apply(PERSISTENCE_ID);
    }
    
    /**
     * Tests returning null when the ID string is null (covers if condition false).
     */
    @Test
    void searchAndValidateId_should_return_null_on_null_id_string() {
        // When
        MockBDM result = ProcessUtils.searchAndValidateId(null, searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }
    
    /**
     * Tests returning null when the ID string is empty (covers if condition false).
     */
    @Test
    void searchAndValidateId_should_return_null_on_empty_id_string() {
        // When
        MockBDM result = ProcessUtils.searchAndValidateId("", searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }

    /**
     * Tests the error path when the ID string is not a valid number (covers NumberFormatException catch branch).
     */
    @Test
    void searchAndValidateId_should_throw_runtime_exception_on_invalid_id_format() {
        // Use Mockito to simulate the behavior of ExceptionUtils.logAndThrow
        try (MockedStatic<ExceptionUtils> mocked = mockStatic(ExceptionUtils.class)) {
            // FIX: Set the static mock to THROW a RuntimeException when called, fulfilling assertThrows.
            mocked.when(() -> ExceptionUtils.logAndThrow(any(), anyString()))
                  .thenThrow(new RuntimeException("Simulated Format Error"));
            
            // When / Then: RuntimeException must be thrown due to NumberFormatException
            assertThrows(RuntimeException.class, () -> {
                ProcessUtils.searchAndValidateId("abc", searchFunction, OBJECT_TYPE);
            });
            // Verify that the custom logging/throwing method was called
            mocked.verify(() -> ExceptionUtils.logAndThrow(any(), anyString()), times(1));
        }
    }

    /**
     * Tests the error path when the object is not found (covers the internal searchAndValidate error path).
     */
    @Test
    void searchAndValidateId_should_throw_runtime_exception_if_object_not_found() {
        // Given: The search function returns null
        when(searchFunction.apply(PERSISTENCE_ID)).thenReturn(null);

        // Use Mockito to simulate the behavior of ExceptionUtils.logAndThrow
        try (MockedStatic<ExceptionUtils> mocked = mockStatic(ExceptionUtils.class)) {
            // FIX: Set the static mock to THROW a RuntimeException when called, fulfilling assertThrows.
            mocked.when(() -> ExceptionUtils.logAndThrow(any(), anyString()))
                  .thenThrow(new RuntimeException("Simulated Object Not Found Error"));

            // When / Then: RuntimeException must be thrown due to object not found
            assertThrows(RuntimeException.class, () -> {
                ProcessUtils.searchAndValidateId(String.valueOf(PERSISTENCE_ID), searchFunction, OBJECT_TYPE);
            });
            // Verify that the custom logging/throwing method was called
            mocked.verify(() -> ExceptionUtils.logAndThrow(any(), anyString()), times(1));
        }
    }
    
    // =========================================================================
    // SECTION 3: validateActionAndDelete TESTS
    // =========================================================================

    /**
     * Tests successful validation for a DELETE action when the BDM object is found.
     */
    @Test
    void validateActionAndDelete_should_return_object_on_delete_if_found() {
        // Given
        MockBDM bdmObject = new MockBDM(PERSISTENCE_ID);

        // When
        MockBDM result = ProcessUtils.validateActionAndDelete(
                bdmObject, 
                ActionType.DELETE.name(), 
                PERSISTENCE_ID, 
                OBJECT_TYPE
        );

        // Then
        assertSame(bdmObject, result);
    }
    
    /**
     * Tests the error path for a DELETE action when the BDM object is NOT found (null).
     */
    @Test
    void validateActionAndDelete_should_throw_exception_on_delete_if_not_found() {
        // Use Mockito to simulate the behavior of ExceptionUtils.logAndThrow
        try (MockedStatic<ExceptionUtils> mocked = mockStatic(ExceptionUtils.class)) {
            // FIX: Set the static mock to THROW a RuntimeException when called, fulfilling assertThrows.
            mocked.when(() -> ExceptionUtils.logAndThrow(any(), anyString()))
                  .thenThrow(new RuntimeException("Simulated Deletion Not Found Error"));

            // When / Then: RuntimeException must be thrown
            assertThrows(RuntimeException.class, () -> {
                ProcessUtils.validateActionAndDelete(
                        null, // Object is null (not found)
                        ActionType.DELETE.name(), 
                        PERSISTENCE_ID, 
                        OBJECT_TYPE
                );
            });
            // Verify that the custom logging/throwing method was called
            mocked.verify(() -> ExceptionUtils.logAndThrow(any(), anyString()), times(1));
        }
    }

    /**
     * Tests the case where the action is NOT DELETE, so it returns null and bypasses validation.
     */
    @Test
    void validateActionAndDelete_should_return_null_on_non_delete_action() {
        // Given
        MockBDM bdmObject = new MockBDM(PERSISTENCE_ID);
        
        // When: Action is "INSERT" (any non-DELETE action)
        MockBDM resultCreate = ProcessUtils.validateActionAndDelete(
                bdmObject, 
                ActionType.INSERT.name(), 
                PERSISTENCE_ID, 
                OBJECT_TYPE
        );
        
        // When: actionTypeInput is null
        MockBDM resultNullAction = ProcessUtils.validateActionAndDelete(
                bdmObject, 
                null, 
                PERSISTENCE_ID, 
                OBJECT_TYPE
        );

        // Then
        assertNull(resultCreate);
        assertNull(resultNullAction);
    }
    
    /**
     * Tests the case where the action is DELETE but in lowercase/mixed case (covers equalsIgnoreCase).
     */
    @Test
    void validateActionAndDelete_should_work_with_mixed_case_delete_action() {
        // Given
        MockBDM bdmObject = new MockBDM(PERSISTENCE_ID);

        // When
        MockBDM result = ProcessUtils.validateActionAndDelete(
                bdmObject, 
                "delete", // lowercase
                PERSISTENCE_ID, 
                OBJECT_TYPE
        );

        // Then
        assertSame(bdmObject, result);
    }

    // =========================================================================
    // SECTION 4: Private Constructor Coverage
    // =========================================================================
    
    /**
     * Tests the private constructor to ensure the utility class cannot be instantiated, 
     * enforcing its static nature and achieving code coverage on the constructor.
     */
    @Test
    void constructor_should_throw_unsupported_operation_exception() throws Exception {
        // 1. Retrieve the Constructor object for the class.
        Constructor<ProcessUtils> constructor = ProcessUtils.class.getDeclaredConstructor();
        
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