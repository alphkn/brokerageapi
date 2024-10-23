package com.inghubs.brokerageapi.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.inghubs.brokerageapi.constant.AssetCodes;
import com.inghubs.brokerageapi.constant.CommonConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents an asset owned by a customer in the brokerage system.
 */
@Data
@AllArgsConstructor // Generates a constructor with all fields
@NoArgsConstructor  // Generates a no-arguments constructor
@Entity // Indicates that this class is a JPA entity
public class Asset {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicates that the ID should be generated automatically
    private Long id; // Unique identifier for the asset

    @ManyToOne // Defines a many-to-one relationship with the Customer entity
    @JoinColumn(name = CommonConstants.CUSTOMER_ID) // Specifies the foreign key column name
    @JsonBackReference // Prevents infinite recursion during JSON serialization
    private Customer customer; // The customer that owns this asset

    @Enumerated(EnumType.STRING) // Specifies that the enum should be stored as a string in the database
    @Column(nullable = false) // Indicates that this field cannot be null
    private AssetCodes assetCode; // The code representing the type of asset

    @Column(nullable = false) // Indicates that this field cannot be null
    private BigDecimal size; // Total size of the asset

    @Column(nullable = false) // Indicates that this field cannot be null
    private BigDecimal usableSize; // Usable size of the asset available for transactions

    public String toString() {
        return "Asset{" +
                "id=" + id +
                ", assetCode=" + assetCode +
                ", size=" + size +
                ", usableSize=" + usableSize +
                '}';
    }

    // Additional validation or business logic methods can be added here if necessary
}
