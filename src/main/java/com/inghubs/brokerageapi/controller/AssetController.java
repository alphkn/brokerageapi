package com.inghubs.brokerageapi.controller;

import java.util.List;

import com.inghubs.brokerageapi.entity.Asset;
import com.inghubs.brokerageapi.service.AssetService;
import com.inghubs.brokerageapi.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for handling asset-related API endpoints.
 */
@RestController
@RequestMapping("/api/assets")
@Tag(name = "Asset API", description = "Asset API endpoints")
public class AssetController {
    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);

    private final AssetService assetService;
    private final AuthenticationService authenticationService;

    /**
     * Constructor for AssetController that initializes AssetService and AuthenticationService.
     *
     * @param assetService          the service responsible for asset management.
     * @param authenticationService  the service for handling authentication-related logic.
     */
    @Autowired
    public AssetController(AssetService assetService, AuthenticationService authenticationService) {
        this.assetService = assetService;
        this.authenticationService = authenticationService;
        logger.info("AssetController initialized with AssetService and AuthenticationService.");
    }

    /**
     * Endpoint for listing assets associated with a specific customer.
     *
     * @param userDetails the authenticated user's details.
     * @param customerId  the ID of the customer whose assets are to be listed.
     * @return a ResponseEntity containing the list of assets for the specified customer.
     */
    @Operation(summary = "Listing Assets endpoint")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')") // Only allow ADMIN and CUSTOMER roles to access this endpoint
    public ResponseEntity<List<Asset>> listAssets(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long customerId) {
        // Check if the authenticated user has access to the specified customer
        authenticationService.checkCustomerAccess(userDetails, customerId);
        logger.debug("Listing assets for customer ID: {}", customerId);
        List<Asset> assets = assetService.listAssets(customerId);
        logger.info("Assets listed successfully for customer ID: {}", customerId);
        return ResponseEntity.ok(assets);
    }
}