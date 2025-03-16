package com.ing.brokeragefirm.customer.api;

import javax.validation.constraints.NotNull;

public record CreateCustomerRequest (@NotNull String name, @NotNull String username, @NotNull String password) {
}
