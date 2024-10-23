package com.inghubs.brokerageapi.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents a customer in the brokerage system, including their user information and associated assets and trade orders.
 */
@Data
@AllArgsConstructor // Generates a constructor with all fields
@NoArgsConstructor  // Generates a no-arguments constructor
@Entity // Indicates that this class is a JPA entity
public class Customer {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicates that the ID should be generated automatically
    private Long id; // Unique identifier for the customer

    @OneToOne // Defines a one-to-one relationship with the User entity
    @JoinColumn(name = "user_id") // Specifies the foreign key column name
    @JsonManagedReference // Manages serialization to prevent infinite recursion
    private User user; // The user account associated with this customer

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL) // Defines a one-to-many relationship with the Asset entity
    @JsonManagedReference // Manages serialization to prevent infinite recursion
    private List<Asset> assets; // List of assets owned by the customer

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL) // Defines a one-to-many relationship with the TradeOrder entity
    @JsonManagedReference // Manages serialization to prevent infinite recursion
    private List<TradeOrder> tradeOrders; // List of trade orders placed by the customer

    private boolean isEnabled = false; // Indicates whether the customer's account is enabled

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", user=" + (user != null ? user.getId() : "null") + // Avoids recursion by displaying only user ID
                ", assetsCount=" + (assets != null ? assets.size() : 0) + // Displays the count of assets
                ", tradeOrdersCount=" + (tradeOrders != null ? tradeOrders.size() : 0) + // Displays the count of trade orders
                ", isEnabled=" + isEnabled +
                '}';
    }
    // Additional business logic methods or validation can be added here if necessary
}