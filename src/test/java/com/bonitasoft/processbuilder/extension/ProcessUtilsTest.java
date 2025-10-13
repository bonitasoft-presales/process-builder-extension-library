package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstanceNotFoundException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.bonitasoft.processbuilder.extension.ProcessUtils.ProcessInitiator;
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
 * This class uses Mockito to isolate dependencies on Bonita APIs and static utility methods
 * (especially {@code ExceptionUtils}).
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
    private static final String FAKE_ERROR_MESSAGE = "Simulated ExceptionUtils error";


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
        when(searchFunction.apply(anyLong())).thenReturn(new MockBDM(PERSISTENCE_ID));
    }

    // =========================================================================
    // SECTION 1: getProcessInitiator TESTS
    // =========================================================================

    @Test
    @DisplayName("Should successfully retrieve the process initiator details")
    void getProcessInitiator_should_return_valid_initiator_on_success() {
        // When
        ProcessInitiator result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertEquals(USER_ID, result.id());
        assertEquals(USER_NAME, result.userName());
        assertEquals(FIRST_NAME + " " + LAST_NAME, result.fullName());
    }

    @Test
    @DisplayName("Should return 'unknown_user' on UserNotFoundException")
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

    @Test
    @DisplayName("Should return 'unknown_user' on ProcessInstanceNotFoundException (general exception path)")
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

    @Test
    @DisplayName("Should return 'unknown_user' on any other unexpected API exception")
    void getProcessInitiator_should_return_unknown_user_on_any_other_exception() throws Exception {
        // Given: Simulate an error deep inside the logic 
        when(processInstance.getStartedBy()).thenThrow(new RuntimeException("Unexpected API error"));

        // When
        ProcessInitiator result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("unknown_user", result.userName());
    }

    // =========================================================================
    // SECTION 2: searchAndValidateId TESTS
    // =========================================================================

    @Test
    @DisplayName("searchAndValidateId should return object on valid ID string")
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
    
    @Test
    @DisplayName("searchAndValidateId should return null on null ID string")
    void searchAndValidateId_should_return_null_on_null_id_string() {
        // When
        MockBDM result = ProcessUtils.searchAndValidateId(null, searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }
    
    @Test
    @DisplayName("searchAndValidateId should return null on empty ID string")
    void searchAndValidateId_should_return_null_on_empty_id_string() {
        // When
        MockBDM result = ProcessUtils.searchAndValidateId("", searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }

    @Test
    @DisplayName("searchAndValidateId should throw exception on invalid ID format (NumberFormatException)")
    void searchAndValidateId_should_throw_runtime_exception_on_invalid_id_format() {
        // Use Mockito to simulate the behavior of ExceptionUtils.logAndThrow
        try (MockedStatic<ExceptionUtils> mocked = mockStatic(ExceptionUtils.class)) {
            // Setup the mock to THROW a RuntimeException when called, fulfilling assertThrows.
            mocked.when(() -> ExceptionUtils.logAndThrow(any(), anyString()))
                  .thenThrow(new RuntimeException(FAKE_ERROR_MESSAGE));
            
            // When / Then: RuntimeException must be thrown due to NumberFormatException
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                ProcessUtils.searchAndValidateId("abc", searchFunction, OBJECT_TYPE);
            });

            // Verify the mock was called
            mocked.verify(() -> ExceptionUtils.logAndThrow(any(), anyString()), times(1));
            assertEquals(FAKE_ERROR_MESSAGE, thrown.getMessage());
        }
    }

    @Test
    @DisplayName("searchAndValidateId should throw exception if object not found (internal searchAndValidate error)")
    void searchAndValidateId_should_throw_runtime_exception_if_object_not_found() {
        // Given: The search function returns null
        when(searchFunction.apply(PERSISTENCE_ID)).thenReturn(null);

        // Use Mockito to simulate the behavior of ExceptionUtils.logAndThrow
        try (MockedStatic<ExceptionUtils> mocked = mockStatic(ExceptionUtils.class)) {
            // Setup the mock to THROW a RuntimeException when called, fulfilling assertThrows.
            mocked.when(() -> ExceptionUtils.logAndThrow(any(), anyString()))
                  .thenThrow(new RuntimeException(FAKE_ERROR_MESSAGE));

            // When / Then: RuntimeException must be thrown due to object not found
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                ProcessUtils.searchAndValidateId(String.valueOf(PERSISTENCE_ID), searchFunction, OBJECT_TYPE);
            });

            // Verify the mock was called
            mocked.verify(() -> ExceptionUtils.logAndThrow(any(), anyString()), times(1));
            assertEquals(FAKE_ERROR_MESSAGE, thrown.getMessage());
        }
    }
    
    // =========================================================================
    // SECTION 3: validateActionAndDelete TESTS
    // =========================================================================

    @Test
    @DisplayName("validateActionAndDelete should return object on DELETE action if found")
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
    
    @Test
    @DisplayName("validateActionAndDelete should throw exception on DELETE action if not found")
    void validateActionAndDelete_should_throw_exception_on_delete_if_not_found() {
        // Use Mockito to simulate the behavior of ExceptionUtils.logAndThrow
        try (MockedStatic<ExceptionUtils> mocked = mockStatic(ExceptionUtils.class)) {
            // Setup the mock to THROW a RuntimeException when called, fulfilling assertThrows.
            mocked.when(() -> ExceptionUtils.logAndThrow(any(), anyString()))
                  .thenThrow(new RuntimeException(FAKE_ERROR_MESSAGE));

            // When / Then: RuntimeException must be thrown
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                ProcessUtils.validateActionAndDelete(
                        null, // Object is null (not found)
                        ActionType.DELETE.name(), 
                        PERSISTENCE_ID, 
                        OBJECT_TYPE
                );
            });
            // Verify the mock was called
            mocked.verify(() -> ExceptionUtils.logAndThrow(any(), anyString()), times(1));
            assertEquals(FAKE_ERROR_MESSAGE, thrown.getMessage());
        }
    }

    @Test
    @DisplayName("validateActionAndDelete should return null on non-DELETE action (INSERT/UPDATE/null)")
    void validateActionAndDelete_should_return_null_on_non_delete_action() {
        // Given
        MockBDM bdmObject = new MockBDM(PERSISTENCE_ID);
        
        // When: Action is "INSERT"
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
    
    @Test
    @DisplayName("validateActionAndDelete should handle mixed case 'DELETE' action")
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
    
    @Test
    @DisplayName("Constructor should throw UnsupportedOperationException")
    void constructor_should_throw_unsupported_operation_exception() throws Exception {
        // 1. Retrieve the Constructor object for the class.
        Constructor<ProcessUtils> constructor = ProcessUtils.class.getDeclaredConstructor();
        
        // 2. VERIFICATION: Ensure the constructor is PRIVATE.
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), 
                "The constructor must be private.");
        
        // 3. FORCE ACCESSIBILITY: Override the 'private' restriction for testing.
        constructor.setAccessible(true);
        
        // 4. Invoke the constructor and expect the wrapper exception (InvocationTargetException).
        InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance();
        }, "Invoking the private constructor should wrap the internal exception.");
        
        // 5. Verify the actual cause is the expected exception.
        Throwable actualCause = thrownException.getCause();
        assertTrue(actualCause instanceof UnsupportedOperationException, 
                "The internal exception (cause) must be UnsupportedOperationException.");
                
        final String expectedMessage = "This is a ProcessUtils class and cannot be instantiated.";
        // Note: The original test used a complex String replace that is not necessary here.
        assertEquals(expectedMessage, actualCause.getMessage(),
                    "The constructor's message should match the expected text.");
        
        // Optional: Revert the accessibility change after the test
        constructor.setAccessible(false);
    }
}