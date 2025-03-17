package com.ing.brokeragefirm.order.api;

import javax.validation.constraints.NotNull;

public record CancelOrderRequest(@NotNull Long id, @NotNull Long customerId) {
}
