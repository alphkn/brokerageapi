package com.inghubs.brokerageapi.exception;

import com.inghubs.brokerageapi.constant.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles InsufficientBalanceException.
     *
     * @param ex the exception instance
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
        log.error(ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),
                                                      CommonConstants.INSUFFICIENT_BALANCE, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles OrderNotFoundException.
     *
     * @param ex the exception instance
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        ApiErrorResponse error = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(),
                                                      CommonConstants.ORDER_NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles CustomerNotFoundException.
     *
     * @param ex the exception instance
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex) {
        ApiErrorResponse error = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(),
                                                      CommonConstants.CUSTOMER_NOT_FOUND_OR_NOT_ENABLED, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Generic exception handler for any other exceptions.
     *
     * @param ex the exception instance
     * @return ResponseEntity containing the error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage());
        ApiErrorResponse error = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                                      "Internal Server Error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
