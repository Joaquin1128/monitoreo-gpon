package com.example.monitoreo.monitoreo_gpon_back.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.monitoreo.monitoreo_gpon_back.model.Hub;
import com.example.monitoreo.monitoreo_gpon_back.repository.*;

@RestController
@RequestMapping("/api/hubs")
public class HubController {

    private final HubRepository hubRepository;
    private final OltRepository oltRepository;

    public HubController(HubRepository hubRepository, OltRepository oltRepository) {
        this.hubRepository = hubRepository;
        this.oltRepository = oltRepository;
    }

    @GetMapping
    public List<Hub> listHubs() {
        return hubRepository.findAll();
    }

    @GetMapping("/{hubId}")
    public ResponseEntity<Hub> getHub(@PathVariable Long hubId) {
        return hubRepository.findById(hubId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{hubId}/olts")
    public ResponseEntity<?> listOltsByHub(@PathVariable Long hubId) {
        if (!hubRepository.existsById(hubId)) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(oltRepository.findByHubId(hubId));
    }
}
