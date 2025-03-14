package com.ing.brokeragefirm.asset.service;

import com.ing.brokeragefirm.asset.domain.Asset;
import com.ing.brokeragefirm.asset.domain.AssetRepository;
import com.ing.brokeragefirm.order.api.OrderRequest;
import com.ing.brokeragefirm.order.domain.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Asset> criteriaQuery;

    @Mock
    private Root<Asset> root;

    @Mock
    private TypedQuery<Asset> typedQuery;

    @InjectMocks
    private AssetService assetService;

    @BeforeEach
    void setupMocks() {
    }

    @Test
    void checkAssetAvailability_ShouldThrowException_WhenCustomerIdIsNull() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Asset.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Asset.class)).thenReturn(root);


        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(null);

        Exception exception = assertThrows(RuntimeException.class,
                () -> assetService.checkAssetAvailability(orderRequest));

        assertEquals("CustomerId should be provided", exception.getMessage());
    }

    @Test
    void checkAssetAvailability_ShouldThrowException_WhenAssetNameIsNull() {

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Asset.class)).thenReturn(criteriaQuery);


        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        orderRequest.setAssetName(null);

        Exception exception = assertThrows(RuntimeException.class,
                () -> assetService.checkAssetAvailability(orderRequest));

        assertEquals("AssetName should be provided", exception.getMessage());
    }

    @Test
    void checkAssetAvailability_ShouldThrowException_WhenTryAssetNotFound() {

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        orderRequest.setAssetName("Gold");

        // No TRY assets found
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(RuntimeException.class,
                () -> assetService.checkAssetAvailability(orderRequest));

        assertEquals("Customer does not have any TRY asset", exception.getMessage());
    }

    @Test
    void checkAssetAvailability_ShouldThrowException_WhenBuyingAndNoSufficientFunds() {

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Asset.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Asset.class)).thenReturn(root);
        when(entityManager.createQuery(any(CriteriaQuery.class))).thenReturn(typedQuery);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        orderRequest.setAssetName("Gold");
        orderRequest.setOrderSide(Order.OrderSide.BUY);
        orderRequest.setPrice(100.0);
        orderRequest.setSize(2.0);

        Asset tryAsset = new Asset();
        tryAsset.setSize(150.0);  // Insufficient funds for the order (200)

        when(typedQuery.getResultList()).thenReturn(Collections.singletonList(tryAsset));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> assetService.checkAssetAvailability(orderRequest));

        assertEquals("Insufficient TRY asset for the order", exception.getMessage());
    }

    @Test
    void listAssets_ShouldReturnAssetsForCustomerId() {

        Long customerId = 1L;
        List<Asset> mockAssets = Arrays.asList(new Asset(), new Asset());

        when(assetRepository.findByCustomerId(customerId)).thenReturn(mockAssets);

        List<Asset> result = assetService.listAssets(customerId);

        assertEquals(2, result.size());
        verify(assetRepository, times(1)).findByCustomerId(customerId);
    }

    @Test
    void updateAssetAfterCancel_ShouldThrowException_WhenCustomerDoesNotOwnAsset() {

        Order order = new Order();
        order.setCustomerId(1L);
        order.setAssetName("Gold");

        when(assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName())).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class,
                () -> assetService.updateAssetAfterCancel(order));

        assertEquals("Customer does not own that asset", exception.getMessage());
    }

    @Test
    void updateAssetAfterCancel_ShouldUpdateAssetSize() {

        Order order = new Order();
        order.setCustomerId(1L);
        order.setAssetName("Gold");
        order.setSize(5.0);

        Asset mockAsset = new Asset();
        mockAsset.setSize(10.0);

        when(assetRepository.findByCustomerIdAndAssetName(order.getCustomerId(), order.getAssetName())).thenReturn(mockAsset);

        assetService.updateAssetAfterCancel(order);

        assertEquals(15.0, mockAsset.getSize());
        verify(assetRepository, times(1)).save(mockAsset);
    }
}