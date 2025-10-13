package com.example.monitoreo.monitoreo_gpon_back.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "metric")
public class Metric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "olt_id")
    private Olt olt;

    @ManyToOne
    @JoinColumn(name = "ont_id")
    private Ont ont;

    private String metricType;
    private Double value;
    private String stringValue;
    private OffsetDateTime collectedAt = OffsetDateTime.now();

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Olt getOlt() { return olt; }
    public void setOlt(Olt olt) { this.olt = olt; }
    public Ont getOnt() { return ont; }
    public void setOnt(Ont ont) { this.ont = ont; }
    public String getMetricType() { return metricType; }
    public void setMetricType(String metricType) { this.metricType = metricType; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public String getStringValue() { return stringValue; }
    public void setStringValue(String stringValue) { this.stringValue = stringValue; }
    public OffsetDateTime getCollectedAt() { return collectedAt; }
    public void setCollectedAt(OffsetDateTime collectedAt) { this.collectedAt = collectedAt; }
}
