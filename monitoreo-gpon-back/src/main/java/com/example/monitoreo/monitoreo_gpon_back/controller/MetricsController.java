package com.example.monitoreo.monitoreo_gpon_back.controller;

import com.example.monitoreo.monitoreo_gpon_back.service.MetricService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricService metricService;

    public MetricsController(MetricService metricService) {
        this.metricService = metricService;
    }

    @GetMapping("/olts/{oltId}/latest")
    public ResponseEntity<?> latest(@PathVariable Long oltId) {
        return ResponseEntity.ok(metricService.findLatestByOlt(oltId));
    }
}
