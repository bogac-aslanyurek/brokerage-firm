package com.ing.brokeragefirm.order.api;

import com.ing.brokeragefirm.customer.service.CustomerService;
import com.ing.brokeragefirm.exception.ApiException;
import com.ing.brokeragefirm.order.domain.Order;
import com.ing.brokeragefirm.order.model.ListOrderRequest;
import com.ing.brokeragefirm.order.model.OrderRequest;
import com.ing.brokeragefirm.order.service.OrderService;
import com.ing.brokeragefirm.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final SecurityService securityService;

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        final Long customerId = orderRequest.getCustomerId();
        final Boolean isAuthorized = securityService.aclCheck(String.valueOf(customerId));
        if(!isAuthorized) {
            throw new ApiException(1007, "You are not authorized");
        }

        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @PostMapping("/list")
    public ResponseEntity<List<Order>> listOrders(
            @RequestBody ListOrderRequest listOrderRequest) {

        final Long customerId = listOrderRequest.customerId();
        final Boolean isAuthorized = securityService.aclCheck(String.valueOf(customerId));
        if(!isAuthorized) {
            throw new ApiException(1007, "You are not authorized");
        }

        return ResponseEntity.ok(orderService.listOrders(listOrderRequest));
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestBody CancelOrderRequest request) {

        final Boolean isAuthorized = securityService.aclCheck(String.valueOf(request.customerId()));
        if(!isAuthorized) {
            throw new ApiException(1007, "You are not authorized");
        }

        orderService.cancelOrder(request.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/match")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> matchOrder(@PathVariable Long id) {
        orderService.matchOrder(id);
        return ResponseEntity.noContent().build();
    }
}
