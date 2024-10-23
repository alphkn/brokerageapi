package com.inghubs.brokerageapi.repository;

import java.util.List;

import com.inghubs.brokerageapi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for managing Transaction entities.
 * Extends JpaRepository to provide CRUD operations for Transaction entities.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Retrieves a list of transactions associated with a specific customer ID.
     *
     * @param customerId the ID of the customer whose transactions are to be retrieved
     * @return a list of transactions associated with the given customer ID
     */
    List<Transaction> findByCustomerId(Long customerId);
}