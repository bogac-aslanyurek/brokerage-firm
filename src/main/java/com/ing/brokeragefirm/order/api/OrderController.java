package com.ing.brokeragefirm.order.api;

import com.ing.brokeragefirm.order.domain.Order;
import com.ing.brokeragefirm.order.model.ListOrderRequest;
import com.ing.brokeragefirm.order.model.OrderRequest;
import com.ing.brokeragefirm.order.service.OrderService;
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

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @PostMapping("/list")
    public ResponseEntity<List<Order>> listOrders(
                                                  @RequestBody ListOrderRequest listOrderRequest) {
        return ResponseEntity.ok(orderService.listOrders(listOrderRequest));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/match")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void>  matchOrder(@PathVariable Long id) {
        orderService.matchOrder(id);
        return ResponseEntity.noContent().build();
    }
}
