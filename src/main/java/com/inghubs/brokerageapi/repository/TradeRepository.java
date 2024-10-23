package com.inghubs.brokerageapi.repository;

import com.inghubs.brokerageapi.entity.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for managing Trade entities.
 * Extends JpaRepository to provide CRUD operations for Trade entities.
 */
@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    // Additional custom query methods can be defined here if needed in the future.
}