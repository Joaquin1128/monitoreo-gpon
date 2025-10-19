package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.monitoreo.monitoreo_gpon_back.snmp.dto.*;

@RestController
@RequestMapping("/api/snmp")
public class SnmpController {

    private final SnmpService snmpService;

    public SnmpController(SnmpService snmpService) {
        this.snmpService = snmpService;
    }

    @GetMapping("/olts/summary")
    public ResponseEntity<List<OltSummaryResponse>> getAllOltsSummary() {
        List<OltSummaryResponse> summaries = snmpService.getAllOltsSummary();
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/olts/{oltId}/detailed")
    public ResponseEntity<OltDetailedResponse> getOltDetailed(@PathVariable Long oltId) {
        try {
            OltDetailedResponse detailed = snmpService.getOltDetailed(oltId);
            return ResponseEntity.ok(detailed);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
