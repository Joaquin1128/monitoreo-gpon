package com.example.monitoreo.monitoreo_gpon_back.model;

import com.example.monitoreo.monitoreo_gpon_back.snmp.SnmpValueType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "oid_definition", uniqueConstraints = {@UniqueConstraint(columnNames = {"vendor_id","device_type_id","metric_key"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OidDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "device_type_id", nullable = false)
    private DeviceType deviceType;

    @Column(name = "metric_key", nullable = false)
    private String metricKey;

    @Column(name = "logical_name")
    private String logicalName;

    @Column(nullable = false)
    private String oid;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "value_type", nullable = false)
    private SnmpValueType valueType = SnmpValueType.STRING;

    private String description;

    public OidDefinition(String metricKey, String oid, SnmpValueType valueType) {
        this.metricKey = metricKey;
        this.oid = oid;
        this.valueType = valueType == null ? SnmpValueType.STRING : valueType;
    }

    public OidDefinition(String metricKey, String oid, String valueType) {
        this.metricKey = metricKey;
        this.oid = oid;
        if (valueType == null) {
            this.valueType = SnmpValueType.STRING;
        } else {
            this.valueType = SnmpValueType.fromString(valueType);
        }
    }
}
