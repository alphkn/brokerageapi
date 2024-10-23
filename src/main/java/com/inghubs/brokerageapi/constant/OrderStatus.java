package com.inghubs.brokerageapi.constant;

/**
 * Enumeration representing the various statuses of an order in the trading system.
 */
public enum OrderStatus {
    PENDING,         // Order has been created but not yet executed
    FILLED,          // Order has been fully executed
    CANCELED,        // Order has been canceled and will not be executed
    PARTIALLY_FILLED; // Order has been partially executed
}