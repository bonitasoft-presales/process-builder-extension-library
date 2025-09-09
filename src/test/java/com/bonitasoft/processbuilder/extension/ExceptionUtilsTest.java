package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExceptionUtilsTest {

    /**
     * Tests that a valid exception class is correctly instantiated and thrown.
     * This test verifies the core functionality of logAndThrowError.
     */
    @Test
    void logAndThrowError_should_throw_expected_exception_when_valid_class_is_provided() {
        // When a valid exception class is provided
        // Then the method should throw an instance of that class
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            ExceptionUtils.logAndThrowError(IllegalArgumentException.class, "Test error message for IllegalArgumentException");
        });

        assertEquals("Test error message for IllegalArgumentException", thrownException.getMessage());
    }

    /**
     * Tests that the method throws a RuntimeException when the provided exception class
     * does not have a constructor with a single String argument.
     * This test validates the error handling for reflection-related failures.
     */
    @Test
    void logAndThrowError_should_throw_RuntimeException_when_constructor_is_missing() {
        // Given an exception class without a (String) constructor
        class ExceptionWithoutStringConstructor extends Exception {
        }

        // When the method is called with this class
        // Then it should throw a RuntimeException because it cannot be instantiated
        Exception thrownException = assertThrows(RuntimeException.class, () -> {
            ExceptionUtils.logAndThrowError(ExceptionWithoutStringConstructor.class, "This should not be thrown");
        });
        
        assertEquals("Failed to instantiate the specified exception class.", thrownException.getMessage());
    }

    /**
     * Tests that the method correctly handles an exception that does not have a public constructor with a single String argument.
     * This test ensures that the InvocationTargetException is caught and handled properly.
     */
    @Test
    void logAndThrowError_should_throw_RuntimeException_when_exception_has_no_string_constructor() {
        // Given an exception class that cannot be instantiated via the expected constructor
        class ExceptionWithoutStringConstructor extends Exception {
            public ExceptionWithoutStringConstructor(Integer someNumber) {
                super("Test message");
            }
        }

        // When the method is called with this class, it should throw a RuntimeException
        Exception thrownException = assertThrows(RuntimeException.class, () -> {
            ExceptionUtils.logAndThrowError(ExceptionWithoutStringConstructor.class, "This should fail");
        });
        
        // Then the message should match the one from the catch block
        assertEquals("Failed to instantiate the specified exception class.", thrownException.getMessage());
    }

    



}