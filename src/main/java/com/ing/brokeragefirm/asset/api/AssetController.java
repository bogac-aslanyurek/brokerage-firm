package com.ing.brokeragefirm.asset.api;


import com.ing.brokeragefirm.asset.domain.Asset;
import com.ing.brokeragefirm.asset.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/list")
    @Transactional
    public ResponseEntity<List<Asset>> listAssets(@RequestParam("customerId") Long customerId) {
        return ResponseEntity.ok(assetService.listAssets(customerId));
    }

    @PostMapping("/create")
    public ResponseEntity<Asset> createAsset(@RequestBody CreateAssetRequest request) {
        return ResponseEntity.ok(assetService.createAsset(request.customerId(), request.assetName(), request.assetSize()));

    }
}
