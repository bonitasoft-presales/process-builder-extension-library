package com.bonitasoft.processbuilder.extension;

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
     * Private constructor to prevent instantiation of this utility class.
     * All methods in this class are static and should be called directly on the class itself.
     */
    private ExceptionUtils() {
        // This constructor is intentionally empty.
    }

    /**
     * Logs a formatted error message and then throws a new instance of a specified exception.
     * This method is a safe and generic way to handle exceptions by using a {@link java.util.function.Supplier},
     * which avoids the complexities and potential runtime errors of Reflection. It leverages the logging
     * framework's ability to format messages, which is more efficient because the message is only
     * constructed if the log level is active.
     *
     * @param <T> The type of the exception to be thrown. This must be a subclass of {@link java.lang.Exception}.
     * @param exceptionSupplier A {@link java.util.function.Supplier} that provides an instance of the exception to be thrown.
     * The supplier's {@code get()} method should create and return a new exception instance, typically
     * via a lambda or a constructor reference (e.g., {@code IllegalArgumentException::new}).
     * @param format A parameterized message format string compatible with the logging framework's
     * formatters (e.g., using `{}`). This format string will be logged to the error level.
     * @param args A variable-length argument list of objects to be substituted into the format string.
     * These objects correspond to the `{}` placeholders in the {@code format} string.
     * @throws T The exception instance provided by the {@code exceptionSupplier}.
     */
    public static <T extends Exception> void logAndThrow(
        Supplier<T> exceptionSupplier,
        String format,
        Object... args) throws T {
        
        // The logger is designed to handle message formatting lazily, which is more performant.
        // The message is only built if the ERROR log level is enabled.
        LOGGER.error(format, args);
        
        // Throw the exception provided by the supplier. This ensures we're throwing a new,
        // explicitly defined exception instance.
        throw exceptionSupplier.get();
    }
}