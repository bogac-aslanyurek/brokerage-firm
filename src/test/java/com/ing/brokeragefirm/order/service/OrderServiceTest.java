package com.ing.brokeragefirm.order.service;

import com.ing.brokeragefirm.asset.service.AssetService;
import com.ing.brokeragefirm.customer.domain.Customer;
import com.ing.brokeragefirm.exception.ApiException;
import com.ing.brokeragefirm.order.domain.Order;
import com.ing.brokeragefirm.order.domain.OrderRepository;
import com.ing.brokeragefirm.order.model.ListOrderRequest;
import com.ing.brokeragefirm.order.model.OrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith( MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetService assetService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_success() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(1L);
        orderRequest.setAssetName("AAPL");
        orderRequest.setOrderSide(Order.OrderSide.BUY);
        orderRequest.setPrice(150.00);
        orderRequest.setSize(10d);

        Order savedOrder = new Order();
        savedOrder.setId(1L);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        Order result = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(assetService).doReserve(
                eq(1L), eq("AAPL"), eq(Order.OrderSide.BUY), eq(150.00), eq(10d));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void listOrders_success() {
        // Arrange
        ListOrderRequest listOrderRequest = new ListOrderRequest(1L, null,null);
        List<Order> expectedOrders = List.of(new Order(), new Order());
        when(orderRepository.searchOrders(listOrderRequest)).thenReturn(expectedOrders);

        // Act
        List<Order> result = orderService.listOrders(listOrderRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository).searchOrders(listOrderRequest);
    }

    @Test
    void cancelOrder_success() {
        // Arrange
        Long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(Order.OrderStatus.PENDING);
        Customer customer = new Customer();
        customer.setId(1L);
        existingOrder.setCustomer(customer);
        existingOrder.setAssetName("AAPL");
        existingOrder.setOrderSide(Order.OrderSide.SELL);
        existingOrder.setPrice(150.00);
        existingOrder.setSize(10d);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        // Act
        assertDoesNotThrow(() -> orderService.cancelOrder(orderId));

        // Assert
        assertEquals(Order.OrderStatus.CANCELED, existingOrder.getStatus());
        verify(assetService).undoReserve(1L, "AAPL", Order.OrderSide.SELL,  (150.00), 10d);
        verify(orderRepository).save(existingOrder);
    }

    @Test
    void cancelOrder_nonPendingOrder_shouldThrowException() {
        // Arrange
        Long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(Order.OrderStatus.MATCHED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        // Act and Assert
        ApiException exception = assertThrows(ApiException.class, () -> orderService.cancelOrder(orderId));
        assertEquals(1004, exception.getCode());
        verify(orderRepository, never()).save(existingOrder);
    }

    @Test
    void cancelOrder_notFound_shouldThrowException() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act and Assert
        ApiException exception = assertThrows(ApiException.class, () -> orderService.cancelOrder(orderId));
        assertEquals(1005, exception.getCode());
    }

    @Test
    void matchOrder_success() {
        // Arrange
        Long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(Order.OrderStatus.PENDING);
        Customer customer = new Customer();
        customer.setId(1L);
        existingOrder.setCustomer(customer);
        existingOrder.setAssetName("AAPL");
        existingOrder.setOrderSide(Order.OrderSide.BUY);
        existingOrder.setPrice(150.00);
        existingOrder.setSize(10d);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

        // Act
        assertDoesNotThrow(() -> orderService.matchOrder(orderId));

        // Assert
        assertEquals(Order.OrderStatus.MATCHED, existingOrder.getStatus());
        verify(assetService).doApplyReserve(1L, "AAPL", Order.OrderSide.BUY, (150.00), 10d);
        verify(orderRepository).save(existingOrder);
    }

    @Test
    void matchOrder_nonPendingOrder_shouldThrowException() {
        // Arrange
        Long orderId = 1L;
        Order existingOrder = new Order();
        existingOrder.setId(orderId);
        existingOrder.setStatus(Order.OrderStatus.CANCELED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        // Act and Assert
        ApiException exception = assertThrows(ApiException.class, () -> orderService.matchOrder(orderId));
        assertEquals(1004, exception.getCode());
        verify(orderRepository, never()).save(existingOrder);
    }

    @Test
    void matchOrder_notFound_shouldThrowException() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act and Assert
        ApiException exception = assertThrows(ApiException.class, () -> orderService.matchOrder(orderId));
        assertEquals(1005, exception.getCode());
    }
}