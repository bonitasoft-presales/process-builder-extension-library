package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExceptionUtilsTest {
   
    /**
     * Tests that the supplier-based method correctly throws the provided exception.
     * This test ensures that the logAndThrow method's core functionality works.
     */
    @Test
    void logAndThrow_should_throw_exception_from_supplier() {
        // Given a Supplier that returns a new IllegalArgumentException
        final String errorMessage = "Test error message for Supplier";

        // When the method is called
        // Then it should throw the exception provided by the supplier
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            ExceptionUtils.logAndThrow(() -> new IllegalArgumentException(errorMessage), errorMessage);
        });
        
        // Then the thrown exception's message should match the one passed to the supplier
        assertEquals(errorMessage, thrownException.getMessage());
    }
}