package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.snmp4j.smi.Variable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.monitoreo.monitoreo_gpon_back.model.*;
import com.example.monitoreo.monitoreo_gpon_back.repository.*;
import com.example.monitoreo.monitoreo_gpon_back.snmp.dto.ProbeRequest;
import com.example.monitoreo.monitoreo_gpon_back.snmp.dto.ProbeResult;

@RestController
@RequestMapping("/api/snmp")
public class SnmpController {

    private final OltRepository oltRepository;
    private final OntRepository ontRepository;
    private final SnmpService snmpService;
    private final SnmpOidResolver resolver;
    private final OidDefinitionRepository oidDefinitionRepository;
    private final DeviceTypeRepository deviceTypeRepository;

    public SnmpController(OltRepository oltRepository, OntRepository ontRepository, SnmpService snmpService, SnmpOidResolver resolver,
        OidDefinitionRepository oidDefinitionRepository, DeviceTypeRepository deviceTypeRepository) {
        this.oltRepository = oltRepository;
        this.ontRepository = ontRepository;
        this.snmpService = snmpService;
        this.resolver = resolver;
        this.oidDefinitionRepository = oidDefinitionRepository;
        this.deviceTypeRepository = deviceTypeRepository;
    }

    private List<OidDefinition> fetchOidDefinitions(Vendor vendor, DeviceType deviceType) {
        if (vendor == null || deviceType == null) return Collections.emptyList();
        return oidDefinitionRepository.findByVendorAndDeviceType(vendor, deviceType);
    }

    private List<ProbeResult> probeDevice(List<OidDefinition> defs, Vendor vendor, DeviceType deviceType,
        String ipAddress, int port, String community, int timeoutMs, Map<String, Object> ctx) {
        List<ProbeResult> results = new ArrayList<>();
        List<String> requestedOids = new ArrayList<>();
        List<SnmpOidResolver.ResolvedOid> resolved = new ArrayList<>();
        List<String> metrics = new ArrayList<>();

        for (OidDefinition d : defs) {
            metrics.add(d.getMetricKey());
            try {
                SnmpOidResolver.ResolvedOid r = resolver.resolve(vendor, deviceType, d.getMetricKey());
                if (r == null) {
                    results.add(new ProbeResult(d.getMetricKey(), null, "OID_NOT_FOUND"));
                    continue;
                }
                resolved.add(r);
                requestedOids.add(r.getOid());
            } catch (Exception e) {
                results.add(new ProbeResult(d.getMetricKey(), null, "ERROR:" + e.getMessage()));
            }
        }

        if (!requestedOids.isEmpty()) {
            try {
                Map<String, Variable> res = snmpService.getByOids(ipAddress, port, community, timeoutMs, requestedOids);
                for (int i = 0; i < resolved.size(); i++) {
                    SnmpOidResolver.ResolvedOid r = resolved.get(i);
                    String metric = i < metrics.size() ? metrics.get(i) : r.getOid();
                    Variable var = res.get(r.getOid());
                    String raw = var == null ? null : var.toString();
                    Object parsed = SnmpUtils.parseVariable(var, r.getValueType());
                    String vt = r.getValueType() == null ? null : r.getValueType().name();
                    results.add(new ProbeResult(metric, raw, parsed, vt, r.getSource()));
                }
            } catch (Exception e) {
                for (String metric : metrics) {
                    results.add(new ProbeResult(metric, null, "ERROR:" + e.getMessage()));
                }
            }
        }
        return results;
    }

    @PostMapping("/olts/{oltId}/probe")
    public ResponseEntity<?> probeOlt(@PathVariable Long oltId, @RequestBody(required = false) ProbeRequest req) {
        Olt olt = oltRepository.findById(oltId).orElse(null);
        if (olt == null) return ResponseEntity.notFound().build();

        Vendor vendor = olt.getVendor();
        DeviceType deviceType = olt.getDeviceType();
        List<OidDefinition> defs = fetchOidDefinitions(vendor, deviceType);

        if (defs.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No OID definitions for this OLT vendor/type"));
        }

        Map<String, Object> ctx = (req == null || req.getContext() == null) ? Map.of() : req.getContext();
        List<ProbeResult> results = probeDevice(defs, vendor, deviceType, olt.getIpAddress(),
            olt.getSnmpPort(), olt.getSnmpCommunity(), olt.getSnmpTimeoutMs(), ctx);

        return ResponseEntity.ok(results);
    }

    @PostMapping("/onts/{ontId}/probe")
    public ResponseEntity<?> probeOnt(@PathVariable Long ontId, @RequestBody(required = false) ProbeRequest req) {
        Ont ont = ontRepository.findById(ontId).orElse(null);
        if (ont == null) return ResponseEntity.notFound().build();

        Olt olt = ont.getOlt();
        if (olt == null) return ResponseEntity.badRequest().body(Map.of("error", "ONT has no parent OLT"));

        Vendor vendor = ont.getVendor() != null ? ont.getVendor() : olt.getVendor();
        DeviceType deviceType = ont.getDeviceType() != null ? ont.getDeviceType() : deviceTypeRepository.findByName("ONT").orElse(null);

        List<OidDefinition> defs = fetchOidDefinitions(vendor, deviceType);
        if (defs.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No OID definitions for this ONT vendor/type"));
        }

        Map<String, Object> ctx = (req == null || req.getContext() == null) ? Map.of() : req.getContext();
        List<ProbeResult> results = probeDevice(defs, vendor, deviceType, olt.getIpAddress(),
            olt.getSnmpPort(), olt.getSnmpCommunity(), olt.getSnmpTimeoutMs(), ctx);

        return ResponseEntity.ok(results);
    }
}
