package com.ing.brokeragefirm.asset.service;

import com.ing.brokeragefirm.asset.domain.Asset;
import com.ing.brokeragefirm.asset.domain.AssetRepository;
import com.ing.brokeragefirm.customer.domain.Customer;
import com.ing.brokeragefirm.customer.service.CustomerService;
import com.ing.brokeragefirm.order.domain.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AssetService assetService;

    private final Long customerId = 1L;
    private final String assetName = "XAU";
    private final Double requestPrice = 100.0;
    private final Double requestSize = 10.0;
    private Customer customer;

    @BeforeEach
    void setUp() {
        this.customer = new Customer();
        this.customer.setId(customerId);

    }

    @Test
    void testDoReserve_Success() {
        // Arrange
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(requestSize * requestPrice);
        mockAsset.setSize(requestSize * requestPrice);

        Asset tryAsset = new Asset();
        tryAsset.setName("TRY");
        tryAsset.setUsableSize(requestSize * requestPrice);
        tryAsset.setSize(requestSize * requestPrice);

        tryAsset.setCustomer(customer);

        when(assetRepository.findByCustomerIdAndName(customerId, "TRY")).thenReturn(tryAsset);

        // Act
        assetService.doReserve(customerId, assetName, Order.OrderSide.BUY, requestPrice, requestSize);

        // Assert
        verify(assetRepository, times(1)).findByCustomerIdAndName(customerId, "TRY");
        verify(entityManager, times(1)).lock(tryAsset, LockModeType.PESSIMISTIC_WRITE);
    }

    @Test
    void testUndoReserve_Success() {
        // Arrange
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(requestSize * requestPrice);
        mockAsset.setSize(requestSize * requestPrice);

        Asset tryAsset = new Asset();
        tryAsset.setName("TRY");
        tryAsset.setUsableSize(requestSize * requestPrice);
        tryAsset.setSize(requestSize * requestPrice);
        when(assetRepository.findByCustomerIdAndName(customerId, assetName)).thenReturn(mockAsset);
        when(assetRepository.findByCustomerIdAndName(customerId, "TRY")).thenReturn(tryAsset);

        // Act
        assetService.undoReserve(customerId, assetName, Order.OrderSide.SELL, requestPrice, requestSize);

        // Assert
        verify(assetRepository, times(1)).findByCustomerIdAndName(customerId, "TRY");
        // Add more verifications based on the implementation specifics
    }

    @Test
    void testListAssets_Success() {
        // Arrange
        Asset asset1 = new Asset();
        Asset asset2 = new Asset();
        List<Asset> mockAssets = Arrays.asList(asset1, asset2);
        when(assetRepository.findAllByCustomerId(customerId)).thenReturn(mockAssets);

        // Act
        List<Asset> result = assetService.listAssets(customerId);

        // Assert
        assertEquals(2, result.size());
        verify(assetRepository, times(1)).findAllByCustomerId(customerId);
    }

    @Test
    void testDoApplyReserve_Success() {
        // Arrange
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(requestSize * requestPrice);
        mockAsset.setSize(requestSize * requestPrice);

        Asset tryAsset = new Asset();
        tryAsset.setName("TRY");
        tryAsset.setUsableSize(requestSize * requestPrice);
        tryAsset.setSize(requestSize * requestPrice);

        when(assetRepository.findByCustomerIdAndName(customerId, assetName)).thenReturn(mockAsset);
        when(assetRepository.findByCustomerIdAndName(customerId, "TRY")).thenReturn(tryAsset);

        // Act
        assetService.doApplyReserve(customerId, assetName, Order.OrderSide.BUY, requestPrice, requestSize);

        // Assert
        verify(assetRepository, times(1)).findByCustomerIdAndName(customerId, assetName);
        verify(assetRepository, times(1)).findByCustomerIdAndName(customerId, "TRY");
        // Add more verifications based on the implementation specifics
    }

    @Test
    void testCreateAsset_Success() {
        // Arrange
        Asset newAsset = new Asset();
        newAsset.setId(1L);
        newAsset.setName(assetName);
        when(assetRepository.save(any(Asset.class))).thenReturn(newAsset);

        // Act
        Asset createdAsset = assetService.createAsset(customerId, assetName, requestSize);

        // Assert
        assertNotNull(createdAsset);
        assertEquals(assetName, createdAsset.getName());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void testListAssets_EmptyResult() {
        // Arrange
        when(assetRepository.findAllByCustomerId(customerId)).thenReturn(Collections.emptyList());

        // Act
        List<Asset> result = assetService.listAssets(customerId);

        // Assert
        assertTrue(result.isEmpty());
        verify(assetRepository, times(1)).findAllByCustomerId(customerId);
    }
}