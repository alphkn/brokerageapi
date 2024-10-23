package com.inghubs.brokerageapi.repository;

import com.inghubs.brokerageapi.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository interface for managing Customer entities.
 * Extends JpaRepository to provide CRUD operations.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // No additional methods are defined, but the JpaRepository provides
    // standard CRUD operations for Customer entities.
}