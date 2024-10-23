package com.inghubs.brokerageapi.service;

import com.inghubs.brokerageapi.constant.CommonConstants;
import com.inghubs.brokerageapi.entity.Customer;
import com.inghubs.brokerageapi.exception.CustomerNotFoundException;
import com.inghubs.brokerageapi.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Service for managing customer-related operations.
 */
@Service
@Transactional
@Validated
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Retrieves an enabled customer by their ID.
     *
     * @param customerId the ID of the customer to retrieve
     * @return the found Customer entity
     * @throws CustomerNotFoundException if the customer is not found or not enabled
     */
    public Customer getEnabledCustomerById(Long customerId) {
        log.info("Fetching customer with ID: {}", customerId);

        return customerRepository.findById(customerId)
                .filter(Customer::isEnabled)
                .orElseThrow(() -> {
                    log.error("Customer not found or not enabled for ID: {}", customerId);
                    return new CustomerNotFoundException(CommonConstants.CUSTOMER_NOT_FOUND_OR_NOT_ENABLED);
                });
    }
}
