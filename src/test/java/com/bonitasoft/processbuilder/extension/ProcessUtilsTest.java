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
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProcessUtilsTest {

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

    private static final long PROCESS_INSTANCE_ID = 100L;
    private static final long USER_ID = 123L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String USER_NAME = "jdoe";

    @BeforeEach
    void setUp() throws Exception {
        // Mock the general behavior of the APIs
        when(apiAccessor.getProcessAPI()).thenReturn(processApi);
        when(apiAccessor.getIdentityAPI()).thenReturn(identityAPI);

        // Mock the happy path scenario for the process instance and user
        when(processApi.getProcessInstance(PROCESS_INSTANCE_ID)).thenReturn(processInstance);
        when(processInstance.getStartedBy()).thenReturn(USER_ID);
        when(identityAPI.getUser(USER_ID)).thenReturn(user);
        when(user.getFirstName()).thenReturn(FIRST_NAME);
        when(user.getLastName()).thenReturn(LAST_NAME);
        when(user.getUserName()).thenReturn(USER_NAME);
    }

    /**
     * Tests the successful retrieval of the process initiator.
     */
    @Test
    void getProcessInitiator_should_return_valid_initiator_on_success() {
        // Given
        // The mocks are already set up in the @BeforeEach method for a successful scenario.

        // When
        ProcessUtils.ProcessInitiator result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertEquals(USER_ID, result.id());
        assertEquals(USER_NAME, result.userName());
        assertEquals(FIRST_NAME + " " + LAST_NAME, result.fullName());
    }

    /**
     * Tests the graceful handling of a UserNotFoundException.
     */
    @Test
    void getProcessInitiator_should_return_unknown_user_on_UserNotFoundException() throws Exception {
        // Given
        when(identityAPI.getUser(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        // When
        ProcessUtils.ProcessInitiator result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("unknown_user", result.userName());
        assertEquals("unknown_user", result.fullName());
    }

    /**
     * Tests the graceful handling of a ProcessInstanceNotFoundException.
     */
    @Test
    void getProcessInitiator_should_return_unknown_user_on_ProcessInstanceNotFoundException() throws Exception {
        // Given
        when(processApi.getProcessInstance(anyLong())).thenThrow(new ProcessInstanceNotFoundException("Process instance not found"));

        // When
        ProcessUtils.ProcessInitiator result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("unknown_user", result.userName());
        assertEquals("unknown_user", result.fullName());
    }

    /**
     * Tests the graceful handling of any other unexpected exception.
     */
    @Test
    void getProcessInitiator_should_return_unknown_user_on_any_other_exception() throws Exception {
        // Given
        when(identityAPI.getUser(anyLong())).thenThrow(new RuntimeException("Unexpected error"));

        // When
        ProcessUtils.ProcessInitiator result = ProcessUtils.getProcessInitiator(apiAccessor, PROCESS_INSTANCE_ID);

        // Then
        assertNotNull(result);
        assertNull(result.id());
        assertEquals("unknown_user", result.userName());
        assertEquals("unknown_user", result.fullName());
    }
}