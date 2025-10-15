package com.example.monitoreo.monitoreo_gpon_back.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "snmp_profile")
public class SnmpProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Vendor vendor;

    private String description;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SnmpOid> oids = new ArrayList<>();
    
    @Column(name = "default_metric_sets", columnDefinition = "text")
    private String defaultMetricSets;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public List<SnmpOid> getOids() { return oids; }
    public void setOids(List<SnmpOid> oids) { this.oids = oids; }
    public String getDefaultMetricSets() { return defaultMetricSets; }
    public void setDefaultMetricSets(String defaultMetricSets) { this.defaultMetricSets = defaultMetricSets; }
}
