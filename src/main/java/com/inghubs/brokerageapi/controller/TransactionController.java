package com.inghubs.brokerageapi.controller;

import java.util.List;

import com.inghubs.brokerageapi.entity.Transaction;
import com.inghubs.brokerageapi.service.AuthenticationService;
import com.inghubs.brokerageapi.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for handling transaction-related API endpoints.
 */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction API", description = "Transaction API endpoints")
public class TransactionController {
    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;
    private final AuthenticationService authenticationService;

    /**
     * Constructor for TransactionController that initializes TransactionService and AuthenticationService.
     *
     * @param transactionService       the service responsible for transaction management.
     * @param authenticationService     the service for handling authentication-related logic.
     */
    public TransactionController(TransactionService transactionService, AuthenticationService authenticationService) {
        this.transactionService = transactionService;
        this.authenticationService = authenticationService;
        log.info("TransactionController initialized with TransactionService and AuthenticationService.");
    }

    /**
     * Endpoint for listing transactions for a specific customer.
     *
     * @param userDetails the authenticated user's details.
     * @param customerId  the ID of the customer whose transactions are to be listed.
     * @return a ResponseEntity containing the list of transactions.
     */
    @Operation(summary = "Listing Transactions endpoint")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')") // Only allow ADMIN and CUSTOMER roles to access this endpoint
    public ResponseEntity<List<Transaction>> listTransactions(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long customerId) {
        // Check if the authenticated user has access to the specified customer
        authenticationService.checkCustomerAccess(userDetails, customerId);
        List<Transaction> transactions = transactionService.listTransactions(customerId);

        log.info("Listed {} transactions for customer ID: {}", transactions.size(), customerId);
        return ResponseEntity.ok(transactions); // Respond with HTTP 200 OK
    }

    /**
     * Endpoint for depositing or withdrawing money.
     *
     * @param userDetails    the authenticated user's details.
     * @param transaction    the transaction details including amount and type.
     * @return a ResponseEntity containing the saved transaction.
     */
    @Operation(summary = "Deposit or Withdraw Money endpoint")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')") // Only allow ADMIN and CUSTOMER roles to access this endpoint
    public ResponseEntity<Transaction> depositMoney(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody Transaction transaction) {
        // Check if the authenticated user has access to the customer associated with the transaction
        authenticationService.checkCustomerAccess(userDetails, transaction.getCustomer().getId());

        Transaction savedTransaction = transactionService.checkAndSaveTransaction(transaction);
        log.info("Transaction processed successfully: {}", savedTransaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction); // Respond with HTTP 201 Created
    }
}