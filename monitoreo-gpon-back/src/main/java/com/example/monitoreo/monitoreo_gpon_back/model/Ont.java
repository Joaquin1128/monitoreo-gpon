package com.example.monitoreo.monitoreo_gpon_back.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ont")
public class Ont {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "olt_id")
    private Olt olt;

    private String onuId;
    private Integer onuIndex;
    private String serialNumber;
    private String model;
    private String vendor;
    private String status;

    private OffsetDateTime lastSeenAt;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Olt getOlt() { return olt; }
    public void setOlt(Olt olt) { this.olt = olt; }
    public String getOnuId() { return onuId; }
    public void setOnuId(String onuId) { this.onuId = onuId; }
    public Integer getOnuIndex() { return onuIndex; }
    public void setOnuIndex(Integer onuIndex) { this.onuIndex = onuIndex; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(OffsetDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }
}
