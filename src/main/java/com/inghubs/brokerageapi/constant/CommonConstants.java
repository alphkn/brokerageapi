package com.inghubs.brokerageapi.constant;

/**
 * Class containing common constants used throughout the application.
 */
public class CommonConstants {
    // General error messages and keys
    public static final String ASSET_NOT_FOUND = "Asset not found";
    public static final String INSUFFICIENT_ASSET_BALANCE = "Insufficient asset balance";
    public static final String CUSTOMER_ID = "customer_id"; // Key for customer ID
    public static final String USER_ROLES = "user_roles"; // Key for user roles
    public static final String USER_ID = "user_id"; // Key for user ID
    public static final String ROLE = "role"; // Key for role
    public static final String INSUFFICIENT_BALANCE = "Insufficient Balance"; // General balance error
    public static final String ORDER_NOT_FOUND = "Order Not Found"; // Error for order not found
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists"; // Error for duplicate username
    public static final String PASSWORD_MUST_BE_AT_LEAST_8_CHARACTERS_LONG = "Password must be at least 8 characters long"; // Password validation message
    public static final String INVALID_ROLE_PROVIDED = "Invalid role provided"; // Error for invalid role
    public static final String USER_NOT_FOUND = "User not found"; // Error for user not found
    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password"; // Error for authentication failure
    public static final String USER_NOT_FOUND_WITH_USERNAME = "User not found with username: "; // Message for user lookup failure
    public static final String ORDER_NOT_FOUND_OR_NOT_IN_PENDING_STATUS = "Order not found or not in PENDING status"; // Order status error
    public static final String CUSTOMER_NOT_FOUND_OR_NOT_ENABLED = "Customer not found or not ENABLED"; // Customer status error
    public static final String PRICE = "price"; // Key for price
    public static final String CREATE_DATE = "createDate"; // Key for creation date
}