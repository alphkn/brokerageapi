package com.inghubs.brokerageapi.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.inghubs.brokerageapi.constant.AssetCodes;
import com.inghubs.brokerageapi.constant.CommonConstants;
import com.inghubs.brokerageapi.constant.OrderSide;
import com.inghubs.brokerageapi.constant.OrderStatus;
import com.inghubs.brokerageapi.entity.Trade;
import com.inghubs.brokerageapi.entity.TradeOrder;
import com.inghubs.brokerageapi.repository.TradeOrderRepository;
import com.inghubs.brokerageapi.repository.TradeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service for matching trade orders.
 */
@Service
@Transactional
@Slf4j
public class TradeMatchingService {
    private final TradeOrderRepository tradeOrderRepository;
    private final TradeRepository tradeRepository;
    private final AssetService assetService;

    @Autowired
    public TradeMatchingService(TradeOrderRepository tradeOrderRepository, TradeRepository tradeRepository, AssetService assetService) {
        this.tradeOrderRepository = tradeOrderRepository;
        this.tradeRepository = tradeRepository;
        this.assetService = assetService;
    }

    /**
     * Matches buy and sell orders for the given asset code.
     *
     * @param assetCode the asset code for which orders are matched
     */
    public void matchOrders(AssetCodes assetCode) {
        log.info("Matching orders for asset code: {}", assetCode);

        List<OrderStatus> statuses = Arrays.asList(OrderStatus.PENDING, OrderStatus.PARTIALLY_FILLED);

        // Retrieve active buy orders sorted by price (highest first)
        List<TradeOrder> buyOrders = tradeOrderRepository.findByAssetCodeAndOrderSideAndStatusIn(
                assetCode, OrderSide.BUY, statuses, Sort.by(Sort.Direction.DESC, CommonConstants.PRICE, CommonConstants.CREATE_DATE));

        // Retrieve active sell orders sorted by price (lowest first)
        List<TradeOrder> sellOrders = tradeOrderRepository.findByAssetCodeAndOrderSideAndStatusIn(
                assetCode, OrderSide.SELL, statuses, Sort.by(Sort.Direction.ASC, CommonConstants.PRICE, CommonConstants.CREATE_DATE));

        log.info("Found {} buy orders and {} sell orders for asset code: {}", buyOrders.size(), sellOrders.size(), assetCode);

        // Match orders
        for (TradeOrder buyOrder : buyOrders) {
            for (Iterator<TradeOrder> iterator = sellOrders.iterator(); iterator.hasNext(); ) {
                TradeOrder sellOrder = iterator.next();

                // Check if orders can be matched
                if (canMatch(buyOrder, sellOrder)) {
                    log.info("Matching buy order ID {} with sell order ID {}", buyOrder.getId(), sellOrder.getId());
                    
                    // Lock orders before processing to avoid race conditions
                    tradeOrderRepository.lockById(buyOrder.getId());
                    tradeOrderRepository.lockById(sellOrder.getId());
                    
                    executeTrade(buyOrder, sellOrder);

                    // Remove fully executed sell order
                    if (sellOrder.getStatus() == OrderStatus.FILLED) {
                        log.info("Sell order ID {} fully executed and removed from the list.", sellOrder.getId());
                        iterator.remove();
                    }
                    // Break if the buy order is fully executed
                    if (buyOrder.getStatus() == OrderStatus.FILLED) {
                        log.info("Buy order ID {} fully executed.", buyOrder.getId());
                        break;
                    }
                }
            }
        }

        log.info("Completed matching orders for asset code: {}", assetCode);
    }

    /**
     * Determines if a buy order can be matched with a sell order.
     *
     * @param buyOrder the buy order
     * @param sellOrder the sell order
     * @return true if the orders can be matched, false otherwise
     */
    private boolean canMatch(TradeOrder buyOrder, TradeOrder sellOrder) {
        boolean matchable = buyOrder.getPrice().compareTo(sellOrder.getPrice()) >= 0 &&
                !buyOrder.getCustomer().getId().equals(sellOrder.getCustomer().getId());
        log.debug("Checking if buy order ID {} can match with sell order ID {}: {}", buyOrder.getId(), sellOrder.getId(), matchable);
        return matchable;
    }

    /**
     * Executes the trade between a buy order and a sell order.
     *
     * @param buyOrder the buy order
     * @param sellOrder the sell order
     */
    private void executeTrade(TradeOrder buyOrder, TradeOrder sellOrder) {
        // Calculate execution size and price
        BigDecimal executionSize = buyOrder.getSize().min(sellOrder.getSize());
        BigDecimal executionPrice = sellOrder.getPrice();
        BigDecimal tradeTotalAmount = executionPrice.multiply(executionSize);
        BigDecimal predictedTotalAmount = executionSize.multiply(buyOrder.getPrice());

        // Create and save trade
        Trade trade = new Trade();
        trade.setBuyOrder(buyOrder);
        trade.setSellerOrder(sellOrder);
        trade.setExecutedPrice(executionPrice);
        trade.setExecutedSize(executionSize);
        tradeRepository.save(trade);

        log.info("Trade created: {} {} at price {}", executionSize, buyOrder.getAssetCode(), executionPrice);

        // Update orders
        updateOrderStatus(buyOrder, executionSize);
        updateOrderStatus(sellOrder, executionSize);

        // Transfer assets between customers
        Long buyerId = buyOrder.getCustomer().getId();
        Long sellerId = sellOrder.getCustomer().getId();
        AssetCodes assetCode = buyOrder.getAssetCode();

        // Release and withdraw assets
        assetService.releaseAsset(buyerId, AssetCodes.TRY, predictedTotalAmount);
        assetService.withdrawAsset(buyerId, AssetCodes.TRY, tradeTotalAmount);
        assetService.releaseAsset(sellerId, assetCode, executionSize);
        assetService.withdrawAsset(sellerId, assetCode, executionSize);
        assetService.assignAsset(buyerId, assetCode, executionSize);
        assetService.assignAsset(sellerId, AssetCodes.TRY, tradeTotalAmount);

        // Save updated orders
        tradeOrderRepository.save(buyOrder);
        tradeOrderRepository.save(sellOrder);

        log.info("Trade executed: {} {} at price {} between buyer ID {} and seller ID {}", executionSize, assetCode, executionPrice, buyerId, sellerId);
    }

    /**
     * Updates the status of the given order based on the executed size.
     *
     * @param order the order to be updated
     * @param executedSize the size that was executed
     */
    private void updateOrderStatus(TradeOrder order, BigDecimal executedSize) {
        order.setSize(order.getSize().subtract(executedSize));
        if (order.getSize().compareTo(BigDecimal.ZERO) == 0) {
            order.setStatus(OrderStatus.FILLED);
            log.info("Order ID {} has been filled.", order.getId());
        } else {
            order.setStatus(OrderStatus.PARTIALLY_FILLED);
            log.info("Order ID {} is partially filled. Remaining size: {}", order.getId(), order.getSize());
        }
    }
}
