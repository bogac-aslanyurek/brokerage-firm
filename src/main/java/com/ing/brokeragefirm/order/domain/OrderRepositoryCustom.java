package com.ing.brokeragefirm.order.domain;

import com.ing.brokeragefirm.order.model.ListOrderRequest;

import java.util.List;

public interface OrderRepositoryCustom {

     List<Order> searchOrders(ListOrderRequest request);

}
