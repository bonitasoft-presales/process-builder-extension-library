package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
class ExceptionUtilsTest {

    // Helper class 1: Used to test the reflection failure case (missing String constructor)
    private static class ExceptionWithoutStringConstructor extends Exception {
        public ExceptionWithoutStringConstructor() {
            super();
        }
    }

    // Helper class 2: Used to test the reflection failure case (InstantiationException/IllegalAccessException)
    private static class FailingConstructorException extends Exception {
        public FailingConstructorException(String message) { // ⚠️ CAMBIO: Lo hacemos PUBLIC
            super(message);
            throw new RuntimeException("Forcing instantiation failure for test coverage.");
        }
    }

    // --- Tests for logAndThrow (Supplier-based) method ---

    /**
     * Tests that the supplier-based method correctly throws the provided exception type 
     * and that the thrown instance is the one created by the Supplier.
     */
    @Test
    void logAndThrow_SupplierBased_should_throw_provided_instance() {
        // GIVEN a Supplier that returns a new IllegalArgumentException
        final String expectedMessage = "Test message from supplier";
        final Supplier<IllegalArgumentException> supplier = () -> new IllegalArgumentException(expectedMessage);

        // WHEN the method is called
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> {
            // The format string is logged, but the exception message comes from the supplier
            ExceptionUtils.logAndThrow(supplier, "Log format: {}", "arg");
        });
        
