package com.ing.brokeragefirm.order.api;

import java.time.LocalDate;

public record ListOrderRequest(Long customerId, LocalDate startDate, LocalDate endDate) {
}