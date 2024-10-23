package com.inghubs.brokerageapi.exception;

/**
 * Represents an error response returned by the API.
 */
public class ApiErrorResponse {

    // HTTP status code of the error
    private final int statusCode;

    // Short error description
    private final String error;

    // Detailed error message
    private final String message;

    /**
     * Constructs an ApiErrorResponse with the specified status code, error type, and message.
     *
     * @param statusCode the HTTP status code
     * @param error a short description of the error
     * @param message a detailed error message
     */
    public ApiErrorResponse(int statusCode, String error, String message) {
        this.statusCode = statusCode;
        this.error = error;
        this.message = message;
    }

    // Getters for the fields can be added if needed
    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
