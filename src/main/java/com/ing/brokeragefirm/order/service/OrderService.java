package com.ing.brokeragefirm.order.service;

import com.ing.brokeragefirm.asset.service.AssetService;
import com.ing.brokeragefirm.order.api.OrderRequest;
import com.ing.brokeragefirm.order.domain.Order;
import com.ing.brokeragefirm.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetService assetService;

    public Order createOrder(OrderRequest orderRequest) {
        // Check if enough usable size is available for BUY/SELL order
        assetService.checkAssetAvailability(orderRequest);

        // Create the order with PENDING status
        Order order = new Order();
        order.setCustomerId(orderRequest.getCustomerId());
        order.setOrderSide(orderRequest.getOrderSide());
        order.setSize(orderRequest.getSize());
        order.setPrice(orderRequest.getPrice());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public List<Order> listOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.searchOrders(customerId, startDate, endDate);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (Order.OrderStatus.PENDING.equals(order.getStatus())) {
            order.setStatus(Order.OrderStatus.CANCELED);
            orderRepository.save(order);
            assetService.updateAssetAfterCancel(order);
        } else {
            throw new IllegalArgumentException("Only PENDING orders can be canceled");
        }
    }
}