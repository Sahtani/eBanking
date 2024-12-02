package com.youcode.ebanking.exception;

/**
 * Exception thrown when a user with the same email already exists.
 */
public class UserAlreadyExistsByEmailException extends RuntimeException {

    /**
     * Default constructor with a generic error message.
     */
    public UserAlreadyExistsByEmailException() {
        super("A user with this email already exists.");
    }

    /**
     * Constructor with a custom error message.
     *
     * @param message The error message.
     */
    public UserAlreadyExistsByEmailException(String message) {
        super(message);
    }

    /**
     * Constructor with a custom error message and a cause.
     *
     * @param message The error message.
     * @param cause   The underlying cause of the exception.
     */
    public UserAlreadyExistsByEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
