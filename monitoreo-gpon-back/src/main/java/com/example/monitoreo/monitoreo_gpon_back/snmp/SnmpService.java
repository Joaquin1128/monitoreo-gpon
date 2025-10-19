package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.monitoreo.monitoreo_gpon_back.model.*;
import com.example.monitoreo.monitoreo_gpon_back.repository.*;
import com.example.monitoreo.monitoreo_gpon_back.snmp.dto.*;

import jakarta.annotation.PostConstruct;

@Service
public class SnmpService {

    private static final Logger log = LoggerFactory.getLogger(SnmpService.class);

    private final OltRepository oltRepository;
    private final Snmp4jService snmp4jService;
    private final SnmpOidResolver resolver;
    private final OidDefinitionRepository oidDefinitionRepository;

    public SnmpService(OltRepository oltRepository, Snmp4jService snmp4jService,
        SnmpOidResolver resolver, OidDefinitionRepository oidDefinitionRepository) {
        this.oltRepository = oltRepository;
        this.snmp4jService = snmp4jService;
        this.resolver = resolver;
        this.oidDefinitionRepository = oidDefinitionRepository;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing SNMP Service...");
        SnmpFieldMapper.validateMapping();
        log.info("SNMP Service initialized successfully");
    }

    private List<OidDefinition> fetchOidDefinitions(Vendor vendor, DeviceType deviceType) {
        if (vendor == null || deviceType == null) {
            return Collections.emptyList();
        }
        return oidDefinitionRepository.findByVendorAndDeviceType(vendor, deviceType);
    }

    private List<ProbeResult> probeDevice(List<OidDefinition> defs, IDevice device,
        String ipAddress, int port, String community, int timeoutMs) {
        
        List<ProbeResult> results = new ArrayList<>();
        List<String> requestedOids = new ArrayList<>();
        List<SnmpOidResolver.ResolvedOid> resolved = new ArrayList<>();

        for (OidDefinition d : defs) {
            try {
                SnmpOidResolver.ResolvedOid r = resolver.resolve(device, d.getMetricKey());
                if (r == null) {
                    results.add(new ProbeResult(d.getMetricKey(), null));
                    continue;
                }
                resolved.add(r);
                requestedOids.add(r.getOid());
            } catch (Exception e) {
                results.add(new ProbeResult(d.getMetricKey(), null));
            }
        }

        if (!requestedOids.isEmpty()) {
            try {
                Map<String, org.snmp4j.smi.Variable> res = snmp4jService.getByOids(ipAddress, port, community, timeoutMs, requestedOids);
                for (SnmpOidResolver.ResolvedOid r : resolved) {
                    org.snmp4j.smi.Variable var = res.get(r.getOid());
                    String raw = var == null ? null : var.toString();
                    Object parsed = SnmpUtils.parseVariable(var, r.getValueType());
                    String vt = r.getValueType() == null ? null : r.getValueType().name();
                    
                    String metricKey = defs.stream()
                        .filter(d -> d.getOid().equals(r.getOid()))
                        .map(OidDefinition::getMetricKey)
                        .findFirst()
                        .orElse("unknown");
                    
                    results.add(new ProbeResult(metricKey, raw, parsed, vt, r.getLogicalName()));
                }
            } catch (Exception e) {
                for (SnmpOidResolver.ResolvedOid r : resolved) {
                    String metricKey = defs.stream()
                        .filter(d -> d.getOid().equals(r.getOid()))
                        .map(OidDefinition::getMetricKey)
                        .findFirst()
                        .orElse("unknown");
                    results.add(new ProbeResult(metricKey, null));
                }
            }
        }

        return results;
    }

    private OltSummaryResponse buildOltSummary(Olt olt) {
        List<ProbeResult> probeResults = probeOlt(olt.getId());
        
        OltSummaryResponse summary = new OltSummaryResponse(olt.getId(), olt.getName(), olt.getIpAddress());
        
        summary.setModel(olt.getModel());
        summary.setVendor(olt.getVendor() != null ? olt.getVendor().getName() : "Unknown");
        summary.setDeviceType(olt.getDeviceType() != null ? olt.getDeviceType().getName() : "Unknown");
        summary.setSnmpStatus("success");
        
        for (ProbeResult result : probeResults) {
            String logicalName = result.getLogicalName();
            Object parsedValue = result.getParsedValue();
            String valueTypeStr = result.getValueType();
            
            if (logicalName != null && parsedValue != null) {
                log.debug("Mapping summary field: {} = {} (type: {})", logicalName, parsedValue, valueTypeStr);
                SnmpValueType valueType = SnmpValueType.fromString(valueTypeStr);
                SnmpFieldMapper.mapToSummary(summary, logicalName, parsedValue, valueType);
            } else {
                log.warn("Skipping invalid probe result: logicalName={}, parsedValue={}", logicalName, parsedValue);
            }
        }
        
        if (summary.getOverallStatus() == null) {
            summary.setOverallStatus(summary.getUptime() != null && !summary.getUptime().equals("N/A") ? "online" : "offline");
        }
        
        return summary;
    }