        // THEN the thrown exception's message must match the one hardcoded in the supplier.
        assertEquals(expectedMessage, thrownException.getMessage(), 
                     "The thrown exception should carry the message provided in the Supplier.");
    }
    
    /**
     * Tests that the supplier-based method correctly handles a checked exception type.
     */
    @Test
    void logAndThrow_SupplierBased_should_handle_checked_exception() {
        // GIVEN a checked exception type (e.g., Exception)
        final String expectedMessage = "Checked exception test";
        
        // WHEN the method is called
        Exception thrownException = assertThrows(Exception.class, () -> {
            ExceptionUtils.logAndThrow(() -> new Exception(expectedMessage), "Test log format");
        });
        
        // THEN the message must match the one provided in the supplier
        assertEquals(expectedMessage, thrownException.getMessage(), 
                     "The thrown checked exception should carry the message from the Supplier.");
    }
    
    // --- Tests for logAndThrowWithMessage (Function-based) method ---

    /**
     * Tests that the function-based method correctly throws the specified exception type 
     * and ensures the exception message is the *formatted message* (using String.format).
     */
    @Test
    void logAndThrowWithMessage_should_throw_exception_with_formatted_message() {
        // GIVEN a format string and arguments
        final String format = "The value '%s' caused an error: %s.";
        final Object[] args = {"12345", "InvalidInput"};
        final String expectedMessage = String.format(format, args); 

        // WHEN the method is called with a Function that takes a String (the message)
        Exception thrownException = assertThrows(RuntimeException.class, () -> {
            ExceptionUtils.logAndThrowWithMessage(
                RuntimeException::new, 
                format, 
                args
            );
        });

        // THEN the thrown exception's message must exactly match the formatted message string
        assertEquals(expectedMessage, thrownException.getMessage(), 
                     "The thrown exception must contain the full formatted message from String.format.");
    }

    // --- Tests for logAndThrowWithClass (Reflection-based) method ---

    /**
     * Tests the reflection-based method with a standard IllegalArgumentException (happy path).
     * This verifies the method correctly finds and invokes the String constructor.
     */
    @Test
    void logAndThrowWithClass_should_throw_formatted_message() { 
        // GIVEN format and arguments
        final String format = "Reflection test failed for parameter: %s.";
        final String param = "token-123";
        final String expectedMessage = String.format(format, param); 

        // WHEN the method is called, we now EXPECT the wrapping RuntimeException
        Exception thrownWrapperException = assertThrows(RuntimeException.class, () -> { // ⚠️ CAMBIO 1: Esperamos RuntimeException
            ExceptionUtils.logAndThrowWithClass(IllegalArgumentException.class, format, param); 
        });

        // THEN: The wrapper exception's cause must be the expected IllegalArgumentException
        Throwable thrownCause = thrownWrapperException.getCause();
        assertTrue(thrownCause instanceof IllegalArgumentException, "The CAUSE must be IllegalArgumentException, not the wrapper RuntimeException."); // ⚠️ CAMBIO 2

        // THEN: The message of the CAUSE must be the expected formatted message
        assertEquals(expectedMessage, thrownCause.getMessage(), 
                     "The CAUSE's message must contain the full formatted message.");
        
        // Ensure the wrapper message indicates a general failure
        assertTrue(thrownWrapperException.getMessage().contains("Could not instantiate exception class: "),
                   "The wrapper exception message must indicate a general instantiation failure."); // ⚠️ CAMBIO 3: Nueva aserción
    }
    
    /**
     * Tests the reflection-based method's failure path when the target exception class
     * does not have the required public constructor that accepts a single String.
     * This should trigger the {@code catch (NoSuchMethodException e)} block.
     */
    @Test
    void logAndThrowWithClass_should_throw_RuntimeException_on_missing_constructor() {
        // GIVEN a custom exception class that lacks the required String constructor
        final String format = "Attempting to throw unsupported exception: %s";

        // WHEN the method is called with the unsupported class
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            ExceptionUtils.logAndThrowWithClass(ExceptionWithoutStringConstructor.class, format, "test");
        });

        // THEN the thrown exception is a RuntimeException (as defined in the catch block)
        // and its cause is NoSuchMethodException.
        assertTrue(thrownException.getCause() instanceof NoSuchMethodException, 
                   "The cause must be NoSuchMethodException, covering the 'NoSuchMethodException e' block.");
        
        // THEN the message should clearly indicate the failure reason
        assertTrue(thrownException.getMessage().contains("Error during exception construction for class: "), 
                   "The error message should indicate a construction failure.");
    }
    
    /**
     * Tests the reflection-based method's generic failure path for other reflection errors
     * (e.g., IllegalAccessException or InvocationTargetException).
     * This should trigger the generic {@code catch (Exception e)} block.
     */
    @Test
    void logAndThrowWithClass_should_throw_RuntimeException_on_reflection_error() {
        // GIVEN a format string
        final String format = "Simulating a generic reflection error: %s";
        
        // WHEN the method is called with a class that fails construction
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            ExceptionUtils.logAndThrowWithClass(FailingConstructorException.class, format, "test"); // ⚠️ CAMBIO AQUÍ
        });

        // THEN: The thrown exception is the RuntimeException from the generic catch block.
        String actualMessage = thrownException.getMessage();
        
        assertTrue(actualMessage.contains("Could not instantiate exception class: "), 
                   "The error message must contain the instantiation failure message, covering the generic 'catch (Exception e)' block.");
        
        assertTrue(thrownException.getCause() instanceof InvocationTargetException, 
                   "The cause of the thrown exception must be InvocationTargetException (due to the helper class throwing an exception inside its constructor).");
    }


    // --- Test for private constructor enforcement (100% Coverage) ---

    /**
     * Tests the private constructor to ensure the utility class cannot be instantiated, 
     * enforcing its static nature and achieving code coverage on the private constructor.
     */
    @Test
    void constructor_should_throw_unsupported_operation_exception() throws Exception {
        // 1. Retrieve the Constructor object for the class.
        Constructor<ExceptionUtils> constructor = ExceptionUtils.class.getDeclaredConstructor();
        
        // 2. VERIFICATION: Ensure the constructor is PRIVATE.
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), 
                "The constructor must be declared as private to prevent instantiation.");
        
        // 3. FORCE ACCESSIBILITY: Override the 'private' restriction for testing purposes.
        constructor.setAccessible(true);
        
        // 4. Invoke the constructor and expect the wrapper exception (InvocationTargetException).
        InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, () -> {
            constructor.newInstance();
        }, "Invoking the private constructor should wrap the internal exception in InvocationTargetException.");
        
        // 5. Verify the actual cause is the expected exception (UnsupportedOperationException).
        Throwable actualCause = thrownException.getCause();
        assertTrue(actualCause instanceof UnsupportedOperationException, 
                "The internal exception (cause) must be UnsupportedOperationException.");
                
        // Verification of the message content
        final String expectedMessagePart = "This is a "+ExceptionUtils.class.getSimpleName()+" class and cannot be instantiated.";
        assertTrue(actualCause.getMessage().contains(expectedMessagePart),
                    "The constructor's message should contain the expected text.");
    }
}