package com.ing.brokeragefirm.asset.api;


import com.ing.brokeragefirm.asset.domain.Asset;
import com.ing.brokeragefirm.asset.service.AssetService;
import com.ing.brokeragefirm.exception.ApiException;
import com.ing.brokeragefirm.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final SecurityService securityService;

    @GetMapping("/list")
    @Transactional
    public ResponseEntity<List<Asset>> listAssets(@RequestParam("customerId") Long customerId) {
        final Boolean isAuthorized = securityService.aclCheck(String.valueOf(customerId));
        if(!isAuthorized) {
            throw new ApiException(1007, "You are not authorized");
        }

        return ResponseEntity.ok(assetService.listAssets(customerId));
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Asset> createAsset(@RequestBody CreateAssetRequest request) {
        return ResponseEntity.ok(assetService.createAsset(request.customerId(), request.assetName(), request.assetSize()));

    }
}
