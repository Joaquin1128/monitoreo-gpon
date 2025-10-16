package com.example.monitoreo.monitoreo_gpon_back.model;

import com.example.monitoreo.monitoreo_gpon_back.model.enums.SnmpValueTypeEnum;

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

    @Column(nullable = false)
    private String oid;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "value_type", nullable = false)
    private SnmpValueTypeEnum valueType = SnmpValueTypeEnum.STRING;

    private String description;

    public OidDefinition(String metricKey, String oid, SnmpValueTypeEnum valueType) {
        this.metricKey = metricKey;
        this.oid = oid;
        this.valueType = valueType == null ? SnmpValueTypeEnum.STRING : valueType;
    }

    public OidDefinition(String metricKey, String oid, String valueType) {
        this.metricKey = metricKey;
        this.oid = oid;
        if (valueType == null) {
            this.valueType = SnmpValueTypeEnum.STRING;
        } else {
            String v = valueType.trim().toUpperCase();
            if ("NUMERIC".equals(v)) {
                this.valueType = SnmpValueTypeEnum.NUMERIC;
            } else {
                this.valueType = SnmpValueTypeEnum.STRING;
            }
        }
    }
}
