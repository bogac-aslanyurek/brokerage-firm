package com.ing.brokeragefirm.order.domain;


import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepositoryCustom {

     List<Order> searchOrders(Long customerId, LocalDateTime startDate, LocalDateTime endDate);

}
