package com.inghubs.brokerageapi.controller;

import com.inghubs.brokerageapi.constant.AssetCodes;
import com.inghubs.brokerageapi.service.TradeMatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for handling trade matching-related API endpoints.
 */
@RestController
@RequestMapping("/api/tradeMatcher")
@Tag(name = "Trade Matcher API", description = "Trade Matcher API endpoints")
public class TradeMatchingController {
    private static final Logger log = LoggerFactory.getLogger(TradeMatchingController.class);

    private final TradeMatchingService tradeMatchingService;

    /**
     * Constructor for TradeMatchingController that initializes TradeMatchingService.
     *
     * @param tradeMatchingService the service responsible for matching trade orders.
     */
    public TradeMatchingController(TradeMatchingService tradeMatchingService) {
        this.tradeMatchingService = tradeMatchingService;
        log.info("TradeMatchingController initialized with TradeMatchingService.");
    }

    /**
     * Endpoint for matching all pending trade orders.
     * This endpoint is restricted to users with the ADMIN role.
     *
     * @return a ResponseEntity indicating the completion of the operation.
     */
    @Operation(summary = "Match All Pending Trade Orders endpoint")
    @PostMapping("/match")
    @PreAuthorize("hasRole('ADMIN')") // Only allow ADMIN role to access this endpoint
    public ResponseEntity<Void> matchAllPendingTradeOrder() {
        log.info("Matching all pending trade orders...");

        // Match orders for different asset codes
        tradeMatchingService.matchOrders(AssetCodes.SASA);
        tradeMatchingService.matchOrders(AssetCodes.GARAN);
        tradeMatchingService.matchOrders(AssetCodes.ING);

        log.info("All pending trade orders matched successfully.");
        return ResponseEntity.ok().build(); // Respond with HTTP 200 OK
    }
}