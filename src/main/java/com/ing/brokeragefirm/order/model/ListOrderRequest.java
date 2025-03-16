package com.ing.brokeragefirm.order.model;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public record ListOrderRequest(@NotNull Long customerId, LocalDate startDate, LocalDate endDate) {
}