package com.example.monitoreo.monitoreo_gpon_back.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "port")
public class Port {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "olt_id")
    private Olt olt;

    private Integer slot;
    private Integer portNumber;
    private String description;
    private String status;

    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Olt getOlt() { return olt; }
    public void setOlt(Olt olt) { this.olt = olt; }
    public Integer getSlot() { return slot; }
    public void setSlot(Integer slot) { this.slot = slot; }
    public Integer getPortNumber() { return portNumber; }
    public void setPortNumber(Integer portNumber) { this.portNumber = portNumber; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
