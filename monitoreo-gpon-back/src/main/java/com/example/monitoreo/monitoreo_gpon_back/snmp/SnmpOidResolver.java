package com.example.monitoreo.monitoreo_gpon_back.snmp;

import org.springframework.stereotype.Service;

import com.example.monitoreo.monitoreo_gpon_back.model.*;
import com.example.monitoreo.monitoreo_gpon_back.model.enums.SnmpValueTypeEnum;
import com.example.monitoreo.monitoreo_gpon_back.repository.*;

@Service
public class SnmpOidResolver {

    private final OidDefinitionRepository oidDefinitionRepository;

    public SnmpOidResolver(OidDefinitionRepository oidDefinitionRepository) {
        this.oidDefinitionRepository = oidDefinitionRepository;
    }

    public ResolvedOid resolve(Vendor vendor, DeviceType deviceType, String metricKey) {
        if (vendor == null || deviceType == null || metricKey == null) {
            return null;
        }

        return oidDefinitionRepository.findByVendorAndDeviceType(vendor, deviceType).stream()
            .filter(d -> metricKey.equals(d.getMetricKey()))
            .findFirst()
            .map(d -> new ResolvedOid(d.getOid(), d.getValueType(), "OID_DEFINITION"))
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
