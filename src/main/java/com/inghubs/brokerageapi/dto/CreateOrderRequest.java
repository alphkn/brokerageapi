package com.inghubs.brokerageapi.dto;

import java.math.BigDecimal;

import com.inghubs.brokerageapi.constant.AssetCodes;
import com.inghubs.brokerageapi.constant.OrderSide;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


/**
 * Represents a request to create a new trade order, containing all necessary details.
 */
@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "Customer ID cannot be null") // Validation constraint to ensure customerId is provided
    private Long customerId; // The ID of the customer placing the order

    @NotNull(message = "Asset Code cannot be null") // Validation constraint to ensure assetCode is provided
    private AssetCodes assetCode; // The code of the asset for the order

    @NotNull(message = "Order Side cannot be null") // Validation constraint to ensure side is provided
    private OrderSide side; // Indicates whether the order is a buy or sell

    @NotNull(message = "Size cannot be null") // Validation constraint to ensure size is provided
    @DecimalMin(value = "0.01", message = "Size must be greater than zero") // Validation constraint for size
    private BigDecimal size; // The size (quantity) of the asset being ordered

    @NotNull(message = "Price cannot be null") // Validation constraint to ensure price is provided
    @DecimalMin(value = "0.01", message = "Price must be greater than zero") // Validation constraint for price
    private BigDecimal price; // The price at which the asset is to be ordered

    // Additional logging can be added in service or controller where this request is processed
}
