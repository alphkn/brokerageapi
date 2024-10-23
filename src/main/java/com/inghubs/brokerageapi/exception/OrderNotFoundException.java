package com.inghubs.brokerageapi.exception;

public class OrderNotFoundException extends RuntimeException {
    // Constructor with a message
    public OrderNotFoundException(String message) {
        super(message);
    }

    // Default constructor
    public OrderNotFoundException() {
        super("Order not found");
    }

    // Constructor with a message and cause
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}