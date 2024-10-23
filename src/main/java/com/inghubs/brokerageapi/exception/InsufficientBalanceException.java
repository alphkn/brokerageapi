package com.inghubs.brokerageapi.exception;

public class InsufficientBalanceException extends RuntimeException {
    // Constructor with a message
    public InsufficientBalanceException(String message) {
        super(message);
    }

    // Default constructor
    public InsufficientBalanceException() {
        super("Insufficient balance");
    }

    // Constructor with a message and cause
    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}