    private OltDetailedResponse buildOltDetailed(Olt olt) {
        List<ProbeResult> probeResults = probeOlt(olt.getId());
        
        OltDetailedResponse detailed = new OltDetailedResponse(olt.getId(), olt.getName(), olt.getIpAddress());
        
        detailed.setModel(olt.getModel());
        detailed.setVendor(olt.getVendor() != null ? olt.getVendor().getName() : "Unknown");
        detailed.setDeviceType(olt.getDeviceType() != null ? olt.getDeviceType().getName() : "Unknown");
        detailed.setSnmpStatus("success");
        
        for (ProbeResult result : probeResults) {
            String logicalName = result.getLogicalName();
            Object parsedValue = result.getParsedValue();
            String valueTypeStr = result.getValueType();
            
            if (logicalName != null && parsedValue != null) {
                log.debug("Mapping detailed field: {} = {} (type: {})", logicalName, parsedValue, valueTypeStr);
                SnmpValueType valueType = SnmpValueType.fromString(valueTypeStr);
                SnmpFieldMapper.mapToDetailed(detailed, logicalName, parsedValue, valueType);
            } else {
                log.warn("Skipping invalid probe result: logicalName={}, parsedValue={}", logicalName, parsedValue);
            }
        }
        
        if (detailed.getOverallStatus() == null) {
            detailed.setOverallStatus(detailed.getUptime() != null && !detailed.getUptime().equals("N/A") ? "online" : "offline");
        }
        
        return detailed;
    }

    private OltSummaryResponse createErrorSummary(Olt olt, String errorMessage) {
        OltSummaryResponse errorSummary = new OltSummaryResponse(olt.getId(), olt.getName(), olt.getIpAddress());
        errorSummary.setSnmpStatus("error");
        errorSummary.setErrorMessage(errorMessage);
        errorSummary.setOverallStatus("offline");
        return errorSummary;
    }

    private OltDetailedResponse createErrorDetailed(Olt olt, String errorMessage) {
        OltDetailedResponse errorDetailed = new OltDetailedResponse(olt.getId(), olt.getName(), olt.getIpAddress());
        errorDetailed.setSnmpStatus("error");
        errorDetailed.setErrorMessage(errorMessage);
        errorDetailed.setOverallStatus("offline");
        return errorDetailed;
    }
    
    private List<ProbeResult> probeOlt(Long oltId) {
        Olt olt = oltRepository.findById(oltId).orElse(null);
        if (olt == null) {
            throw new RuntimeException("OLT not found");
        }

        Vendor vendor = olt.getVendor();
        DeviceType deviceType = olt.getDeviceType();
        List<OidDefinition> defs = fetchOidDefinitions(vendor, deviceType);

        if (defs.isEmpty()) {
            throw new RuntimeException("No OID definitions for this OLT vendor/type");
        }

        return probeDevice(defs, olt, olt.getIpAddress(),
            olt.getSnmpPort(), olt.getSnmpCommunity(), olt.getSnmpTimeoutMs());
    }

    public List<OltSummaryResponse> getAllOltsSummary() {
        List<Olt> olts = oltRepository.findAll();
        List<OltSummaryResponse> summaries = new ArrayList<>();
        
        for (Olt olt : olts) {
            try {
                OltSummaryResponse summary = buildOltSummary(olt);
                summaries.add(summary);
            } catch (Exception e) {
                summaries.add(createErrorSummary(olt, e.getMessage()));
            }
        }
        
        return summaries;
    }

    public OltDetailedResponse getOltDetailed(Long oltId) {
        Olt olt = oltRepository.findById(oltId).orElse(null);
        if (olt == null) {
            throw new RuntimeException("OLT not found");
        }

        try {
            return buildOltDetailed(olt);
        } catch (Exception e) {
            return createErrorDetailed(olt, e.getMessage());
        }
    }

}
