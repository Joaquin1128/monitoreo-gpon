package com.example.monitoreo.monitoreo_gpon_back.snmp;

import org.springframework.stereotype.Service;

import com.example.monitoreo.monitoreo_gpon_back.model.*;
import com.example.monitoreo.monitoreo_gpon_back.repository.*;

@Service
public class SnmpOidResolver {

    private final OidDefinitionRepository oidDefinitionRepository;

    public SnmpOidResolver(OidDefinitionRepository oidDefinitionRepository) {
        this.oidDefinitionRepository = oidDefinitionRepository;
    }

    public ResolvedOid resolve(IDevice device, String metricKey) {
        if (device == null || metricKey == null) {
            return null;
        }
        
        Vendor vendor = device.getVendor();
        DeviceType deviceType = device.getDeviceType();
        if (vendor == null || deviceType == null || metricKey == null) {
            return null;
        }

        return oidDefinitionRepository.findByVendorAndDeviceType(vendor, deviceType).stream()
            .filter(d -> metricKey.equals(d.getMetricKey()))
            .findFirst()
            .map(d -> new ResolvedOid(d.getOid(), d.getValueType(), d.getLogicalName()))
            .orElse(null);
    }

    public static class ResolvedOid {
        private final String oid;
        private final SnmpValueType valueType;
        private final String logicalName;

        public ResolvedOid(String oid, SnmpValueType valueType, String logicalName) {
            this.oid = oid;
            this.valueType = valueType;
            this.logicalName = logicalName;
        }

        public String getOid() { return oid; }
        public SnmpValueType getValueType() { return valueType; }
        public String getLogicalName() { return logicalName; }
    }
}
