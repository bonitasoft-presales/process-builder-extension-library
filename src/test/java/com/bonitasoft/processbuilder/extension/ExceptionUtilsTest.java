package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bonitasoft.processbuilder.constants.Constants;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

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

     /**
     * Tests that the supplier-based method correctly throws the provided exception type.
     * This test validates the core functionality of the method.
     */
    @Test
    void logAndThrow_should_throw_exception_from_supplier_with_message() {
        // GIVEN a Supplier that returns a new IllegalArgumentException (with message)
        final String expectedFormat = "Test format message: {}";
        final String arg = "SupplierTest";
        final String expectedMessage = "Supplier message"; // The message is hardcoded in the supplier

        // WHEN the method is called
        // THEN it should throw the IllegalArgumentException provided by the supplier
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            // Note: The format/args are logged, but the thrown exception message comes from the supplier
            ExceptionUtils.logAndThrow(() -> new IllegalArgumentException(expectedMessage), expectedFormat, arg);
        });

        // THEN the thrown exception's message must match the one hardcoded in the supplier
        assertEquals(expectedMessage, thrownException.getMessage(), "The thrown exception should carry the message provided in the Supplier.");
    }
    
    /**
     * Tests that the supplier-based method works correctly when throwing a checked exception.
     * This confirms the generic type parameter <T> handles checked exceptions as expected.
     */
    @Test
    void logAndThrow_should_handle_checked_exception() {
        // GIVEN a checked exception type (e.g., Exception)
        final String expectedMessage = "Checked exception test";
        
        // WHEN the method is called
        // THEN it should throw the checked exception
        Exception thrownException = assertThrows(Exception.class, () -> {
            ExceptionUtils.logAndThrow(() -> new Exception(expectedMessage), "Format not important here");
        });
        
        // THEN the message must match the one provided in the supplier
        assertEquals(expectedMessage, thrownException.getMessage(), "The thrown checked exception should carry the message from the Supplier.");
    }
    
    // --- Tests for the new logAndThrowWithMessage (Function-based) method ---

    /**
     * Tests that the function-based method correctly throws the specified exception type 
     * and ensures the exception message is the *formatted message*.
     * This is the crucial test for solving the "No message" issue.
     */
    @Test
    void logAndThrowWithMessage_should_throw_exception_with_formatted_message() {
        // GIVEN a format string and arguments
        final String format = "The value '{}' caused an error due to condition: {}.";
        final Object[] args = {"12345", "InvalidInput"};
        final String expectedMessage = String.format(format, args); // The message we expect

        // WHEN the method is called with a Function that takes a String (the message)
        // THEN it should throw a RuntimeException
        Exception thrownException = assertThrows(RuntimeException.class, () -> {
            ExceptionUtils.logAndThrowWithMessage(
                RuntimeException::new, // Pass the constructor reference (Function<String, RuntimeException>)
                format, 
                args
            );
        });

        // THEN the thrown exception's message must exactly match the formatted message string
        assertEquals(expectedMessage, thrownException.getMessage(), "The thrown exception must contain the full formatted message.");
    }

    /**
     * Tests the function-based method with a different exception type (IllegalArgumentException).
     * This ensures the generic typing and Function application work across various exception classes.
     */
    @Test
    void logAndThrowWithMessage_should_throw_specified_exception_type() {
        // GIVEN a specific exception type and message data
        final String format = "Uniqueness violation for category: {}.";
        final String categoryName = "Finance_Reports";
        final String expectedMessage = String.format(format, categoryName);

        // WHEN the method is called using a lambda to construct IllegalArgumentException
        // THEN it should throw IllegalArgumentException
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            ExceptionUtils.logAndThrowWithMessage(
                msg -> new IllegalArgumentException(msg), // Use lambda for explicit construction
                format, 
                categoryName
            );
        });

        // THEN the thrown exception type and message should be correct
        assertTrue(thrownException instanceof IllegalArgumentException, "The thrown exception must be of type IllegalArgumentException.");
        assertEquals(expectedMessage, thrownException.getMessage(), "The thrown exception must contain the correct formatted message.");
    }

    /**
     * Tests the private constructor to ensure the utility class cannot be instantiated, 
     * enforcing its static nature and achieving code coverage on the constructor.
     */
    @Test
    void constructor_should_throw_unsupported_operation_exception() throws Exception {
        // 1. Retrieve the Constructor object for the class.
        Constructor<ExceptionUtils> constructor = ExceptionUtils.class.getDeclaredConstructor();
        
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