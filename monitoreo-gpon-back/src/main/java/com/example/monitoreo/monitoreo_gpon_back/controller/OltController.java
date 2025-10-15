package com.example.monitoreo.monitoreo_gpon_back.controller;

import com.example.monitoreo.monitoreo_gpon_back.dto.OltCreate;
import com.example.monitoreo.monitoreo_gpon_back.model.Hub;
import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
import com.example.monitoreo.monitoreo_gpon_back.repository.HubRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.OltRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/olts")
public class OltController {

    private final OltRepository oltRepository;
    private final HubRepository hubRepository;

    public OltController(OltRepository oltRepository, HubRepository hubRepository) {
        this.oltRepository = oltRepository;
        this.hubRepository = hubRepository;
    }

    @GetMapping("/hub/{hubId}")
    public List<Olt> getByHub(@PathVariable Long hubId) {
        return oltRepository.findByHubId(hubId);
    }

    @PostMapping
    public ResponseEntity<Olt> create(@RequestBody OltCreate dto) {
        Hub hub = hubRepository.findById(dto.getHubId()).orElse(null);
        if (hub == null) {
            return ResponseEntity.badRequest().build();
        }
        
        com.example.monitoreo.monitoreo_gpon_back.model.Vendor v = com.example.monitoreo.monitoreo_gpon_back.model.Vendor.UNKNOWN;
        try {
            if (dto.getVendor() != null) {
                v = com.example.monitoreo.monitoreo_gpon_back.model.Vendor.valueOf(dto.getVendor().toUpperCase());
            }
        } catch (Exception ignored) {
        }

        Olt olt = new Olt(dto.getName(), dto.getIpAddress(), dto.getModel(), v, dto.getLocation(), "UNKNOWN");
        olt.setHub(hub);
        Olt saved = oltRepository.save(olt);

        return ResponseEntity.created(URI.create("/api/olts/" + saved.getId())).body(saved);
    }

    @GetMapping("/{oltId}")
    public ResponseEntity<Olt> getOlt(@PathVariable Long oltId) {
        return oltRepository.findById(oltId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{oltId}")
    public ResponseEntity<Olt> update(@PathVariable Long oltId, @RequestBody OltCreate dto) {
        return oltRepository.findById(oltId).map(existing -> {
            existing.setName(dto.getName());
            existing.setIpAddress(dto.getIpAddress());
            existing.setLocation(dto.getLocation());
            existing.setModel(dto.getModel());
            try {
                existing.setVendor(com.example.monitoreo.monitoreo_gpon_back.model.Vendor.valueOf(dto.getVendor().toUpperCase()));
            } catch (Exception ex) {
                existing.setVendor(com.example.monitoreo.monitoreo_gpon_back.model.Vendor.UNKNOWN);
            }

            oltRepository.save(existing);
            return ResponseEntity.ok(existing);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{oltId}")
    public ResponseEntity<Void> delete(@PathVariable Long oltId) {
        if (!oltRepository.existsById(oltId)) {
            return ResponseEntity.notFound().build();
        }
        
        oltRepository.deleteById(oltId);
        return ResponseEntity.noContent().build();
    }
}
