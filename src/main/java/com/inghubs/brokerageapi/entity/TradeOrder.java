package com.inghubs.brokerageapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.inghubs.brokerageapi.constant.AssetCodes;
import com.inghubs.brokerageapi.constant.CommonConstants;
import com.inghubs.brokerageapi.constant.OrderSide;
import com.inghubs.brokerageapi.constant.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents a trade order in the brokerage system, which can either be a buy or sell order.
 */
@Data
@AllArgsConstructor // Generates a constructor with all fields
@NoArgsConstructor  // Generates a no-arguments constructor
@Entity // Indicates that this class is a JPA entity
public class TradeOrder {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicates that the ID should be generated automatically
    private Long id; // Unique identifier for the trade order

    @ManyToOne // Defines a many-to-one relationship with the Customer entity
    @JoinColumn(name = CommonConstants.CUSTOMER_ID) // Specifies the foreign key column name for the customer
    @JsonBackReference // Prevents infinite recursion in JSON serialization
    private Customer customer; // The customer associated with this trade order

    @Enumerated(EnumType.STRING) // Specifies that this field should be stored as a string in the database
    @Column(nullable = false) // Indicates that this field cannot be null
    private AssetCodes assetCode; // The asset code for the order

    @Enumerated(EnumType.STRING) // Specifies that this field should be stored as a string in the database
    @Column(nullable = false) // Indicates that this field cannot be null
    private OrderSide orderSide; // The side of the order (BUY or SELL)

    @Column(nullable = false) // Indicates that this field cannot be null
    private BigDecimal size; // The size of the order (amount of the asset)

    @Column(nullable = false) // Indicates that this field cannot be null
    private BigDecimal price; // The price at which the order is placed

    @Enumerated(EnumType.STRING) // Specifies that this field should be stored as a string in the database
    @Column(nullable = false) // Indicates that this field cannot be null
    private OrderStatus status; // The current status of the order (e.g., PENDING, FILLED)

    @Column(nullable = false) // Indicates that this field cannot be null
    private LocalDateTime createDate; // The date and time when the order was created

    @PrePersist // Indicates that this method should be called before the entity is persisted
    private void prePersist() {
        createDate = LocalDateTime.now(); // Sets the creation date to the current date and time
    }

    @Override
    public String toString() {
        return "TradeOrder{" +
                "id=" + id +
                ", assetCode=" + assetCode +
                ", orderSide=" + orderSide +
                ", size=" + size +
                ", price=" + price +
                ", status=" + status +
                ", createDate=" + createDate +
                '}';
    }
}