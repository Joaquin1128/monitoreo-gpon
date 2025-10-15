package com.example.monitoreo.monitoreo_gpon_back.snmp;

import com.example.monitoreo.monitoreo_gpon_back.model.*;
import com.example.monitoreo.monitoreo_gpon_back.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SnmpOidResolver {

    private final SnmpProfileRepository profileRepository;
    private final SnmpOidRepository oidRepository;
    private final OltRepository oltRepository;
    private final OntRepository ontRepository;
    public SnmpOidResolver(SnmpProfileRepository profileRepository, SnmpOidRepository oidRepository, OltRepository oltRepository, OntRepository ontRepository) {
        this.profileRepository = profileRepository;
        this.oidRepository = oidRepository;
        this.oltRepository = oltRepository;
        this.ontRepository = ontRepository;
    }

    public ResolvedOid resolve(String deviceType, Long deviceId, String metricKey, Map<String, Object> context) {
        // busca perfil asignado al dispositivo (ver en futuro)
        if ("OLT".equals(deviceType)) {
            Optional<Olt> oltOpt = oltRepository.findById(deviceId);
            if (oltOpt.isPresent()) {
                Olt olt = oltOpt.get();
                SnmpProfile p = olt.getSnmpProfile();
                if (p != null) {
                    List<SnmpOid> list = oidRepository.findByProfile(p);
                    for (SnmpOid s : list) if (metricKey.equals(s.getMetricKey())) return new ResolvedOid(s.getOid(), s.getValueType(), "DEVICE_PROFILE");
                }
                // vendor default
                if (olt.getVendor() != null) {
                    Optional<SnmpProfile> vendorProfile = profileRepository.findByName(olt.getVendor().name() + " default");
                    if (vendorProfile.isPresent()) {
                        for (SnmpOid s : oidRepository.findByProfile(vendorProfile.get())) if (metricKey.equals(s.getMetricKey())) return new ResolvedOid(s.getOid(), s.getValueType(), "VENDOR_PROFILE");
                    }
                }
            }
        } else if ("ONT".equals(deviceType)) {
            Optional<Ont> ontOpt = ontRepository.findById(deviceId);
            if (ontOpt.isPresent()) {
                Ont ont = ontOpt.get();
                SnmpProfile p = ont.getOlt() != null ? ont.getOlt().getSnmpProfile() : null;
                if (p != null) {
                    for (SnmpOid s : oidRepository.findByProfile(p)) if (metricKey.equals(s.getMetricKey())) return new ResolvedOid(s.getOid(), s.getValueType(), "DEVICE_PROFILE");
                }
                if (ont.getVendor() != null) {
                    Optional<SnmpProfile> vendorProfile = profileRepository.findByName(ont.getVendor().name() + " default");
                    if (vendorProfile.isPresent()) {
                        for (SnmpOid s : oidRepository.findByProfile(vendorProfile.get())) if (metricKey.equals(s.getMetricKey())) return new ResolvedOid(s.getOid(), s.getValueType(), "VENDOR_PROFILE");
                    }
                }
            }
        }

        throw new RuntimeException("OID_NOT_FOUND");
    }

    public static class ResolvedOid {
        private final String oid;
        private final SnmpOid.ValueType valueType;
        private final String source;

        public ResolvedOid(String oid, SnmpOid.ValueType valueType, String source) {
            this.oid = oid;
            this.valueType = valueType;
            this.source = source;
        }

        public String getOid() { return oid; }
        public SnmpOid.ValueType getValueType() { return valueType; }
        public String getSource() { return source; }
    }
}
