package com.example.monitoreo.monitoreo_gpon_back.snmp;

import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
import com.example.monitoreo.monitoreo_gpon_back.model.Ont;
import com.example.monitoreo.monitoreo_gpon_back.repository.OltRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.OntRepository;
import com.example.monitoreo.monitoreo_gpon_back.snmp.dto.ProbeRequest;
import com.example.monitoreo.monitoreo_gpon_back.snmp.dto.ProbeResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.monitoreo.monitoreo_gpon_back.model.SnmpProfile;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/snmp")
public class SnmpController {

    private final OltRepository oltRepository;
    private final OntRepository ontRepository;
    private final SnmpService snmpService;
    private final SnmpOidResolver resolver;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SnmpController(OltRepository oltRepository, OntRepository ontRepository, SnmpService snmpService, SnmpOidResolver resolver) {
        this.oltRepository = oltRepository;
        this.ontRepository = ontRepository;
        this.snmpService = snmpService;
        this.resolver = resolver;
    }

    private List<String> extractMetricListFromProfile(SnmpProfile profile, String deviceType) {
        if (profile == null || profile.getDefaultMetricSets() == null) return Collections.emptyList();
        try {
            Map<String, List<String>> map = objectMapper.readValue(profile.getDefaultMetricSets(), new TypeReference<Map<String, List<String>>>(){});
            List<String> list = map.get(deviceType);
            if (list == null) {
                return Collections.emptyList();
            }

            return list.stream().filter(s -> s != null && !s.isBlank()).map(String::trim).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @PostMapping("/olts/{oltId}/probe")
    public ResponseEntity<?> probeOlt(@PathVariable Long oltId, @RequestBody ProbeRequest req) {
        Olt olt = oltRepository.findById(oltId).orElse(null);

        if (olt == null) return ResponseEntity.notFound().build();

        List<ProbeResult> results = new ArrayList<>();
        Map<String, Object> ctx = req.getContext() == null ? Map.of() : req.getContext();

        SnmpProfile profile = olt.getSnmpProfile();
        List<String> metrics = extractMetricListFromProfile(profile, "OLT");
        if (metrics == null || metrics.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error","No default metrics configured for this OLT/profile"));
        }

        List<String> requestedOids = new ArrayList<>();
        List<SnmpOidResolver.ResolvedOid> resolved = new ArrayList<>();
        for (String metric : metrics) {
            try {
                SnmpOidResolver.ResolvedOid r = resolver.resolve("OLT", oltId, metric, ctx);
                resolved.add(r);
                requestedOids.add(r.getOid());
            } catch (Exception e) {
                results.add(new ProbeResult(metric, null, "ERROR:" + e.getMessage()));
            }
        }

            if (!requestedOids.isEmpty()) {
            try {
                Map<String, String> res = snmpService.getByOids(olt.getIpAddress(), olt.getSnmpPort(), olt.getSnmpCommunity(), olt.getSnmpTimeoutMs(), requestedOids);
                    for (int i = 0; i < resolved.size(); i++) {
                        SnmpOidResolver.ResolvedOid r = resolved.get(i);
                        String metric = metrics.get(i);
                        String value = res.getOrDefault(metric, res.get(r.getOid()));
                        results.add(new ProbeResult(metric, value, r.getSource()));
                    }
            } catch (Exception e) {
                    for (String metric : metrics) results.add(new ProbeResult(metric, null, "ERROR:" + e.getMessage()));
            }
        }

        return ResponseEntity.ok(results);
    }

    @PostMapping("/onts/{ontId}/probe")
    public ResponseEntity<?> probeOnt(@PathVariable Long ontId, @RequestBody ProbeRequest req) {
        Ont ont = ontRepository.findById(ontId).orElse(null);
        if (ont == null) return ResponseEntity.notFound().build();

        Olt olt = ont.getOlt();
        if (olt == null) return ResponseEntity.badRequest().body(Map.of("error","ONT has no parent OLT"));

        List<ProbeResult> results = new ArrayList<>();
        Map<String, Object> ctx = req.getContext() == null ? Map.of() : req.getContext();
        if (!ctx.containsKey("ont_index") && ont.getOnuIndex() != null) {
            Map<String, Object> m = new HashMap<>(ctx);
            m.put("ont_index", ont.getOnuIndex());
            ctx = m;
        }

        List<String> requestedOids2 = new ArrayList<>();
        List<SnmpOidResolver.ResolvedOid> resolved2 = new ArrayList<>();
        SnmpProfile profile2 = olt.getSnmpProfile();
        List<String> metrics = extractMetricListFromProfile(profile2, "ONT");
        if (metrics == null || metrics.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error","No default metrics configured for this ONT/profile"));
        }
        for (String metric : metrics) {
            try {
                SnmpOidResolver.ResolvedOid r = resolver.resolve("ONT", ontId, metric, ctx);
                resolved2.add(r);
                requestedOids2.add(r.getOid());
            } catch (Exception e) {
                results.add(new ProbeResult(metric, null, "ERROR:" + e.getMessage()));
            }
        }

        if (!requestedOids2.isEmpty()) {
            try {
                Map<String, String> res = snmpService.getByOids(olt.getIpAddress(), olt.getSnmpPort(), olt.getSnmpCommunity(), olt.getSnmpTimeoutMs(), requestedOids2);
                for (int i = 0; i < resolved2.size(); i++) {
                    SnmpOidResolver.ResolvedOid r = resolved2.get(i);
                    String metric = metrics.get(i);
                    String value = res.getOrDefault(metric, res.get(r.getOid()));
                    results.add(new ProbeResult(metric, value, r.getSource()));
                }
            } catch (Exception e) {
                for (String metric : metrics) results.add(new ProbeResult(metric, null, "ERROR:" + e.getMessage()));
            }
        }

        return ResponseEntity.ok(results);
    }
}
