package com.inghubs.brokerageapi.exception;

/**
 * Exception thrown when a customer is not found in the system.
 */
public class CustomerNotFoundException extends RuntimeException {

    /**
     * Constructs a new CustomerNotFoundException with the specified detail message.
     *
     * @param message the detail message, saved for later retrieval by the {@link Throwable#getMessage()} method.
     */
    public CustomerNotFoundException(String message) {
        super(message);
    }

}
