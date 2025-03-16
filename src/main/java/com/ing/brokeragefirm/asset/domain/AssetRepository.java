package com.ing.brokeragefirm.asset.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query("Select a From Asset a where a.customer.id = :customerId")
    List<Asset> findByCustomerId(Long customerId);

    @Query("Select a From Asset a where a.customer.id = :customerId and a.assetName=:assetName")
    Asset findByCustomerIdAndAssetName(Long customerId, String assetName);
}
