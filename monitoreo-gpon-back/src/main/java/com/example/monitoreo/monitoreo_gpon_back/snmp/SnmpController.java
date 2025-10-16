package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.snmp4j.smi.Variable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.monitoreo.monitoreo_gpon_back.model.DeviceType;
import com.example.monitoreo.monitoreo_gpon_back.model.OidDefinition;
import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
import com.example.monitoreo.monitoreo_gpon_back.model.Ont;
import com.example.monitoreo.monitoreo_gpon_back.model.Vendor;
import com.example.monitoreo.monitoreo_gpon_back.repository.DeviceTypeRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.OidDefinitionRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.OltRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.OntRepository;
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

    public SnmpController(OltRepository oltRepository, OntRepository ontRepository, SnmpService snmpService, SnmpOidResolver resolver, com.example.monitoreo.monitoreo_gpon_back.repository.OidDefinitionRepository oidDefinitionRepository, com.example.monitoreo.monitoreo_gpon_back.repository.DeviceTypeRepository deviceTypeRepository) {
        this.oltRepository = oltRepository;
        this.ontRepository = ontRepository;
        this.snmpService = snmpService;
        this.resolver = resolver;
        this.oidDefinitionRepository = oidDefinitionRepository;
        this.deviceTypeRepository = deviceTypeRepository;
    }

    private List<OidDefinition> fetchOidDefinitionsByDeviceVendorAndType(com.example.monitoreo.monitoreo_gpon_back.model.Vendor vendorEntity, com.example.monitoreo.monitoreo_gpon_back.model.DeviceType deviceType) {
        if (vendorEntity == null || deviceType == null) {
            return Collections.emptyList();
        }

        List<OidDefinition> defs = oidDefinitionRepository.findByVendorAndDeviceType(vendorEntity, deviceType);
        if (defs == null) {
            return Collections.emptyList();
        }

        return defs;
    }

    @PostMapping("/olts/{oltId}/probe")
    public ResponseEntity<?> probeOlt(@PathVariable Long oltId, @RequestBody(required = false) ProbeRequest req) {
        Olt olt = oltRepository.findById(oltId).orElse(null);

        if (olt == null) {
            return ResponseEntity.notFound().build();
        }

        List<ProbeResult> results = new ArrayList<>();
        Map<String, Object> ctx = (req == null || req.getContext() == null) ? Map.of() : req.getContext();

        Vendor vendorEntity = olt.getVendor();
        DeviceType deviceType = olt.getDeviceType();
        List<OidDefinition> defs = fetchOidDefinitionsByDeviceVendorAndType(vendorEntity, deviceType);
        if (defs.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error","No OID definitions for this OLT vendor/type"));
        }

        List<String> requestedOids = new ArrayList<>();
        List<SnmpOidResolver.ResolvedOid> resolved = new ArrayList<>();
        List<String> metrics = new ArrayList<>();
        for (OidDefinition d : defs) {
            metrics.add(d.getMetricKey());
            try {
                SnmpOidResolver.ResolvedOid r = resolver.resolve("OLT", oltId, d.getMetricKey(), ctx);
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
                Map<String, org.snmp4j.smi.Variable> res = snmpService.getByOids(olt.getIpAddress(), olt.getSnmpPort(), olt.getSnmpCommunity(), olt.getSnmpTimeoutMs(), requestedOids);
                for (int i = 0; i < resolved.size(); i++) {
                    SnmpOidResolver.ResolvedOid r = resolved.get(i);
                    String metric = i < metrics.size() ? metrics.get(i) : resolved.get(i).getOid();
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

        return ResponseEntity.ok(results);
    }

    @PostMapping("/onts/{ontId}/probe")
    public ResponseEntity<?> probeOnt(@PathVariable Long ontId, @RequestBody(required = false) ProbeRequest req) {
        Ont ont = ontRepository.findById(ontId).orElse(null);
        if (ont == null) return ResponseEntity.notFound().build();

        Olt olt = ont.getOlt();
        if (olt == null) return ResponseEntity.badRequest().body(Map.of("error","ONT has no parent OLT"));

        List<ProbeResult> results = new ArrayList<>();
        Map<String, Object> ctx = (req == null || req.getContext() == null) ? Map.of() : req.getContext();

        List<String> requestedOids = new ArrayList<>();
        List<SnmpOidResolver.ResolvedOid> resolved2 = new ArrayList<>();
        
        Vendor vendorEntity = ont.getVendor() != null ? ont.getVendor() : olt.getVendor();
        DeviceType deviceType = ont.getDeviceType() != null ? ont.getDeviceType() : deviceTypeRepository.findByName("ONT").orElse(null);
        List<OidDefinition> defs = fetchOidDefinitionsByDeviceVendorAndType(vendorEntity, deviceType);
        if (defs.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error","No OID definitions for this ONT vendor/type"));
        }

        List<String> metrics = new ArrayList<>();
        for (OidDefinition d : defs) {
            metrics.add(d.getMetricKey());
            try {
                SnmpOidResolver.ResolvedOid r = resolver.resolve("ONT", ontId, d.getMetricKey(), ctx);
                if (r == null) {
                    results.add(new ProbeResult(d.getMetricKey(), null, "OID_NOT_FOUND"));
                    continue;
                }

                resolved2.add(r);
                requestedOids.add(r.getOid());
            } catch (Exception e) {
                results.add(new ProbeResult(d.getMetricKey(), null, "ERROR:" + e.getMessage()));
            }
        }

        if (!requestedOids.isEmpty()) {
            try {
                Map<String, Variable> res = snmpService.getByOids(olt.getIpAddress(), olt.getSnmpPort(), olt.getSnmpCommunity(), olt.getSnmpTimeoutMs(), requestedOids);
                for (int i = 0; i < resolved2.size(); i++) {
                    SnmpOidResolver.ResolvedOid r = resolved2.get(i);
                    String metric = i < metrics.size() ? metrics.get(i) : resolved2.get(i).getOid();
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

        return ResponseEntity.ok(results);
    }
}
