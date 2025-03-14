package com.ing.brokeragefirm.order.api;

import java.time.LocalDateTime;

public record ListOrderRequest(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
}