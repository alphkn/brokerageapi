package com.inghubs.brokerageapi.exception;

import com.inghubs.brokerageapi.constant.CommonConstants;


public class InsufficientBalanceException extends RuntimeException {

    // Constructor with a message
    public InsufficientBalanceException(String message) {
        super(message);
    }

    // Default constructor
    public InsufficientBalanceException() {
        super(CommonConstants.INSUFFICIENT_BALANCE);
    }

    // Constructor with a message and cause
    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}