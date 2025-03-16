package com.ing.brokeragefirm.asset.api;


import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public record CreateAssetRequest(@NotNull Long customerId, @NotNull @Size(min = 1, max = 10) String assetName,
                                 @NotNull @Positive @Max(1000) Double assetSize) {
}
