package com.inghubs.brokerageapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.inghubs.brokerageapi.constant.CommonConstants;
import com.inghubs.brokerageapi.constant.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


/**
 * Represents a financial transaction in the brokerage system.
 */
@Getter // Generates getters for all fields
@Setter // Generates setters for all fields
@Entity // Indicates that this class is a JPA entity
public class Transaction {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicates that the ID should be generated automatically
    private Long id; // Unique identifier for the transaction

    @ManyToOne // Defines a many-to-one relationship with the Customer entity
    @JoinColumn(name = CommonConstants.CUSTOMER_ID) // Specifies the foreign key column name for the customer
    private Customer customer; // The customer associated with this transaction

    @Enumerated(EnumType.STRING) // Specifies that this field should be stored as a string in the database
    @Column(nullable = false) // Indicates that this field cannot be null
    @NotNull(message = "Transaction type cannot be null") // Validation constraint
    private TransactionType type; // The type of transaction (e.g., DEPOSIT or WITHDRAWAL)

    @Column(nullable = false) // Indicates that this field cannot be null
    @NotNull(message = "Amount cannot be null") // Validation constraint
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0") // Validation constraint
    private BigDecimal amount; // The amount of money involved in the transaction

    @Size(min = 15, max = 34, message = "IBAN must be between 15 and 34 characters") // Validation constraint for IBAN length
    private String iban; // The IBAN associated with the transaction

    @Column(nullable = false) // Indicates that this field cannot be null
    @NotNull(message = "Timestamp cannot be null") // Validation constraint
    private LocalDateTime timestamp = LocalDateTime.now(); // The date and time when the transaction is created

    @Column(nullable = false) // Indicates that this field cannot be null
    private boolean processed = false; // Flag indicating whether the transaction has been processed

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", customerId=" + (customer != null ? customer.getId() : "null") + // Displays the customer ID
                ", type=" + type +
                ", amount=" + amount +
                ", iban='" + iban + '\'' +
                ", timestamp=" + timestamp +
                ", processed=" + processed +
                '}';
    }
}