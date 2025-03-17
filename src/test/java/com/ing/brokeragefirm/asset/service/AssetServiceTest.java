package com.ing.brokeragefirm.asset.service;

import com.ing.brokeragefirm.asset.domain.Asset;
import com.ing.brokeragefirm.asset.domain.AssetRepository;
import com.ing.brokeragefirm.customer.domain.Customer;
import com.ing.brokeragefirm.customer.service.CustomerService;
import com.ing.brokeragefirm.exception.ApiException;
import com.ing.brokeragefirm.order.domain.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Captor
    private ArgumentCaptor<Asset> assetCaptor;

    private final Long customerId = 1L;
    public final String baseAssetName = "TRY";

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
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(requestSize * requestPrice);
        tryAsset.setSize(requestSize * requestPrice);

        tryAsset.setCustomer(customer);

        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);

        // Act
        assetService.doReserve(customerId, assetName, Order.OrderSide.BUY, requestPrice, requestSize);

        // Assert
        verify(assetRepository, times(1)).findByCustomerIdAndName(customerId, baseAssetName);
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
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(requestSize * requestPrice);
        tryAsset.setSize(requestSize * requestPrice);
        when(assetRepository.findByCustomerIdAndName(customerId, assetName)).thenReturn(mockAsset);
        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);

        // Act
        assetService.undoReserve(customerId, assetName, Order.OrderSide.SELL, requestPrice, requestSize);

        // Assert
        verify(assetRepository, times(1)).findByCustomerIdAndName(customerId, baseAssetName);
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
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(requestSize * requestPrice);
        tryAsset.setSize(requestSize * requestPrice);

        when(assetRepository.findByCustomerIdAndName(customerId, assetName)).thenReturn(mockAsset);
        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);

        // Act
        assetService.doApplyReserve(customerId, assetName, Order.OrderSide.BUY, requestPrice, requestSize);

        // Assert
        verify(assetRepository, times(1)).findByCustomerIdAndName(customerId, assetName);
        verify(assetRepository, times(1)).findByCustomerIdAndName(customerId, baseAssetName);
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

    @Test
    void test_doReserve_Throw_WhenInsufficientAssetOnSell() {
        // Arrange
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(0.0);
        mockAsset.setSize(0.0);

        Asset tryAsset = new Asset();
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(0.0);
        tryAsset.setSize(0.0);
        tryAsset.setCustomer(customer);

        when(assetRepository.findByCustomerIdAndName(customerId, assetName)).thenReturn(mockAsset);
        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);


        ApiException exception = assertThrows(ApiException.class, () ->
                assetService.doReserve(customerId, assetName, Order.OrderSide.SELL, requestPrice, requestSize));
        assertEquals(1003, exception.getCode());

    }

    @Test
    void test_doReserve_Throw_WhenInsufficientTryAssetOnBuy() {
        // Arrange
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(requestSize * requestPrice);
        mockAsset.setSize(requestSize * requestPrice);

        Asset tryAsset = new Asset();
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(0.0);
        tryAsset.setSize(0.0);
        tryAsset.setCustomer(customer);

        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);


        ApiException exception = assertThrows(ApiException.class, () ->
                assetService.doReserve(customerId, assetName, Order.OrderSide.BUY, requestPrice, requestSize));
        assertEquals(1003, exception.getCode());

    }

    @Test
    void test_doReserve_Throw_WhenNoTryAssetOnBuy() {
        // Arrange
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(requestSize * requestPrice);
        mockAsset.setSize(requestSize * requestPrice);

        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(null);

        ApiException exception = assertThrows(ApiException.class, () ->
                assetService.doReserve(customerId, assetName, Order.OrderSide.BUY, requestPrice, requestSize));
        assertEquals(1002, exception.getCode());

    }

    @Test
    void test_doReserve_Throw_WhenNoAssetOnSell() {
        Asset tryAsset = new Asset();
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(0.0);
        tryAsset.setSize(0.0);

        when(assetRepository.findByCustomerIdAndName(customerId, assetName)).thenReturn(null);
        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);

        ApiException exception = assertThrows(ApiException.class, () ->
                assetService.doReserve(customerId, assetName, Order.OrderSide.SELL, requestPrice, requestSize));
        assertEquals(1002, exception.getCode());

    }

    @Test
    void test_decreaseTryUsableSize_whenBuyAsset() {
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(requestSize * requestPrice);
        mockAsset.setSize(requestSize * requestPrice);

        Asset tryAsset = new Asset();
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(requestSize * requestPrice);
        tryAsset.setSize(requestSize * requestPrice);

        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);

        assetService.doReserve(customerId, assetName, Order.OrderSide.BUY, requestPrice, requestSize);

        verify(assetRepository).save(assetCaptor.capture());

        Asset capturedAsset = assetCaptor.getValue();
        assertThat(capturedAsset.getUsableSize()).isEqualTo(0);
        assertThat(capturedAsset.getName()).isEqualTo(baseAssetName);
        assertThat(capturedAsset.getSize()).isEqualTo(requestPrice * requestSize);

    }

    @Test
    void test_decreaseAssetUsableSize_whenSellAsset() {
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(requestSize * requestPrice);
        mockAsset.setSize(requestSize * requestPrice);

        Asset tryAsset = new Asset();
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(requestSize * requestPrice);
        tryAsset.setSize(requestSize * requestPrice);

        when(assetRepository.findByCustomerIdAndName(customerId, assetName)).thenReturn(mockAsset);
        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);

        assetService.doReserve(customerId, assetName, Order.OrderSide.SELL, requestPrice, requestSize);

        verify(assetRepository).save(assetCaptor.capture());

        Asset capturedAsset = assetCaptor.getValue();
        assertThat(capturedAsset.getUsableSize()).isEqualTo(0);
        assertThat(capturedAsset.getName()).isEqualTo(assetName);
        assertThat(capturedAsset.getSize()).isEqualTo(requestPrice * requestSize);

    }

    @Test
    void test_increaseTryAssetUsableSize_whenUndoReserveBuy() {
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(0.0);
        mockAsset.setSize(0.0);
        mockAsset.setCustomer(customer);

        Asset tryAsset = new Asset();
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(0.0);
        tryAsset.setSize(requestSize * requestPrice);
        tryAsset.setCustomer(customer);

        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);

        assetService.undoReserve(customerId, assetName, Order.OrderSide.BUY, requestPrice, requestSize);

        verify(assetRepository).save(assetCaptor.capture());
        Asset capturedAsset = assetCaptor.getValue();


        assertThat(capturedAsset.getUsableSize()).isEqualTo(requestSize * requestPrice);
        assertThat(capturedAsset.getSize()).isEqualTo(requestSize * requestPrice);
        assertThat(capturedAsset.getName()).isEqualTo(baseAssetName);

    }

    @Test
    void test_increaseAssetUsableSize_whenUndoReserveSell() {
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(0.0);
        mockAsset.setSize(requestSize * requestPrice);
        mockAsset.setCustomer(customer);

        Asset tryAsset = new Asset();
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(0.0);
        tryAsset.setSize(requestSize * requestPrice);
        tryAsset.setCustomer(customer);

        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);
        when(assetRepository.findByCustomerIdAndName(customerId, assetName)).thenReturn(mockAsset);

        assetService.undoReserve(customerId, assetName, Order.OrderSide.SELL, requestPrice, requestSize);

        verify(assetRepository).save(assetCaptor.capture());
        Asset capturedAsset = assetCaptor.getValue();

        assertThat(capturedAsset.getUsableSize()).isEqualTo(requestSize * requestPrice);
        assertThat(capturedAsset.getSize()).isEqualTo(requestSize * requestPrice);

    }

    @Test
    void test_increaseAssetSize_whenApplyReserveBuy() {
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(0.0);
        mockAsset.setSize(0.0);
        mockAsset.setCustomer(customer);

        Asset tryAsset = new Asset();
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(0.0);
        tryAsset.setSize(requestSize * requestPrice);
        tryAsset.setCustomer(customer);

        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);
        when(assetRepository.findByCustomerIdAndName(customerId, assetName)).thenReturn(mockAsset);

        assetService.doApplyReserve(customerId, assetName, Order.OrderSide.BUY, requestPrice, requestSize);

        verify(assetRepository, times(2)).save(assetCaptor.capture());

        List<Asset> capturedAssets = assetCaptor.getAllValues();
        Asset capturedTryAsset = capturedAssets.get(0);
        Asset capturedMockAsset = capturedAssets.get(1);

        assertThat(capturedTryAsset.getSize()).isEqualTo(0.0);
        assertThat(capturedTryAsset.getName()).isEqualTo(baseAssetName);
        assertThat(capturedMockAsset.getSize()).isEqualTo(requestSize * requestPrice);
        assertThat(capturedMockAsset.getUsableSize()).isEqualTo(requestSize * requestPrice);
        assertThat(capturedMockAsset.getName()).isEqualTo(assetName);
    }

    @Test
    void test_increaseAssetSize_whenApplyReserveSell() {
        Asset mockAsset = new Asset();
        mockAsset.setName(assetName);
        mockAsset.setUsableSize(0.0);
        mockAsset.setSize(requestSize * requestPrice);
        mockAsset.setCustomer(customer);

        Asset tryAsset = new Asset();
        tryAsset.setName(baseAssetName);
        tryAsset.setUsableSize(0.0);
        tryAsset.setSize(0.0);
        tryAsset.setCustomer(customer);

        when(assetRepository.findByCustomerIdAndName(customerId, baseAssetName)).thenReturn(tryAsset);
        when(assetRepository.findByCustomerIdAndName(customerId, assetName)).thenReturn(mockAsset);

        assetService.doApplyReserve(customerId, assetName, Order.OrderSide.SELL, requestPrice, requestSize);

        verify(assetRepository, times(2)).save(assetCaptor.capture());

        List<Asset> capturedAssets = assetCaptor.getAllValues();
        Asset capturedTryAsset = capturedAssets.get(0);
        Asset capturedMockAsset = capturedAssets.get(1);

        assertThat(capturedMockAsset.getSize()).isEqualTo(0.0);
        assertThat(capturedMockAsset.getName()).isEqualTo(assetName);
        assertThat(capturedTryAsset.getSize()).isEqualTo(requestSize * requestPrice);
        assertThat(capturedTryAsset.getUsableSize()).isEqualTo(requestSize * requestPrice);
        assertThat(capturedTryAsset.getName()).isEqualTo(baseAssetName);

    }
}