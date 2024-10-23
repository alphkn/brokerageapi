package com.inghubs.brokerageapi.repository;

import java.util.List;
import java.util.Optional;

import com.inghubs.brokerageapi.constant.AssetCodes;
import com.inghubs.brokerageapi.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository interface for managing Asset entities.
 * Extends JpaRepository to provide CRUD operations.
 */
public interface AssetRepository extends JpaRepository<Asset, Long> {

    /**
     * Finds all assets associated with a specific customer.
     *
     * @param customerId the ID of the customer
     * @return a list of assets belonging to the customer
     */
    List<Asset> findByCustomerId(Long customerId);

    /**
     * Finds an asset for a specific customer by asset code.
     *
     * @param customerId the ID of the customer
     * @param assetCode  the code of the asset
     * @return an Optional containing the asset if found, or empty if not
     */
    Optional<Asset> findByCustomerIdAndAssetCode(Long customerId, AssetCodes assetCode);
}