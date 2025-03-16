package com.ing.brokeragefirm.order.model;

import com.ing.brokeragefirm.order.domain.Order;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderRequest {

    @NotNull
    private Long customerId;
    @NotNull
    private Double price;
    @NotNull
    private Order.OrderSide orderSide;
    @NotNull
    private Double size;
    @NotNull
    private String assetName;

}
