package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceNotFoundException;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstanceNotFoundException;
import org.bonitasoft.engine.exception.RetrieveException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.bonitasoft.engine.session.InvalidSessionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.bonitasoft.processbuilder.enums.ActionType;
import com.bonitasoft.processbuilder.records.UserRecord;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

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

    // --- Mock for HumanTaskInstance ---
    @Mock
    private HumanTaskInstance humanTaskInstance;

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
        UserRecord result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

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
        UserRecord result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

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
        UserRecord result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

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
        UserRecord result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

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
    // SECTION 4: searchById TESTS
    // =========================================================================

    @Test
    @DisplayName("searchById should return object when search function succeeds")
    void searchById_should_return_object_on_successful_search() {
        // Given
        MockBDM expectedObject = new MockBDM(PERSISTENCE_ID);
        when(searchFunction.apply(PERSISTENCE_ID)).thenReturn(expectedObject);

        // When
        MockBDM result = ProcessUtils.searchById(PERSISTENCE_ID, searchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertSame(expectedObject, result);
        verify(searchFunction, times(1)).apply(PERSISTENCE_ID);
    }

    @Test
    @DisplayName("searchById should return null when search function returns null")
    void searchById_should_return_null_when_object_not_found() {
        // Given
        when(searchFunction.apply(PERSISTENCE_ID)).thenReturn(null);

        // When
        MockBDM result = ProcessUtils.searchById(PERSISTENCE_ID, searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, times(1)).apply(PERSISTENCE_ID);
    }

    @Test
    @DisplayName("searchById should return null when persistenceId is null")
    void searchById_should_return_null_on_null_persistence_id() {
        // When
        MockBDM result = ProcessUtils.searchById(null, searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }

    @Test
    @DisplayName("searchById should return null when persistenceId is zero")
    void searchById_should_return_null_on_zero_persistence_id() {
        // When
        MockBDM result = ProcessUtils.searchById(0L, searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }

    @Test
    @DisplayName("searchById should return null when persistenceId is negative")
    void searchById_should_return_null_on_negative_persistence_id() {
        // When
        MockBDM result = ProcessUtils.searchById(-1L, searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }

    @Test
    @DisplayName("searchById should handle exceptions from search function gracefully")
    void searchById_should_return_null_when_search_function_throws_exception() {
        // Given
        when(searchFunction.apply(PERSISTENCE_ID)).thenThrow(new RuntimeException("Search error"));

        // When
        MockBDM result = ProcessUtils.searchById(PERSISTENCE_ID, searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, times(1)).apply(PERSISTENCE_ID);
    }

    @Test
    @DisplayName("searchById should work with different BDM types")
    void searchById_should_work_with_different_bdm_types() {
        // Given: Create a different search function
        Function<Long, String> stringSearchFunction = id -> "Found String for ID: " + id;

        // When
        String result = ProcessUtils.searchById(123L, stringSearchFunction, "StringObject");

        // Then
        assertNotNull(result);
        assertEquals("Found String for ID: 123", result);
    }

    @Test
    @DisplayName("searchById should call search function only once")
    void searchById_should_call_search_function_only_once() {
        // Given
        MockBDM expectedObject = new MockBDM(PERSISTENCE_ID);
        when(searchFunction.apply(PERSISTENCE_ID)).thenReturn(expectedObject);

        // When
        ProcessUtils.searchById(PERSISTENCE_ID, searchFunction, OBJECT_TYPE);

        // Then
        verify(searchFunction, times(1)).apply(PERSISTENCE_ID);
    }

    // =========================================================================
    // SECTION 5: Private Constructor Coverage
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
        assertEquals(expectedMessage, actualCause.getMessage(),
                    "The constructor's message should match the expected text.");
        
        // Optional: Revert the accessibility change after the test
        constructor.setAccessible(false);
    }

    // =========================================================================
    // SECTION 6: getTaskExecutor TESTS
    // =========================================================================

    @Test
    @DisplayName("Should successfully retrieve the task executor details on success")
    void getTaskExecutor_should_return_valid_executor_on_success() throws Exception {
        // Given
        long ACTIVITY_INSTANCE_ID = 200L;
        when(processApi.getHumanTaskInstance(ACTIVITY_INSTANCE_ID)).thenReturn(humanTaskInstance);
        when(humanTaskInstance.getExecutedBy()).thenReturn(USER_ID);
        when(identityAPI.getUser(USER_ID)).thenReturn(user);

        // When
        UserRecord result = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertEquals(USER_ID, result.id());
        assertEquals(USER_NAME, result.userName());
        assertEquals(FIRST_NAME + " " + LAST_NAME, result.fullName());
        verify(processApi, times(1)).getHumanTaskInstance(ACTIVITY_INSTANCE_ID);
        verify(identityAPI, times(1)).getUser(USER_ID);
    }

    @Test
    @DisplayName("Should return 'system_or_unassigned' when executedBy is 0")
    void getTaskExecutor_should_return_system_on_unassigned_task() throws Exception {
        // Given
        long ACTIVITY_INSTANCE_ID = 200L;
        when(processApi.getHumanTaskInstance(ACTIVITY_INSTANCE_ID)).thenReturn(humanTaskInstance);
        when(humanTaskInstance.getExecutedBy()).thenReturn(0L);

        // When
        UserRecord result = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertEquals(0L, result.id());
        assertEquals("system_or_unassigned", result.userName());
        assertEquals("System or Unassigned", result.fullName());
        verify(identityAPI, never()).getUser(anyLong());
    }

    @Test
    @DisplayName("Should return 'system_or_unassigned' when executedBy is negative")
    void getTaskExecutor_should_return_system_on_negative_executed_by() throws Exception {
        // Given
        long ACTIVITY_INSTANCE_ID = 200L;
        when(processApi.getHumanTaskInstance(ACTIVITY_INSTANCE_ID)).thenReturn(humanTaskInstance);
        when(humanTaskInstance.getExecutedBy()).thenReturn(-1L);

        // When
        UserRecord result = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertEquals(-1L, result.id());
        assertEquals("system_or_unassigned", result.userName());
        assertEquals("System or Unassigned", result.fullName());
        verify(identityAPI, never()).getUser(anyLong());
    }

    @Test
    @DisplayName("Should return 'unknown_user' on UserNotFoundException")
    void getTaskExecutor_should_return_unknown_user_on_UserNotFoundException() throws Exception {
        // Given
        long ACTIVITY_INSTANCE_ID = 200L;
        when(processApi.getHumanTaskInstance(ACTIVITY_INSTANCE_ID)).thenReturn(humanTaskInstance);
        when(humanTaskInstance.getExecutedBy()).thenReturn(USER_ID);
        when(identityAPI.getUser(USER_ID)).thenThrow(new UserNotFoundException("User not found"));

        // When
        UserRecord result = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("unknown_user", result.userName());
        assertEquals("Unknown User", result.fullName());
    }

    @Test
    @DisplayName("Should return 'api_error' on InvalidSessionException")
    void getTaskExecutor_should_return_api_error_on_InvalidSessionException() throws Exception {
        // Given
        long ACTIVITY_INSTANCE_ID = 200L;
        when(processApi.getHumanTaskInstance(ACTIVITY_INSTANCE_ID))
            .thenThrow(new InvalidSessionException("Invalid session"));

        // When
        UserRecord result = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("api_error", result.userName());
        assertEquals("API Error", result.fullName());
    }

    @Test
    @DisplayName("Should return 'api_error' on ActivityInstanceNotFoundException")
    void getTaskExecutor_should_return_api_error_on_ActivityInstanceNotFoundException() throws Exception {
        // Given
        long ACTIVITY_INSTANCE_ID = 200L;
        when(processApi.getHumanTaskInstance(ACTIVITY_INSTANCE_ID))
            .thenThrow(new ActivityInstanceNotFoundException(ACTIVITY_INSTANCE_ID));

        // When
        UserRecord result = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("api_error", result.userName());
        assertEquals("API Error", result.fullName());
    }

    @Test
    @DisplayName("Should return 'api_error' on RetrieveException")
    void getTaskExecutor_should_return_api_error_on_RetrieveException() throws Exception {
        // Given
        long ACTIVITY_INSTANCE_ID = 200L;
        when(processApi.getHumanTaskInstance(ACTIVITY_INSTANCE_ID))
            .thenThrow(new RetrieveException("Retrieve error"));

        // When
        UserRecord result = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("api_error", result.userName());
        assertEquals("API Error", result.fullName());
    }

    @Test
    @DisplayName("Should return 'unexpected_error' on any other unexpected exception")
    void getTaskExecutor_should_return_unexpected_error_on_generic_exception() throws Exception {
        // Given
        long ACTIVITY_INSTANCE_ID = 200L;
        when(processApi.getHumanTaskInstance(ACTIVITY_INSTANCE_ID))
            .thenThrow(new RuntimeException("Unexpected error"));

        // When
        UserRecord result = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("unexpected_error", result.userName());
        assertEquals("Unexpected Error", result.fullName());
    }

    @Test
    @DisplayName("Should handle exception when getExecutedBy throws exception")
    void getTaskExecutor_should_handle_exception_on_getExecutedBy() throws Exception {
        // Given
        long ACTIVITY_INSTANCE_ID = 200L;
        when(processApi.getHumanTaskInstance(ACTIVITY_INSTANCE_ID)).thenReturn(humanTaskInstance);
        when(humanTaskInstance.getExecutedBy()).thenThrow(new RuntimeException("Error getting executed by"));

        // When
        UserRecord result = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("unexpected_error", result.userName());
        assertEquals("Unexpected Error", result.fullName());
    }

    @Test
    @DisplayName("Should verify correct method calls on successful retrieval")
    void getTaskExecutor_should_verify_correct_method_calls_on_success() throws Exception {
        // Given
        long ACTIVITY_INSTANCE_ID = 200L;
        when(processApi.getHumanTaskInstance(ACTIVITY_INSTANCE_ID)).thenReturn(humanTaskInstance);
        when(humanTaskInstance.getExecutedBy()).thenReturn(USER_ID);
        when(identityAPI.getUser(USER_ID)).thenReturn(user);

        // When
        ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_INSTANCE_ID);

        // Then: Verify all interactions occurred
        verify(processApi, times(1)).getHumanTaskInstance(ACTIVITY_INSTANCE_ID);
        verify(humanTaskInstance, times(1)).getExecutedBy();
        verify(identityAPI, times(1)).getUser(USER_ID);
        verify(user, times(1)).getFirstName();
        verify(user, times(1)).getLastName();
        verify(user, times(1)).getUserName();
    }

    @Test
    @DisplayName("Should handle multiple consecutive calls with different task instances")
    void getTaskExecutor_should_handle_multiple_calls() throws Exception {
        // Given
        long ACTIVITY_ID_1 = 201L;
        long ACTIVITY_ID_2 = 202L;
        long USER_ID_2 = 124L;

        User user2 = mock(User.class);
        when(user2.getFirstName()).thenReturn("Jane");
        when(user2.getLastName()).thenReturn("Smith");
        when(user2.getUserName()).thenReturn("jsmith");

        // Setup first call
        HumanTaskInstance task1 = mock(HumanTaskInstance.class);
        when(processApi.getHumanTaskInstance(ACTIVITY_ID_1)).thenReturn(task1);
        when(task1.getExecutedBy()).thenReturn(USER_ID);
        when(identityAPI.getUser(USER_ID)).thenReturn(user);

        // Setup second call
        HumanTaskInstance task2 = mock(HumanTaskInstance.class);
        when(processApi.getHumanTaskInstance(ACTIVITY_ID_2)).thenReturn(task2);
        when(task2.getExecutedBy()).thenReturn(USER_ID_2);
        when(identityAPI.getUser(USER_ID_2)).thenReturn(user2);

        // When
        UserRecord result1 = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_ID_1);
        UserRecord result2 = ProcessUtils.getTaskExecutor(apiAccessor, ACTIVITY_ID_2);

        // Then
        assertNotNull(result1);
        assertEquals(USER_ID, result1.id());
        assertEquals(USER_NAME, result1.userName());

        assertNotNull(result2);
        assertEquals(USER_ID_2, result2.id());
        assertEquals("jsmith", result2.userName());
    }

    // =========================================================================
    // SECTION 7: searchBDM TESTS
    // =========================================================================

    @Test
    @DisplayName("searchBDM should return object when persistenceId is valid")
    void searchBDM_should_return_object_on_valid_persistence_id() {
        // Given
        MockBDM expectedObject = new MockBDM(PERSISTENCE_ID);
        when(searchFunction.apply(PERSISTENCE_ID)).thenReturn(expectedObject);

        // When
        MockBDM result = ProcessUtils.searchBDM(String.valueOf(PERSISTENCE_ID), searchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertSame(expectedObject, result);
        verify(searchFunction, times(1)).apply(PERSISTENCE_ID);
    }

    @Test
    @DisplayName("searchBDM should return null when persistenceId is null")
    void searchBDM_should_return_null_on_null_persistence_id() {
        // When
        MockBDM result = ProcessUtils.searchBDM(null, searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }

    @Test
    @DisplayName("searchBDM should return null when persistenceId is empty string")
    void searchBDM_should_return_null_on_empty_persistence_id() {
        // When
        MockBDM result = ProcessUtils.searchBDM("", searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }

    @Test
    @DisplayName("searchBDM should return null when persistenceId is blank string")
    void searchBDM_should_return_null_on_blank_persistence_id() {
        // When
        MockBDM result = ProcessUtils.searchBDM("   ", searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }

    @Test
    @DisplayName("searchBDM should return null when persistenceId format is invalid")
    void searchBDM_should_return_null_on_invalid_persistence_id_format() {
        // When
        MockBDM result = ProcessUtils.searchBDM("abc123", searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, never()).apply(anyLong());
    }

    @Test
    @DisplayName("searchBDM should return null when search function returns null")
    void searchBDM_should_return_null_when_search_function_returns_null() {
        // Given
        when(searchFunction.apply(PERSISTENCE_ID)).thenReturn(null);

        // When
        MockBDM result = ProcessUtils.searchBDM(String.valueOf(PERSISTENCE_ID), searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, times(1)).apply(PERSISTENCE_ID);
    }

    @Test
    @DisplayName("searchBDM should handle persistenceId with leading/trailing spaces")
    void searchBDM_should_trim_persistence_id() {
        // Given
        MockBDM expectedObject = new MockBDM(PERSISTENCE_ID);
        when(searchFunction.apply(PERSISTENCE_ID)).thenReturn(expectedObject);

        // When
        MockBDM result = ProcessUtils.searchBDM("  " + PERSISTENCE_ID + "  ", searchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertSame(expectedObject, result);
        verify(searchFunction, times(1)).apply(PERSISTENCE_ID);
    }

    @Test
    @DisplayName("searchBDM should return null when search function throws exception")
    void searchBDM_should_return_null_when_search_function_throws_exception() {
        // Given
        when(searchFunction.apply(PERSISTENCE_ID)).thenThrow(new RuntimeException("Search error"));

        // When
        MockBDM result = ProcessUtils.searchBDM(String.valueOf(PERSISTENCE_ID), searchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
        verify(searchFunction, times(1)).apply(PERSISTENCE_ID);
    }

    // =========================================================================
    // SECTION 8: searchByStringKey TESTS
    // =========================================================================

    @Test
    @DisplayName("searchByStringKey should return object when searchKey is valid")
    void searchByStringKey_should_return_object_on_valid_search_key() {
        // Given
        String searchKey = "ACTION_REF_001";
        MockBDM expectedObject = new MockBDM(PERSISTENCE_ID);
        Function<String, MockBDM> stringSearchFunction = key -> expectedObject;

        // When
        MockBDM result = ProcessUtils.searchByStringKey(searchKey, stringSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertSame(expectedObject, result);
    }

    @Test
    @DisplayName("searchByStringKey should return null when searchKey is null")
    void searchByStringKey_should_return_null_on_null_search_key() {
        // Given
        Function<String, MockBDM> stringSearchFunction = key -> new MockBDM(PERSISTENCE_ID);

        // When
        MockBDM result = ProcessUtils.searchByStringKey(null, stringSearchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("searchByStringKey should return null when searchKey is empty string")
    void searchByStringKey_should_return_null_on_empty_search_key() {
        // Given
        Function<String, MockBDM> stringSearchFunction = key -> new MockBDM(PERSISTENCE_ID);

        // When
        MockBDM result = ProcessUtils.searchByStringKey("", stringSearchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("searchByStringKey should return null when searchKey is blank string")
    void searchByStringKey_should_return_null_on_blank_search_key() {
        // Given
        Function<String, MockBDM> stringSearchFunction = key -> new MockBDM(PERSISTENCE_ID);

        // When
        MockBDM result = ProcessUtils.searchByStringKey("   ", stringSearchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("searchByStringKey should return null when search function returns null")
    void searchByStringKey_should_return_null_when_search_function_returns_null() {
        // Given
        String searchKey = "NON_EXISTENT_KEY";
        Function<String, MockBDM> stringSearchFunction = key -> null;

        // When
        MockBDM result = ProcessUtils.searchByStringKey(searchKey, stringSearchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("searchByStringKey should handle searchKey with leading/trailing spaces")
    void searchByStringKey_should_trim_search_key() {
        // Given
        String searchKeyWithSpaces = "  ACTION_REF_001  ";
        String expectedTrimmedKey = "ACTION_REF_001";
        MockBDM expectedObject = new MockBDM(PERSISTENCE_ID);
        final String[] capturedKey = new String[1];
        Function<String, MockBDM> stringSearchFunction = key -> {
            capturedKey[0] = key;
            return expectedObject;
        };

        // When
        MockBDM result = ProcessUtils.searchByStringKey(searchKeyWithSpaces, stringSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertSame(expectedObject, result);
        assertEquals(expectedTrimmedKey, capturedKey[0]);
    }

    @Test
    @DisplayName("searchByStringKey should return null when search function throws exception")
    void searchByStringKey_should_return_null_when_search_function_throws_exception() {
        // Given
        String searchKey = "ACTION_REF_001";
        Function<String, MockBDM> stringSearchFunction = key -> {
            throw new RuntimeException("Database error");
        };

        // When
        MockBDM result = ProcessUtils.searchByStringKey(searchKey, stringSearchFunction, OBJECT_TYPE);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("searchByStringKey should work with different generic types")
    void searchByStringKey_should_work_with_different_generic_types() {
        // Given
        Function<String, String> stringSearchFunction = key -> "Found: " + key;

        // When
        String result = ProcessUtils.searchByStringKey("MY_KEY", stringSearchFunction, "StringObject");

        // Then
        assertNotNull(result);
        assertEquals("Found: MY_KEY", result);
    }

    @Test
    @DisplayName("searchByStringKey should pass correct key to search function")
    void searchByStringKey_should_pass_correct_key_to_search_function() {
        // Given
        String expectedKey = "ACTION_REF_002";
        final String[] capturedKey = new String[1];
        Function<String, MockBDM> stringSearchFunction = key -> {
            capturedKey[0] = key;
            return new MockBDM(PERSISTENCE_ID);
        };

        // When
        ProcessUtils.searchByStringKey(expectedKey, stringSearchFunction, OBJECT_TYPE);

        // Then
        assertEquals(expectedKey, capturedKey[0]);
    }

    @Test
    @DisplayName("searchByStringKey should handle special characters in key")
    void searchByStringKey_should_handle_special_characters_in_key() {
        // Given
        String keyWithSpecialChars = "ACTION-REF_001/v2.0";
        MockBDM expectedObject = new MockBDM(PERSISTENCE_ID);
        Function<String, MockBDM> stringSearchFunction = key -> expectedObject;

        // When
        MockBDM result = ProcessUtils.searchByStringKey(keyWithSpecialChars, stringSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertSame(expectedObject, result);
    }

    @Test
    @DisplayName("searchByStringKey should handle multiple consecutive calls independently")
    void searchByStringKey_should_handle_multiple_consecutive_calls() {
        // Given
        MockBDM object1 = new MockBDM(1L);
        MockBDM object2 = new MockBDM(2L);
        Function<String, MockBDM> function1 = key -> object1;
        Function<String, MockBDM> function2 = key -> object2;

        // When
        MockBDM result1 = ProcessUtils.searchByStringKey("KEY_1", function1, OBJECT_TYPE);
        MockBDM result2 = ProcessUtils.searchByStringKey("KEY_2", function2, OBJECT_TYPE);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertSame(object1, result1);
        assertSame(object2, result2);
        assertNotEquals(result1.getId(), result2.getId());
    }

    // =========================================================================
    // SECTION 9: findMostRecentStepInstance TESTS
    // =========================================================================

    @Test
    @DisplayName("findMostRecentStepInstance should return first element when list has one element")
    void findMostRecentStepInstance_should_return_first_element_from_single_element_list() {
        // Given
        MockBDM expectedInstance = new MockBDM(100L);
        Supplier<List<MockBDM>> searchSupplier = () -> Collections.singletonList(expectedInstance);

        // When
        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertSame(expectedInstance, result);
    }

    @Test
    @DisplayName("findMostRecentStepInstance should return first element when list has multiple elements")
    void findMostRecentStepInstance_should_return_first_element_from_multiple_element_list() {
        // Given
        MockBDM mostRecent = new MockBDM(100L);
        MockBDM secondMostRecent = new MockBDM(99L);
        MockBDM thirdMostRecent = new MockBDM(98L);
        Supplier<List<MockBDM>> searchSupplier = () -> List.of(mostRecent, secondMostRecent, thirdMostRecent);

        // When
        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertSame(mostRecent, result);
        assertEquals(100L, result.getId());
    }

    @Test
    @DisplayName("findMostRecentStepInstance should return null when list is null")
    void findMostRecentStepInstance_should_return_null_when_list_is_null() {
        // Given
        Supplier<List<MockBDM>> searchSupplier = () -> null;

        // When
        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("findMostRecentStepInstance should return null when list is empty")
    void findMostRecentStepInstance_should_return_null_when_list_is_empty() {
        // Given
        Supplier<List<MockBDM>> searchSupplier = Collections::emptyList;

        // When
        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("findMostRecentStepInstance should return null when supplier throws exception")
    void findMostRecentStepInstance_should_return_null_when_supplier_throws_exception() {
        // Given
        Supplier<List<MockBDM>> searchSupplier = () -> {
            throw new RuntimeException("Database error");
        };

        // When
        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("findMostRecentStepInstance should work with different generic types")
    void findMostRecentStepInstance_should_work_with_different_generic_types() {
        // Given: Use String type instead of MockBDM
        Supplier<List<String>> searchSupplier = () -> List.of("Most Recent", "Second", "Third");

        // When
        String result = ProcessUtils.findMostRecentStepInstance(searchSupplier, "StepInstance");

        // Then
        assertNotNull(result);
        assertEquals("Most Recent", result);
    }

    @Test
    @DisplayName("findMostRecentStepInstance should handle supplier that returns null elements in list")
    void findMostRecentStepInstance_should_handle_list_with_null_element() {
        // Given: List with null as first element
        Supplier<List<MockBDM>> searchSupplier = () -> Collections.singletonList(null);

        // When
        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        // Then: Should return the null element (not throw exception)
        assertNull(result);
    }

    @Test
    @DisplayName("findMostRecentStepInstance should call supplier only once")
    void findMostRecentStepInstance_should_call_supplier_only_once() {
        // Given
        @SuppressWarnings("unchecked")
        Supplier<List<MockBDM>> mockSupplier = mock(Supplier.class);
        MockBDM expectedInstance = new MockBDM(100L);
        when(mockSupplier.get()).thenReturn(Collections.singletonList(expectedInstance));

        // When
        ProcessUtils.findMostRecentStepInstance(mockSupplier, OBJECT_TYPE);

        // Then
        verify(mockSupplier, times(1)).get();
    }

    @Test
    @DisplayName("findMostRecentStepInstance should handle large lists efficiently")
    void findMostRecentStepInstance_should_handle_large_lists() {
        // Given: Create a large list
        List<MockBDM> largeList = new java.util.ArrayList<>();
        MockBDM firstElement = new MockBDM(1000L);
        largeList.add(firstElement);
        for (int i = 1; i < 1000; i++) {
            largeList.add(new MockBDM((long) i));
        }
        Supplier<List<MockBDM>> searchSupplier = () -> largeList;

        // When
        MockBDM result = ProcessUtils.findMostRecentStepInstance(searchSupplier, OBJECT_TYPE);

        // Then: Should return first element without processing entire list
        assertNotNull(result);
        assertSame(firstElement, result);
        assertEquals(1000L, result.getId());
    }

    @Test
    @DisplayName("findMostRecentStepInstance should handle consecutive calls independently")
    void findMostRecentStepInstance_should_handle_multiple_consecutive_calls() {
        // Given: Two different suppliers
        MockBDM instance1 = new MockBDM(100L);
        MockBDM instance2 = new MockBDM(200L);
        Supplier<List<MockBDM>> supplier1 = () -> Collections.singletonList(instance1);
        Supplier<List<MockBDM>> supplier2 = () -> Collections.singletonList(instance2);

        // When
        MockBDM result1 = ProcessUtils.findMostRecentStepInstance(supplier1, OBJECT_TYPE);
        MockBDM result2 = ProcessUtils.findMostRecentStepInstance(supplier2, OBJECT_TYPE);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertSame(instance1, result1);
        assertSame(instance2, result2);
        assertNotEquals(result1.getId(), result2.getId());
    }

    // =========================================================================
    // SECTION 10: searchBDMList TESTS
    // =========================================================================

    @Test
    @DisplayName("searchBDMList should return list when persistenceId is valid and results exist")
    void searchBDMList_should_return_list_on_valid_persistence_id() {
        // Given
        MockBDM item1 = new MockBDM(1L);
        MockBDM item2 = new MockBDM(2L);
        List<MockBDM> expectedList = List.of(item1, item2);
        Function<Long, List<MockBDM>> listSearchFunction = id -> expectedList;

        // When
        List<MockBDM> result = ProcessUtils.searchBDMList(PERSISTENCE_ID, listSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(item1, result.get(0));
        assertSame(item2, result.get(1));
    }

    @Test
    @DisplayName("searchBDMList should return empty list when persistenceId is null")
    void searchBDMList_should_return_empty_list_on_null_persistence_id() {
        // Given
        Function<Long, List<MockBDM>> listSearchFunction = id -> List.of(new MockBDM(1L));

        // When
        List<MockBDM> result = ProcessUtils.searchBDMList(null, listSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("searchBDMList should return empty list when persistenceId is zero")
    void searchBDMList_should_return_empty_list_on_zero_persistence_id() {
        // Given
        Function<Long, List<MockBDM>> listSearchFunction = id -> List.of(new MockBDM(1L));

        // When
        List<MockBDM> result = ProcessUtils.searchBDMList(0L, listSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("searchBDMList should return empty list when persistenceId is negative")
    void searchBDMList_should_return_empty_list_on_negative_persistence_id() {
        // Given
        Function<Long, List<MockBDM>> listSearchFunction = id -> List.of(new MockBDM(1L));

        // When
        List<MockBDM> result = ProcessUtils.searchBDMList(-1L, listSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("searchBDMList should return empty list when search function returns null")
    void searchBDMList_should_return_empty_list_when_search_returns_null() {
        // Given
        Function<Long, List<MockBDM>> listSearchFunction = id -> null;

        // When
        List<MockBDM> result = ProcessUtils.searchBDMList(PERSISTENCE_ID, listSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("searchBDMList should return empty list when search function returns empty list")
    void searchBDMList_should_return_empty_list_when_search_returns_empty() {
        // Given
        Function<Long, List<MockBDM>> listSearchFunction = id -> Collections.emptyList();

        // When
        List<MockBDM> result = ProcessUtils.searchBDMList(PERSISTENCE_ID, listSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("searchBDMList should return empty list when search function throws exception")
    void searchBDMList_should_return_empty_list_when_search_throws_exception() {
        // Given
        Function<Long, List<MockBDM>> listSearchFunction = id -> {
            throw new RuntimeException("Database error");
        };

        // When
        List<MockBDM> result = ProcessUtils.searchBDMList(PERSISTENCE_ID, listSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("searchBDMList should work with different generic types")
    void searchBDMList_should_work_with_different_generic_types() {
        // Given
        Function<Long, List<String>> stringSearchFunction = id -> List.of("Item1", "Item2", "Item3");

        // When
        List<String> result = ProcessUtils.searchBDMList(100L, stringSearchFunction, "StringObject");

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Item1", result.get(0));
    }

    @Test
    @DisplayName("searchBDMList should handle large result lists")
    void searchBDMList_should_handle_large_result_lists() {
        // Given: Create a large list
        List<MockBDM> largeList = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeList.add(new MockBDM((long) i));
        }
        Function<Long, List<MockBDM>> listSearchFunction = id -> largeList;

        // When
        List<MockBDM> result = ProcessUtils.searchBDMList(PERSISTENCE_ID, listSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertEquals(1000, result.size());
    }

    @Test
    @DisplayName("searchBDMList should pass correct persistenceId to search function")
    void searchBDMList_should_pass_correct_persistence_id() {
        // Given
        final long[] capturedId = new long[1];
        Function<Long, List<MockBDM>> listSearchFunction = id -> {
            capturedId[0] = id;
            return List.of(new MockBDM(id));
        };

        // When
        ProcessUtils.searchBDMList(PERSISTENCE_ID, listSearchFunction, OBJECT_TYPE);

        // Then
        assertEquals(PERSISTENCE_ID, capturedId[0]);
    }

    @Test
    @DisplayName("searchBDMList should handle single item list")
    void searchBDMList_should_handle_single_item_list() {
        // Given
        MockBDM singleItem = new MockBDM(100L);
        Function<Long, List<MockBDM>> listSearchFunction = id -> Collections.singletonList(singleItem);

        // When
        List<MockBDM> result = ProcessUtils.searchBDMList(PERSISTENCE_ID, listSearchFunction, OBJECT_TYPE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(singleItem, result.get(0));
    }
}