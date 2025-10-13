package com.bonitasoft.processbuilder.extension;

import java.util.function.Function;
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
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private ExceptionUtils() {
        throw new UnsupportedOperationException("This is a "+this.getClass().getSimpleName()+" class and cannot be instantiated.");
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
    public static <T extends Exception> T logAndThrow(
        Supplier<T> exceptionSupplier,
        String format,
        Object... args) throws T {
        
        // The logger is designed to handle message formatting lazily, which is more performant.
        // The message is only built if the ERROR log level is enabled.
        LOGGER.error(format, args);
        
        // Throw the exception provided by the supplier. This ensures we're throwing a new,
        // explicitly defined exception instance.
        // throw exceptionSupplier.get();
        throw exceptionSupplier.get();
    }

    /**
     * Utility method to safely log an error message and subsequently throw a new
     * exception instance containing that same formatted message.
     *
     * <p>This ensures that the exception thrown, even if generic (like RuntimeException),
     * always contains the context provided in the 'format' string, which is crucial
     * for troubleshooting in environments that might discard the original cause's details.</p>
     *
     * @param <T> The type of the Exception to be thrown, which must extend Exception.
     * @param exceptionFunction A function that accepts the final formatted message
     * (String) and returns a new exception of type T (e.g., {@code message -> new MyCustomException(message)}).
     * @param format The format string for the error message (compatible with String.format).
     * @param args Arguments referenced by the format specifiers in the format string.
     * @throws T The exception created by the provided function.
     */
    public static <T extends Exception> void logAndThrowWithMessage(
            Function<String, T> exceptionFunction, 
            String format,
            Object... args) throws T {
            
        // 1. EXPLICIT FORMATTING: Construct the complete, formatted error message string.
        // This is done once to ensure consistency between the log and the exception.
        String finalMessage = String.format(format, args);
        
        // 2. LOGGING: Log the error message using the fully formatted string.
        // The LOGGER variable must be defined elsewhere (e.g., LOGGER = LoggerFactory.getLogger(YourClass.class)).
        // Note: Use LOGGER.error, assuming the level is appropriately configured.
        LOGGER.error(finalMessage);
        
        // 3. SECURE THROW: Use the function to create and throw the new exception instance,
        // explicitly passing the 'finalMessage'. This guarantees the exception (T) 
        // always contains the detailed error message, avoiding "No message" errors.
        throw exceptionFunction.apply(finalMessage);
    }
}