package com.ing.brokeragefirm.order.api;

import com.ing.brokeragefirm.order.domain.Order;
import lombok.Data;

@Data
public class OrderRequest {

    private Long customerId;
    private Double price;
    private Order.OrderSide orderSide;
    private Double size;
    private String assetName;

}
