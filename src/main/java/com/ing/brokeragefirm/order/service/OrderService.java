package com.ing.brokeragefirm.order.service;

import com.ing.brokeragefirm.asset.service.AssetService;
import com.ing.brokeragefirm.customer.domain.Customer;
import com.ing.brokeragefirm.exception.ApiException;
import com.ing.brokeragefirm.order.domain.Order;
import com.ing.brokeragefirm.order.domain.OrderRepository;
import com.ing.brokeragefirm.order.model.ListOrderRequest;
import com.ing.brokeragefirm.order.model.OrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetService assetService;

    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        // Check if enough usable size is available for BUY/SELL order
        assetService.doReserve(orderRequest.getCustomerId(), orderRequest.getAssetName(), orderRequest.getOrderSide(), orderRequest.getPrice(), orderRequest.getSize());

        // Create the order with PENDING status
        Order order = new Order();
        Customer customer = new Customer();
        customer.setId(orderRequest.getCustomerId());

        order.setCustomer(customer);
        order.setOrderSide(orderRequest.getOrderSide());
        order.setSize(orderRequest.getSize());
        order.setPrice(orderRequest.getPrice());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreateDate(LocalDateTime.now());
        order.setAssetName(orderRequest.getAssetName());

        return orderRepository.save(order);
    }

    public List<Order> listOrders(ListOrderRequest request) {
        return orderRepository.searchOrders(request);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order orderRequest = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException(1005, "Order not found"));

        if (Order.OrderStatus.PENDING.equals(orderRequest.getStatus())) {
            orderRequest.setStatus(Order.OrderStatus.CANCELED);
            orderRepository.save(orderRequest);

            assetService.undoReserve(orderRequest.getCustomer().getId(), orderRequest.getAssetName(), orderRequest.getOrderSide(), orderRequest.getPrice(), orderRequest.getSize());
        } else {
            throw new ApiException(1004, "Only PENDING orders can be canceled");
        }
    }

    @Transactional
    @PreAuthorize( "hasAuthority('ADMIN')")
    public void matchOrder(Long id) {
        Order orderRequest = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException(1005, "Order not found"));

        if (Order.OrderStatus.PENDING.equals(orderRequest.getStatus())) {
            orderRequest.setStatus(Order.OrderStatus.MATCHED);
            orderRepository.save(orderRequest);

            assetService.doApplyReserve(orderRequest.getCustomer().getId(), orderRequest.getAssetName(), orderRequest.getOrderSide(), orderRequest.getPrice(), orderRequest.getSize());
        } else {
            throw new ApiException(1004, "Only PENDING orders can be matched");

        }
    }
}