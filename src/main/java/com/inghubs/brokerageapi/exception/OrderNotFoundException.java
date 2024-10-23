package com.inghubs.brokerageapi.exception;

import com.inghubs.brokerageapi.constant.CommonConstants;


public class OrderNotFoundException extends RuntimeException {

    // Constructor with a message
    public OrderNotFoundException(String message) {
        super(message);
    }

    // Default constructor
    public OrderNotFoundException() {
        super(CommonConstants.ORDER_NOT_FOUND);
    }

    // Constructor with a message and cause
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}