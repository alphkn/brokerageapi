package com.inghubs.brokerageapi.service;

import java.math.BigDecimal;
import java.util.List;

import com.inghubs.brokerageapi.constant.AssetCodes;
import com.inghubs.brokerageapi.constant.CommonConstants;
import com.inghubs.brokerageapi.constant.TransactionType;
import com.inghubs.brokerageapi.entity.Asset;
import com.inghubs.brokerageapi.entity.Customer;
import com.inghubs.brokerageapi.entity.Transaction;
import com.inghubs.brokerageapi.exception.InsufficientBalanceException;
import com.inghubs.brokerageapi.repository.AssetRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for managing assets related to customers.
 * This includes listing, assigning, withdrawing, and releasing assets.
 */
@Service
@Transactional
public class AssetService {
    private static final Logger logger = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;
    private final CustomerService customerService;

    public AssetService(AssetRepository assetRepository, CustomerService customerService) {
        this.assetRepository = assetRepository;
        this.customerService = customerService;
    }

    /**
     * Retrieves a list of assets for a given customer.
     *
     * @param customerId the ID of the customer whose assets are to be listed
     * @return a list of assets associated with the customer
     */
    public List<Asset> listAssets(Long customerId) {
        logger.info("Listing assets for customer with ID: {}", customerId);
        return assetRepository.findByCustomerId(customerId);
    }

    /**
     * Assigns an asset to a customer by increasing the asset size.
     *
     * @param customerId the ID of the customer
     * @param assetCodes the asset code to assign
     * @param amount     the amount to be assigned
     */
    public void assignAsset(Long customerId, AssetCodes assetCodes, BigDecimal amount) {
        logger.info("Assigning {} of asset {} to customer ID: {}", amount, assetCodes, customerId);
        Asset asset = assetRepository.findByCustomerIdAndAssetCode(customerId, assetCodes)
                .orElseGet(() -> createAsset(customerService.getEnabledCustomerById(customerId), assetCodes));

        asset.setSize(asset.getSize().add(amount));
        asset.setUsableSize(asset.getUsableSize().add(amount));
        assetRepository.save(asset);
    }

    /**
     * Withdraws an asset from a customer's account by decreasing the asset size.
     *
     * @param customerId the ID of the customer
     * @param assetCodes the asset code to withdraw
     * @param amount     the amount to be withdrawn
     */
    public void withdrawAsset(Long customerId, AssetCodes assetCodes, BigDecimal amount) {
        logger.info("Withdrawing {} of asset {} from customer ID: {}", amount, assetCodes, customerId);
        Asset asset = assetRepository.findByCustomerIdAndAssetCode(customerId, assetCodes)
                .orElseThrow(() -> new InsufficientBalanceException(CommonConstants.ASSET_NOT_FOUND));

        if (asset.getUsableSize().compareTo(amount) < 0) {
            logger.error("Insufficient balance for withdrawal. Customer ID: {}, Asset Code: {}, Requested: {}, Available: {}",
                         customerId, assetCodes, amount, asset.getUsableSize());
            throw new InsufficientBalanceException(CommonConstants.INSUFFICIENT_ASSET_BALANCE);
        }

        asset.setSize(asset.getSize().subtract(amount));
        asset.setUsableSize(asset.getUsableSize().subtract(amount));
        assetRepository.save(asset);
    }

    /**
     * Checks and locks a specified size of an asset for a customer.
     *
     * @param customerId the ID of the customer
     * @param assetCode  the asset code to check
     * @param size       the size to lock
     */
    protected void checkAndLockAsset(Long customerId, AssetCodes assetCode, BigDecimal size) {
        logger.info("Checking and locking {} of asset {} for customer ID: {}", size, assetCode, customerId);
        Asset asset = assetRepository.findByCustomerIdAndAssetCode(customerId, assetCode)
                .orElseThrow(() -> new InsufficientBalanceException(CommonConstants.ASSET_NOT_FOUND));

        if (asset.getUsableSize().compareTo(size) < 0) {
            logger.error("Insufficient balance for locking. Customer ID: {}, Asset Code: {}, Requested: {}, Available: {}",
                         customerId, assetCode, size, asset.getUsableSize());
            throw new InsufficientBalanceException(CommonConstants.INSUFFICIENT_ASSET_BALANCE);
        }
        asset.setUsableSize(asset.getUsableSize().subtract(size));
        assetRepository.save(asset);
    }

    /**
     * Creates a new asset for the specified customer.
     *
     * @param customer  the customer to whom the asset will be assigned
     * @param assetCode the asset code to be assigned
     * @return the created asset
     */
    private Asset createAsset(Customer customer, AssetCodes assetCode) {
        logger.info("Creating new asset for customer ID: {} with asset code: {}", customer.getId(), assetCode);
        Asset asset = new Asset();
        asset.setCustomer(customer);
        asset.setAssetCode(assetCode);
        asset.setSize(BigDecimal.ZERO);
        asset.setUsableSize(BigDecimal.ZERO);
        return assetRepository.save(asset);
    }

    /**
     * Releases a specified size of an asset back to the usable pool for a customer.
     *
     * @param customerId the ID of the customer
     * @param assetCode  the asset code to release
     * @param size       the size to release
     */
    public void releaseAsset(Long customerId, AssetCodes assetCode, BigDecimal size) {
        logger.info("Releasing {} of asset {} for customer ID: {}", size, assetCode, customerId);
        Asset asset = assetRepository.findByCustomerIdAndAssetCode(customerId, assetCode)
                .orElseGet(() -> createAsset(customerService.getEnabledCustomerById(customerId), assetCode));
        asset.setUsableSize(asset.getUsableSize().add(size));
        assetRepository.save(asset);
    }

    /**
     * Processes a transaction, either withdrawing or depositing assets based on the transaction type.
     *
     * @param transaction the transaction to process
     */
    public void processTransaction(Transaction transaction) {
        logger.info("Processing transaction: {}", transaction);
        if (transaction.getType().equals(TransactionType.WITHDRAWAL)) {
            withdrawAsset(transaction.getCustomer().getId(), AssetCodes.TRY, transaction.getAmount());
        } else if (transaction.getType().equals(TransactionType.DEPOSIT)) {
            assignAsset(transaction.getCustomer().getId(), AssetCodes.TRY, transaction.getAmount());
        }
    }
}
