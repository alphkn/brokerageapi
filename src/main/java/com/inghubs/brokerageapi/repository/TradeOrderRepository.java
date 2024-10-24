package com.inghubs.brokerageapi.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.inghubs.brokerageapi.constant.AssetCodes;
import com.inghubs.brokerageapi.constant.OrderSide;
import com.inghubs.brokerageapi.constant.OrderStatus;
import com.inghubs.brokerageapi.entity.TradeOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;


/**
 * Repository interface for managing TradeOrder entities.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 */
public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {

    /**
     * Finds TradeOrders for a specific customer within a given date range.
     *
     * @param customerId the ID of the customer
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @param pageable pagination information
     * @return a list of TradeOrders matching the criteria
     */
    List<TradeOrder> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Finds TradeOrders by their status.
     *
     * @param status the status of the TradeOrders
     * @param pageable pagination information
     * @return a list of TradeOrders with the specified status
     */
    List<TradeOrder> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * Finds a TradeOrder by its ID and status.
     *
     * @param id the ID of the TradeOrder
     * @param status the expected status of the TradeOrder
     * @return an Optional containing the found TradeOrder or empty if not found
     */
    Optional<TradeOrder> findByIdAndStatus(Long id, OrderStatus status);

    /**
     * Finds TradeOrders by asset code, order side, and a list of statuses.
     *
     * @param assetCode the asset code associated with the TradeOrders
     * @param orderSide the side of the order (BUY or SELL)
     * @param statuses the list of statuses to filter by
     * @param sort sorting criteria
     * @return a list of TradeOrders matching the specified criteria
     */
    List<TradeOrder> findByAssetCodeAndOrderSideAndStatusIn(AssetCodes assetCode, OrderSide orderSide, List<OrderStatus> statuses, Sort sort);

    /**
     * Retrieves a TradeOrder by its ID using a pessimistic write lock.
     * This ensures that no other transaction can update or delete the same TradeOrder
     * until the current transaction completes, preventing data inconsistencies.
     *
     * @param id The ID of the TradeOrder to be retrieved.
     * @return The TradeOrder entity locked for writing.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE) // Ensures exclusive access to the TradeOrder record.
    @Query("SELECT o FROM TradeOrder o WHERE o.id = :id")
    TradeOrder findByIdWithLock(Long id);
}
