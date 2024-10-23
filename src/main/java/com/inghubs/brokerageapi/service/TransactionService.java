package com.inghubs.brokerageapi.service;

import java.util.List;

import com.inghubs.brokerageapi.entity.Transaction;
import com.inghubs.brokerageapi.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@Validated
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final AssetService assetService;
    private final CustomerService customerService;

    public TransactionService(TransactionRepository transactionRepository, AssetService assetService, CustomerService customerService) {
        this.transactionRepository = transactionRepository;
        this.assetService = assetService;
        this.customerService = customerService;
    }

    /**
     * Retrieves a list of transactions for the specified customer.
     *
     * @param customerId the ID of the customer whose transactions are to be listed
     * @return a list of transactions for the customer
     */
    public List<Transaction> listTransactions(Long customerId) {
        log.info("Listing transactions for customer ID: {}", customerId);
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);
        log.info("Found {} transactions for customer ID: {}", transactions.size(), customerId);
        return transactions;
    }

    /**
     * Checks and saves the transaction after processing.
     *
     * @param transaction the transaction to be checked and saved
     * @return the saved transaction
     */
    public Transaction checkAndSaveTransaction(@Valid Transaction transaction) {
        log.info("Checking transaction for customer ID: {}", transaction.getCustomer().getId());

        // Fetch and set the enabled customer
        transaction.setCustomer(customerService.getEnabledCustomerById(transaction.getCustomer().getId()));

        // Save the transaction
        log.info("Saving transaction: {}", transaction);
        transactionRepository.save(transaction);

        try {
            // Process the transaction
            assetService.processTransaction(transaction);
            transaction.setProcessed(true);
            log.info("Transaction processed successfully: {}", transaction);
        } catch (Exception e) {
            log.error("Error processing transaction: {}. Error: {}", transaction, e.getMessage());
        }

        // Save the processed transaction
        transactionRepository.save(transaction);
        log.info("Transaction saved: {}", transaction);
        return transaction;
    }
}
