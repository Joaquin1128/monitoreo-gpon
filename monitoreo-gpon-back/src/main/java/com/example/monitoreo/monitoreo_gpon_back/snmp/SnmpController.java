package com.example.monitoreo.monitoreo_gpon_back.snmp;

import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
import com.example.monitoreo.monitoreo_gpon_back.repository.OltRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/snmp")
public class SnmpController {

    private final OltRepository oltRepository;
    private final SnmpService snmpService;

    public SnmpController(OltRepository oltRepository, SnmpService snmpService) {
        this.oltRepository = oltRepository;
        this.snmpService = snmpService;
    }

    @GetMapping("/olts/{oltId}/probe")
    public ResponseEntity<?> probe(@PathVariable Long oltId) {
        Olt olt = oltRepository.findById(oltId).orElse(null);

        if (olt == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Map<String, String> res = snmpService.probe(olt.getIpAddress(), olt.getSnmpPort(), olt.getSnmpCommunity(), olt.getSnmpTimeoutMs());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            // Return diagnostic info for easier debugging
            Map<String, Object> diag = Map.of(
                    "error", e.getMessage(),
                    "host", olt.getIpAddress(),
                    "port", olt.getSnmpPort(),
                    "community", olt.getSnmpCommunity(),
                    "timeoutMs", olt.getSnmpTimeoutMs()
            );
            return ResponseEntity.status(502).body(diag);
        }
    }
}
