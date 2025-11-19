package com.bonitasoft.processbuilder.extension;

/**
 * Exception thrown when validation of input parameters fails.
 * This exception is typically used in REST API extensions to signal validation errors.
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message explaining the validation failure
     */
    public ValidationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     *
     * @param message the detail message explaining the validation failure
     * @param cause the cause of the validation failure
     */
    public ValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
