package com.ing.brokeragefirm.asset.service;

import com.ing.brokeragefirm.asset.domain.Asset;
import com.ing.brokeragefirm.asset.domain.AssetRepository;
import com.ing.brokeragefirm.order.api.OrderRequest;
import com.ing.brokeragefirm.order.domain.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final EntityManager em;

    @Transactional
    public void checkAssetAvailability(OrderRequest orderRequest) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<Asset> query = cb.createQuery(Asset.class);
        final Root<Asset> root = query.from(Asset.class);

        if (orderRequest.getCustomerId() == null) {
            throw new RuntimeException("CustomerId should be provided");
        }

        if (orderRequest.getAssetName() == null) {
            throw new RuntimeException("AssetName should be provided");
        }

        query.where(
                cb.and(
                        cb.equal(root.get("customerId"), orderRequest.getCustomerId()),
                        cb.equal(root.get("assetName"), orderRequest.getAssetName())
                )
        );

        final List<Asset> orderAssetResult = em.createQuery(query).getResultList();

        final Asset orderAsset = !orderAssetResult.isEmpty() ? orderAssetResult.get(0) : null;

        query.where(
                cb.and(
                        cb.equal(root.get("customerId"), orderRequest.getCustomerId()),
                        cb.equal(root.get("assetName"), "TRY")
                )
        );

        final List<Asset> tryAssetResult = em.createQuery(query).getResultList();


        if (tryAssetResult.isEmpty()) {
            throw new RuntimeException("Customer does not have any TRY asset");
        }

        final Asset tryAsset = tryAssetResult.get(0);


        if (Order.OrderSide.BUY.equals(orderRequest.getOrderSide())) {
            Double orderTotalPrice = orderRequest.getPrice() * orderRequest.getSize();
            if (tryAsset.getSize().compareTo(orderTotalPrice) < 0) {
                throw new IllegalArgumentException("Insufficient TRY asset for the order");
            }
        } else if (Order.OrderSide.SELL.equals(orderRequest.getOrderSide())) {
            if (orderAsset == null) {
                throw new RuntimeException("Customer does not own that asset");
            }
            if (orderAsset.getSize().compareTo(orderRequest.getSize()) < 0) {
                throw new IllegalArgumentException("Insufficient asset to sell");
            }
        }
    }

    public List<Asset> listAssets(Long customerId) {
        return assetRepository.findByCustomerId(customerId);
    }

    public void updateAssetAfterCancel(Order order) {

        Asset asset = assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName());
        if (asset == null) {
            throw new RuntimeException("Customer does not own that asset");
        }

        asset.setSize(asset.getSize() + order.getSize());
        assetRepository.save(asset);
    }
}
