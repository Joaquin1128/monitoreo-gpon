package com.example.monitoreo.monitoreo_gpon_back.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
import com.example.monitoreo.monitoreo_gpon_back.repository.OltRepository;

@RestController
@RequestMapping("/api/olts")
public class OltController {

    private final OltRepository oltRepository;

    public OltController(OltRepository oltRepository) {
        this.oltRepository = oltRepository;
    }

    @GetMapping("/hub/{hubId}")
    public List<Olt> getByHub(@PathVariable Long hubId) {
        return oltRepository.findByHubId(hubId);
    }

    @GetMapping("/{oltId}")
    public ResponseEntity<Olt> getOlt(@PathVariable Long oltId) {
        return oltRepository.findById(oltId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
