package com.ing.brokeragefirm.order.service;

import com.ing.brokeragefirm.asset.service.AssetService;
import com.ing.brokeragefirm.order.api.OrderRequest;
import com.ing.brokeragefirm.order.domain.Order;
import com.ing.brokeragefirm.order.domain.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private OrderService orderService;


    @Test
    void testCreateOrder() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        orderRequest.setOrderSide(Order.OrderSide.BUY);
        orderRequest.setSize(100d);
        orderRequest.setPrice(50.5);

        Order savedOrder = new Order();
        savedOrder.setId(1L);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        Order result = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());

        // Verify
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();

        assertEquals(orderRequest.getCustomerId(), capturedOrder.getCustomerId());
        assertEquals(orderRequest.getOrderSide(), capturedOrder.getOrderSide());
        assertEquals(orderRequest.getSize(), capturedOrder.getSize());
        assertEquals(orderRequest.getPrice(), capturedOrder.getPrice());
        assertEquals(Order.OrderStatus.PENDING, capturedOrder.getStatus());
        assertNotNull(capturedOrder.getCreateDate());

        verify(assetService).checkAssetAvailability(orderRequest);
    }

    @Test
    void testListOrders() {
        // Arrange
        Long customerId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        List<Order> mockOrders = List.of(new Order(), new Order());
        when(orderRepository.searchOrders(customerId, startDate, endDate)).thenReturn(mockOrders);

        // Act
        List<Order> result = orderService.listOrders(customerId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository).searchOrders(customerId, startDate, endDate);
    }

    @Test
    void testCancelOrder_WithPendingStatus() {
        // Arrange
        Long orderId = 1L;
        Order pendingOrder = new Order();
        pendingOrder.setId(orderId);
        pendingOrder.setStatus(Order.OrderStatus.PENDING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);

        // Act
        orderService.cancelOrder(orderId);

        // Assert
        assertEquals(Order.OrderStatus.CANCELED, pendingOrder.getStatus());

        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(pendingOrder);
        verify(assetService).updateAssetAfterCancel(pendingOrder);
    }

    @Test
    void testCancelOrder_WithNonPendingStatus_ThrowsException() {
        // Arrange
        Long orderId = 1L;
        Order completedOrder = new Order();
        completedOrder.setId(orderId);
        completedOrder.setStatus(Order.OrderStatus.MATCHED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(completedOrder));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(orderId));
        assertEquals("Only PENDING orders can be canceled", exception.getMessage());

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
        verify(assetService, never()).updateAssetAfterCancel(any());
    }

    @Test
    void testCancelOrder_ThrowsWhenOrderNotFound() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(orderId));
        assertEquals("Order not found", exception.getMessage());

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
        verify(assetService, never()).updateAssetAfterCancel(any());
    }
}