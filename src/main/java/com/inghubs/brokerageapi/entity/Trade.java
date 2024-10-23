package com.inghubs.brokerageapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * Represents a trade transaction between two trade orders in the brokerage system.
 */
@Data
@AllArgsConstructor // Generates a constructor with all fields
@NoArgsConstructor  // Generates a no-arguments constructor
@Entity // Indicates that this class is a JPA entity
public class Trade {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicates that the ID should be generated automatically
    private Long id; // Unique identifier for the trade

    @ManyToOne // Defines a many-to-one relationship with the TradeOrder entity
    @JoinColumn(name = "buyer_order_id") // Specifies the foreign key column name for the buyer's order
    private TradeOrder buyOrder; // The trade order that bought the asset

    @ManyToOne // Defines a many-to-one relationship with the TradeOrder entity
    @JoinColumn(name = "seller_order_id") // Specifies the foreign key column name for the seller's order
    private TradeOrder sellerOrder; // The trade order that sold the asset

    @Column(nullable = false) // Indicates that this field cannot be null
    private BigDecimal executedPrice; // The price at which the trade was executed

    @Column(nullable = false) // Indicates that this field cannot be null
    private BigDecimal executedSize; // The size of the asset that was traded

    @Column(nullable = false) // Indicates that this field cannot be null
    private LocalDateTime executionDate; // The date and time when the trade was executed

    @PrePersist // Indicates that this method should be called before the entity is persisted
    private void prePersist() {
        executionDate = LocalDateTime.now(); // Sets the execution date to the current date and time
    }

    @Override
    public String toString() {
        return "Trade{" +
                "id=" + id +
                ", buyOrderId=" + (buyOrder != null ? buyOrder.getId() : "null") + // Displays the buy order ID
                ", sellerOrderId=" + (sellerOrder != null ? sellerOrder.getId() : "null") + // Displays the seller order ID
                ", executedPrice=" + executedPrice +
                ", executedSize=" + executedSize +
                ", executionDate=" + executionDate +
                '}';
    }
}