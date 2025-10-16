package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.monitoreo.monitoreo_gpon_back.model.*;
import com.example.monitoreo.monitoreo_gpon_back.model.enums.SnmpValueTypeEnum;
import com.example.monitoreo.monitoreo_gpon_back.repository.*;

@Service
public class SnmpOidResolver {

    private final DeviceTypeRepository deviceTypeRepository;
    private final OidDefinitionRepository oidDefinitionRepository;
    private final OltRepository oltRepository;
    private final OntRepository ontRepository;

    public SnmpOidResolver(DeviceTypeRepository deviceTypeRepository, OidDefinitionRepository oidDefinitionRepository, OltRepository oltRepository, OntRepository ontRepository) {
        this.deviceTypeRepository = deviceTypeRepository;
        this.oidDefinitionRepository = oidDefinitionRepository;
        this.oltRepository = oltRepository;
        this.ontRepository = ontRepository;
    }

    public ResolvedOid resolve(String deviceType, Long deviceId, String metricKey, Map<String, Object> context) {
        String type = deviceType == null ? "" : deviceType.toUpperCase();

        if ("OLT".equals(type)) {
            return resolveForOlt(deviceId, metricKey);
        } else if ("ONT".equals(type) || "ONU".equals(type)) {
            return resolveForOnt(deviceId, metricKey);
        }

        throw new IllegalArgumentException("Unsupported device type: " + deviceType);
    }

    private ResolvedOid resolveForOlt(Long oltId, String metricKey) {
        return oltRepository.findById(oltId)
                .map(olt -> {
                    Vendor v = olt.getVendor();
                    if (v == null) {
                        return null;
                    }

                    Optional<DeviceType> dtOpt = deviceTypeRepository.findByName("OLT");
                    if (dtOpt.isEmpty()) {
                        return null;
                    }
                    
                    List<OidDefinition> defs = oidDefinitionRepository.findByVendorAndDeviceType(v, dtOpt.get());
                    return defs.stream().filter(d -> metricKey.equals(d.getMetricKey())).findFirst()
                        .map(d -> new ResolvedOid(d.getOid(), d.getValueType(), "OID_DEFINITION")).orElse(null);
                })
                .orElse(null);
    }

    private ResolvedOid resolveForOnt(Long ontId, String metricKey) {
        return ontRepository.findById(ontId)
                .map(ont -> {
                    Vendor v = ont.getVendor() != null ? ont.getVendor() : (ont.getOlt() != null ? ont.getOlt().getVendor() : null);
                    if (v == null) {
                        return null;
                    }

                    Optional<DeviceType> dtOpt = deviceTypeRepository.findByName("ONT");
                    if (dtOpt.isEmpty()) {
                        return null;
                    }

                    List<OidDefinition> defs = oidDefinitionRepository.findByVendorAndDeviceType(v, dtOpt.get());
                    return defs.stream().filter(d -> metricKey.equals(d.getMetricKey())).findFirst()
                        .map(d -> new ResolvedOid(d.getOid(), d.getValueType(), "OID_DEFINITION")).orElse(null);
                })
                .orElse(null);
    }

    public static class ResolvedOid {
        private final String oid;
        private final SnmpValueTypeEnum valueType;
        private final String source;

        public ResolvedOid(String oid, SnmpValueTypeEnum valueType, String source) {
            this.oid = oid;
            this.valueType = valueType;
            this.source = source;
        }

        public String getOid() { return oid; }
        public SnmpValueTypeEnum getValueType() { return valueType; }
        public String getSource() { return source; }
    }
}
