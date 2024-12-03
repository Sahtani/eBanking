package com.youcode.ebanking.exception;

/**
 * Exception thrown when a user with the same email already exists.
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    /**
     * Default constructor with a generic error message.
     */
    public UsernameAlreadyExistsException() {
        super("A user with this email already exists.");
    }

    /**
     * Constructor with a custom error message.
     *
     * @param message The error message.
     */
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructor with a custom error message and a cause.
     *
     * @param message The error message.
     * @param cause   The underlying cause of the exception.
     */
    public UsernameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
