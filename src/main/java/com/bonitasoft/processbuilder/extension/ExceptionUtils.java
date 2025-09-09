package com.bonitasoft.processbuilder.extension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for handling logging and exception throwing.
 * This class centralizes error management to ensure consistency.
 */
public class ExceptionUtils {

    /**
     * A logger for this class, used to record log messages and provide debugging information.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionUtils.class);

    /**
     * Logs an error message and throws a generic or specific exception.
     * This method uses reflection to create a new instance of the provided exception class.
     *
     * @param <T> The type of exception to throw. It must extend from Exception and have a constructor that accepts a single String argument.
     * @param exceptionClass The Class object of the exception to be thrown.
     * @param errorMessage The detailed message for the error.
     * @throws T The new instance of the specified exception class.
     * @throws RuntimeException if the specified exception cannot be instantiated or if a reflection error occurs.
     */
    public static <T extends Exception> void logAndThrowError(Class<T> exceptionClass, String errorMessage) throws T {
        LOGGER.error(errorMessage);
        try {
            // Retrieve the constructor that takes a single String argument.
            Constructor<T> constructor = exceptionClass.getConstructor(String.class);
            // Throw a new instance of the specified exception.
            throw constructor.newInstance(errorMessage);
        } catch (InvocationTargetException e) {
            // InvocationTargetException wraps the real exception thrown by the constructor.
            // We unwrap it and re-throw the original exception.
            Throwable cause = e.getTargetException();
            if (cause instanceof Exception) {
                // We must cast to T to satisfy the method's 'throws T' declaration.
                throw (T) cause;
            } else {
                // If the cause is a non-Exception Throwable, we re-throw it wrapped in a RuntimeException.
                throw new RuntimeException("Failed to create and throw the specified exception.", e);
            }
        } catch (ReflectiveOperationException e) {
            // This catches other reflection errors like NoSuchMethodException or InstantiationException.
            throw new RuntimeException("Failed to instantiate the specified exception class.", e);
        }
    }

    /**
     * Logs an error message and then throws a new instance of a specified exception.
     * This method is a safe and generic way to handle exceptions by using a Supplier,
     * which avoids the complexities and potential runtime errors of Reflection.
     *
     * @param <T> The type of the exception to be thrown. This must be a subclass of Exception.
     * @param exceptionSupplier A {@link java.util.function.Supplier} that provides an instance of the exception to be thrown.
     * The supplier's {@code get()} method should create and return a new exception instance,
     * typically via a lambda or a constructor reference.
     * @param errorMessage A log message describing the error. This message will be recorded by the logger
     * and used to initialize the thrown exception.
     * @throws T The exception instance provided by the {@code exceptionSupplier}.
     */
    public static <T extends Exception> void logAndThrow(Supplier<T> exceptionSupplier, String errorMessage) throws T {
        LOGGER.error(errorMessage);
        throw exceptionSupplier.get();
    }
}