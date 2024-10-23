package com.inghubs.brokerageapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.inghubs.brokerageapi.constant.AssetCodes;
import com.inghubs.brokerageapi.constant.CommonConstants;
import com.inghubs.brokerageapi.constant.OrderSide;
import com.inghubs.brokerageapi.constant.OrderStatus;
import com.inghubs.brokerageapi.entity.Customer;
import com.inghubs.brokerageapi.entity.TradeOrder;
import com.inghubs.brokerageapi.exception.OrderNotFoundException;
import com.inghubs.brokerageapi.repository.TradeOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing trade orders.
 */
@Service
@Transactional
public class TradeOrderService {
    private static final Logger log = LoggerFactory.getLogger(TradeOrderService.class);

    private final TradeOrderRepository orderRepository;
    private final AssetService assetService;
    private final CustomerService customerService;

    public TradeOrderService(TradeOrderRepository orderRepository, AssetService assetService, CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.assetService = assetService;
        this.customerService = customerService;
    }

    /**
     * Creates a trade order for a customer.
     *
     * @param customerId the ID of the customer placing the order
     * @param assetCode the asset code for the trade order
     * @param side the side of the order (buy/sell)
     * @param size the size of the order
     * @param price the price of the order
     * @return the created trade order
     */
    public TradeOrder createTradeOrder(Long customerId, AssetCodes assetCode, OrderSide side, BigDecimal size, BigDecimal price) {
        log.info("Creating trade order: Customer ID = {}, Asset Code = {}, Side = {}, Size = {}, Price = {}",
                 customerId, assetCode, side, size, price);

        Customer customer = customerService.getEnabledCustomerById(customerId);

        // Lock assets based on the order side
        if (side == OrderSide.BUY) {
            BigDecimal totalCost = size.multiply(price);
            assetService.checkAndLockAsset(customerId, AssetCodes.TRY, totalCost);
            log.info("Locked TRY for Customer ID {}: Amount = {}", customerId, totalCost);
        } else {
            assetService.checkAndLockAsset(customerId, assetCode, size);
            log.info("Locked {} for Customer ID {}: Size = {}", assetCode, customerId, size);
        }

        // Create and save the trade order
        TradeOrder tradeOrder = new TradeOrder();
        tradeOrder.setCustomer(customer);
        tradeOrder.setAssetCode(assetCode);
        tradeOrder.setOrderSide(side);
        tradeOrder.setSize(size);
        tradeOrder.setPrice(price);
        tradeOrder.setStatus(OrderStatus.PENDING);
        tradeOrder.setCreateDate(LocalDateTime.now());

        TradeOrder savedOrder = orderRepository.save(tradeOrder);
        log.info("Trade order created successfully: {}", savedOrder);
        return savedOrder;
    }

    /**
     * Lists trade orders for a customer within a specified date range.
     *
     * @param customerId the ID of the customer
     * @param startDate the start date of the date range
     * @param endDate the end date of the date range
     * @param page the page number for pagination
     * @param size the number of orders per page
     * @return a list of trade orders
     */
    public List<TradeOrder> listTradeOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        log.info("Listing trade orders for Customer ID = {} between {} and {}", customerId, startDate, endDate);

        Pageable pageable = PageRequest.of(page, size);
        List<TradeOrder> tradeOrders = orderRepository.findByCustomerIdAndCreateDateBetween(customerId, startDate, endDate, pageable);

        log.info("Retrieved {} trade orders for Customer ID {}", tradeOrders.size(), customerId);
        return tradeOrders;
    }

    /**
     * Cancels a pending trade order and releases locked assets.
     *
     * @param tradeOrderId the ID of the trade order to cancel
     */
    public void cancelTradeOrder(Long tradeOrderId) {
        log.info("Canceling trade order ID = {}", tradeOrderId);
        TradeOrder tradeOrder = getPendingTradeOrder(tradeOrderId);

        // Release locked assets based on the order side
        if (tradeOrder.getOrderSide() == OrderSide.BUY) {
            BigDecimal totalCost = tradeOrder.getSize().multiply(tradeOrder.getPrice());
            assetService.releaseAsset(tradeOrder.getCustomer().getId(), AssetCodes.TRY, totalCost);
            log.info("Released locked TRY for Customer ID {}: Amount = {}", tradeOrder.getCustomer().getId(), totalCost);
        } else {
            assetService.releaseAsset(tradeOrder.getCustomer().getId(), tradeOrder.getAssetCode(), tradeOrder.getSize());
            log.info("Released locked {} for Customer ID {}: Size = {}", tradeOrder.getAssetCode(), tradeOrder.getCustomer().getId(), tradeOrder.getSize());
        }

        // Update order status to canceled
        tradeOrder.setStatus(OrderStatus.CANCELED);
        orderRepository.save(tradeOrder);
        log.info("Trade order ID {} has been canceled", tradeOrderId);
    }

    /**
     * Retrieves the customer ID associated with a trade order.
     *
     * @param tradeOrderId the ID of the trade order
     * @return the customer ID
     */
    public Long getCustomerId(Long tradeOrderId) {
        log.info("Retrieving customer ID for trade order ID = {}", tradeOrderId);
        TradeOrder tradeOrder = getPendingTradeOrder(tradeOrderId);
        Long customerId = tradeOrder.getCustomer().getId();
        log.info("Customer ID for trade order ID {}: {}", tradeOrderId, customerId);
        return customerId;
    }

    /**
     * Retrieves a pending trade order by its ID.
     *
     * @param tradeOrderId the ID of the trade order
     * @return the trade order
     * @throws OrderNotFoundException if the trade order is not found or not in pending status
     */
    private TradeOrder getPendingTradeOrder(Long tradeOrderId) {
        log.info("Retrieving pending trade order ID = {}", tradeOrderId);
        return orderRepository.findByIdAndStatus(tradeOrderId, OrderStatus.PENDING)
                .orElseThrow(() -> {
                    log.error("Order not found or not in pending status for ID = {}", tradeOrderId);
                    return new OrderNotFoundException(CommonConstants.ORDER_NOT_FOUND_OR_NOT_IN_PENDING_STATUS);
                });
    }
}
