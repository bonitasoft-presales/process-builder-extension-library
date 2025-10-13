package com.bonitasoft.processbuilder.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Constants} utility class.
 * <p>
 * This class ensures that the utility class cannot be instantiated and
 * that all constants hold their expected values.
 * </p>
 */
class ConstantsTest {

    /**
     * Test case to ensure the private constructor throws an
     * {@link UnsupportedOperationException} when accessed via reflection,
     * confirming that the utility class cannot be instantiated.
     */
    @Test
    @DisplayName("Should throw UnsupportedOperationException when instantiated")
    void shouldThrowExceptionOnInstantiation() throws NoSuchMethodException {
        // 1. Get the private constructor using reflection
        Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        // 2. Make the constructor accessible (it's private)
        constructor.setAccessible(true);

        // 3. Assert that calling the constructor throws InvocationTargetException,
        // which wraps the actual UnsupportedOperationException
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);

        // 4. Check the cause of the exception
        assertEquals(UnsupportedOperationException.class, thrown.getCause().getClass(),
                "The exception cause should be UnsupportedOperationException.");
    }

    /**
     * Test case to verify that the {@code TEST} constant holds the correct value.
     */
    @Test
    @DisplayName("Should verify the value of the TEST constant")
    void shouldVerifyTestConstantValue() {
        assertEquals("Test", Constants.TEST, "The TEST constant should hold the value 'Test'.");
    }

    /**
     * Test case to verify that the {@code EMPTY} constant holds the correct value.
     */
    @Test
    @DisplayName("Should verify the value of the EMPTY constant")
    void shouldVerifyEmptyConstantValue() {
        assertEquals("", Constants.EMPTY, "The EMPTY constant should hold an empty string.");
    }
}