package com.ing.brokeragefirm.asset.service;

import com.ing.brokeragefirm.asset.domain.Asset;
import com.ing.brokeragefirm.asset.domain.AssetRepository;
import com.ing.brokeragefirm.customer.domain.Customer;
import com.ing.brokeragefirm.customer.service.CustomerService;
import com.ing.brokeragefirm.exception.ApiException;
import com.ing.brokeragefirm.order.domain.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final EntityManager em;
    private final CustomerService customerService;

    @Transactional
    public void doReserve(Long customerId, String assetName, Order.OrderSide orderSide, Double requestPrice, Double requestSize) {
        final Asset tryAsset = getAsset(customerId, "TRY");

        if (tryAsset == null) {
            throw new ApiException(1002, "Customer does not have any {0} asset", "TRY");
        }

        Double orderTotalPrice = requestPrice * requestSize;

        if (Order.OrderSide.BUY.equals(orderSide)) {

            if (tryAsset.getUsableSize().compareTo(orderTotalPrice) < 0) {
                throw new ApiException(1003, "Insufficient {0} asset for the order", "TRY");
            }

            em.lock(tryAsset, LockModeType.PESSIMISTIC_WRITE);
            tryAsset.setUsableSize(tryAsset.getUsableSize() - orderTotalPrice);
            assetRepository.save(tryAsset);

        } else if (Order.OrderSide.SELL.equals(orderSide)) {

            if (assetName.equals("TRY")) {
                throw new ApiException(1006, "Invalid order, cannot sell TRY asset");
            }

            final Asset orderAsset = getAsset(customerId, assetName);

            if (orderAsset == null) {
                throw new ApiException(1002, "Customer does not have any {0} asset", assetName);
            }

            if (orderAsset.getUsableSize().compareTo(orderTotalPrice) < 0) {
                throw new ApiException(1003, "Insufficient {0} asset for the order", orderAsset.getName());
            }

            em.lock(orderAsset, LockModeType.PESSIMISTIC_WRITE);
            orderAsset.setUsableSize(orderAsset.getUsableSize() - orderTotalPrice);
            assetRepository.save(orderAsset);
        }
    }

    @Transactional
    public void undoReserve(Long customerId, String assetName, Order.OrderSide orderSide, Double requestPrice, Double requestSize) {
        final Asset tryAsset = getAsset(customerId, "TRY");

        if (tryAsset == null) {
            throw new ApiException(1002, "Customer does not have any {0} asset", "TRY");
        }

        Double orderTotalPrice = requestPrice * requestSize;


        if (Order.OrderSide.BUY.equals(orderSide)) {
            em.lock(tryAsset, LockModeType.PESSIMISTIC_WRITE);
            tryAsset.setUsableSize(tryAsset.getUsableSize() + orderTotalPrice);
            assetRepository.save(tryAsset);

        } else if (Order.OrderSide.SELL.equals(orderSide)) {

            final Asset orderAsset = getAsset(customerId, assetName);

            if (orderAsset == null) {
                throw new ApiException(1002, "Customer does not have any {0} asset", assetName);
            }

            em.lock(orderAsset, LockModeType.PESSIMISTIC_WRITE);
            orderAsset.setUsableSize(orderAsset.getUsableSize() + orderTotalPrice);
            assetRepository.save(orderAsset);
        }
    }


    private Asset getAsset(Long customerId, String assetName) {
        return assetRepository.findByCustomerIdAndName(customerId, assetName);
    }

    @Transactional
    public List<Asset> listAssets(Long customerId) {
        return assetRepository.findAllByCustomerId(customerId);
    }

    public void doApplyReserve(Long customerId, String assetName, Order.OrderSide orderSide, Double requestPrice, Double requestSize) {
        final Asset tryAsset = getAsset(customerId, "TRY");

        if (tryAsset == null) {
            throw new ApiException(1002, "Customer does not have any {0} asset", "TRY");
        }

        Double orderTotalPrice = requestPrice * requestSize;

        if (Order.OrderSide.BUY.equals(orderSide)) {

            em.lock(tryAsset, LockModeType.PESSIMISTIC_WRITE);
            tryAsset.setSize(tryAsset.getSize() - orderTotalPrice);
            assetRepository.save(tryAsset);

            Asset orderAsset = getAsset(customerId, assetName);

            if (orderAsset == null) {
                orderAsset = initAsset(customerId, assetName);
            } else {
                em.lock(orderAsset, LockModeType.PESSIMISTIC_WRITE);
            }

            orderAsset.setSize(orderAsset.getSize() + orderTotalPrice);
            orderAsset.setUsableSize(orderAsset.getUsableSize() + orderTotalPrice);
            assetRepository.save(orderAsset);

        } else if (Order.OrderSide.SELL.equals(orderSide)) {
            Asset orderAsset = getAsset(customerId, assetName);
            if (orderAsset == null) {
                throw new ApiException(1002, "Customer does not have any {0} asset", assetName);
            }

            em.lock(tryAsset, LockModeType.PESSIMISTIC_WRITE);
            em.lock(orderAsset, LockModeType.PESSIMISTIC_WRITE);

            tryAsset.setSize(tryAsset.getSize() + orderTotalPrice);
            tryAsset.setUsableSize(tryAsset.getUsableSize() + orderTotalPrice);
            assetRepository.save(tryAsset);

            orderAsset.setSize(orderAsset.getSize() - orderTotalPrice);
            assetRepository.save(orderAsset);

        }
    }

    private Asset initAsset(Long customerId, String assetName) {
        Asset asset = new Asset();
        Customer customer = new Customer();
        customer.setId(customerId);
        asset.setCustomer(customer);
        asset.setName(assetName);
        asset.setSize(0.0);
        asset.setUsableSize(0.0);
        return asset;
    }

    public Asset createAsset(Long customerId, String assetName, Double assetSize) {
        // make sure customer exists
        customerService.getCustomer(customerId);

        Asset asset = getAsset(customerId, assetName);

        if (asset == null) {
            asset = initAsset(customerId, assetName);
        }

        asset.setSize(assetSize);
        asset.setUsableSize(assetSize);
        return assetRepository.save(asset);
    }
}
