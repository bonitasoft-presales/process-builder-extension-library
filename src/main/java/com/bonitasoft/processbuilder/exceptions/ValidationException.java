package com.bonitasoft.processbuilder.exceptions;

/**
 * Custom exception for handling validation failures.
 * This exception is thrown when input parameters (e.g., REST API inputs)
 * fail to meet structural, format, or business validation rules.
 * * It extends RuntimeException so it does not need to be declared in method signatures.
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ValidationException with the specified detail message.
     * * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     * * @param message the detail message.
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}