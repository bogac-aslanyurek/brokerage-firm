package com.ing.brokeragefirm.asset.api;


import com.ing.brokeragefirm.asset.domain.Asset;
import com.ing.brokeragefirm.asset.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/list")
    public ResponseEntity<List<Asset>> listAssets(@RequestParam("customerId") Long customerId) {
        return ResponseEntity.ok(assetService.listAssets(customerId));
    }
}
