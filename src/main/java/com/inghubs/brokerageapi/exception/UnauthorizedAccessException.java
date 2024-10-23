package com.inghubs.brokerageapi.exception;

import com.inghubs.brokerageapi.constant.CommonConstants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RuntimeException {

    // Constructor with a message
    public UnauthorizedAccessException(String message) {
        super(message);
    }

    // Default constructor
    public UnauthorizedAccessException() {
        super(CommonConstants.UNAUTHORIZED_ACCESS);
    }

    // Constructor with a message and cause
    public UnauthorizedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}