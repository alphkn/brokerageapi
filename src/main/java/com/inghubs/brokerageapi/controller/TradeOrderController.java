package com.inghubs.brokerageapi.controller;

import java.time.LocalDateTime;
import java.util.List;

import com.inghubs.brokerageapi.dto.CreateOrderRequest;
import com.inghubs.brokerageapi.entity.TradeOrder;
import com.inghubs.brokerageapi.service.AuthenticationService;
import com.inghubs.brokerageapi.service.TradeOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for handling trade order-related API endpoints.
 */
@RestController
@RequestMapping("/api/tradeOrders")
@Tag(name = "Trade Orders API", description = "Trade Orders API endpoints")
public class TradeOrderController {
    private static final Logger log = LoggerFactory.getLogger(TradeOrderController.class);

    private final TradeOrderService tradeOrderService;
    private final AuthenticationService authenticationService;

    /**
     * Constructor for TradeOrderController that initializes TradeOrderService and AuthenticationService.
     *
     * @param tradeOrderService       the service responsible for trade order management.
     * @param authenticationService    the service for handling authentication-related logic.
     */
    public TradeOrderController(TradeOrderService tradeOrderService, AuthenticationService authenticationService) {
        this.tradeOrderService = tradeOrderService;
        this.authenticationService = authenticationService;
        log.info("TradeOrderController initialized with TradeOrderService and AuthenticationService.");
    }

    /**
     * Endpoint for creating a new trade order.
     *
     * @param userDetails the authenticated user's details.
     * @param request     the request containing trade order details.
     * @return a ResponseEntity containing the created trade order.
     */
    @Operation(summary = "Create new Trade Order endpoint")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')") // Only allow ADMIN and CUSTOMER roles to access this endpoint
    public ResponseEntity<TradeOrder> createTradeOrder(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CreateOrderRequest request) {
        // Check if the authenticated user has access to the specified customer
        authenticationService.checkCustomerAccess(userDetails, request.getCustomerId());
        TradeOrder order = tradeOrderService.createTradeOrder(request.getCustomerId(), request.getAssetCode(), request.getSide(), request.getSize(), request.getPrice());

        log.info("Trade order created successfully: {}", order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order); // Respond with HTTP 201 Created
    }

    /**
     * Endpoint for listing trade orders for a specific customer.
     *
     * @param userDetails the authenticated user's details.
     * @param customerId  the ID of the customer whose trade orders are to be listed.
     * @param startDate   the start date for filtering trade orders.
     * @param endDate     the end date for filtering trade orders.
     * @param page        the page number for pagination.
     * @param size        the size of the page for pagination.
     * @return a ResponseEntity containing the list of trade orders.
     */
    @Operation(summary = "Listing Trade Orders endpoint")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')") // Only allow ADMIN and CUSTOMER roles to access this endpoint
    public ResponseEntity<List<TradeOrder>> listTradeOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Check if the authenticated user has access to the specified customer
        authenticationService.checkCustomerAccess(userDetails, customerId);
        List<TradeOrder> tradeOrders = tradeOrderService.listTradeOrders(customerId, startDate, endDate, page, size);
        log.info("Listed {} trade orders for customer ID: {}", tradeOrders.size(), customerId);
        return ResponseEntity.ok(tradeOrders); // Respond with HTTP 200 OK
    }

    /**
     * Endpoint for deleting (cancelling) a specific trade order.
     *
     * @param userDetails    the authenticated user's details.
     * @param tradeOrderId   the ID of the trade order to cancel.
     * @return a ResponseEntity indicating the completion of the operation.
     */
    @Operation(summary = "Delete Trade Order endpoint")
    @DeleteMapping("/{tradeOrderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')") // Only allow ADMIN and CUSTOMER roles to access this endpoint
    public ResponseEntity<Void> cancelOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long tradeOrderId) {
        // Check if the authenticated user has access to the customer of the specified trade order
        authenticationService.checkCustomerAccess(userDetails, tradeOrderService.getCustomerId(tradeOrderId));
        tradeOrderService.cancelTradeOrder(tradeOrderId);

        log.info("Trade order with ID: {} cancelled successfully.", tradeOrderId);
        return ResponseEntity.ok().build(); // Respond with HTTP 200 OK
    }
}
