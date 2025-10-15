package com.example.monitoreo.monitoreo_gpon_back.model;

import jakarta.persistence.*;

@Entity
@Table(name = "snmp_oid")
public class SnmpOid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private SnmpProfile profile;

    private String metricKey;
    private String oid;

    @Enumerated(EnumType.STRING)
    private ValueType valueType = ValueType.STRING;

    public enum ValueType { NUMERIC, STRING }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SnmpProfile getProfile() { return profile; }
    public void setProfile(SnmpProfile profile) { this.profile = profile; }
    public String getMetricKey() { return metricKey; }
    public void setMetricKey(String metricKey) { this.metricKey = metricKey; }
    public String getOid() { return oid; }
    public void setOid(String oid) { this.oid = oid; }
    public ValueType getValueType() { return valueType; }
    public void setValueType(ValueType valueType) { this.valueType = valueType; }
}
