package com.ing.brokeragefirm.order.api;

import com.ing.brokeragefirm.order.domain.Order;
import com.ing.brokeragefirm.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.ok(orderService.listOrders(listOrderRequest.customerId(), listOrderRequest.startDate(), listOrderRequest.endDate()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
