package com.example.monitoreo.monitoreo_gpon_back.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "olt")
public class Olt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String ipAddress;
    private String snmpVersion;
    private String snmpCommunity;
    private Integer snmpPort = 161;
    private Integer snmpTimeoutMs = 5000;
    private String commandProtectionPassword;

    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "olt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ont> onts = new ArrayList<>();

    // campos adicionales usados por controladores y UI
    private String model;
    private String vendor;
    private String location;
    private String status;

    @ManyToOne
    @JoinColumn(name = "hub_id")
    @JsonBackReference
    private Hub hub;

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getSnmpVersion() { return snmpVersion; }
    public void setSnmpVersion(String snmpVersion) { this.snmpVersion = snmpVersion; }
    public String getSnmpCommunity() { return snmpCommunity; }
    public void setSnmpCommunity(String snmpCommunity) { this.snmpCommunity = snmpCommunity; }
    public Integer getSnmpPort() { return snmpPort; }
    public void setSnmpPort(Integer snmpPort) { this.snmpPort = snmpPort; }
    public Integer getSnmpTimeoutMs() { return snmpTimeoutMs; }
    public void setSnmpTimeoutMs(Integer snmpTimeoutMs) { this.snmpTimeoutMs = snmpTimeoutMs; }
    public String getCommandProtectionPassword() { return commandProtectionPassword; }
    public void setCommandProtectionPassword(String commandProtectionPassword) { this.commandProtectionPassword = commandProtectionPassword; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<Ont> getOnts() { return onts; }
    public void setOnts(List<Ont> onts) { this.onts = onts; }
    
    // extra getters/setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Hub getHub() { return hub; }
    public void setHub(Hub hub) { this.hub = hub; }

    // constructor usado por OltController
    public Olt() {}

    public Olt(String name, String ipAddress, String model, String vendor, String location, String status) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.model = model;
        this.vendor = vendor;
        this.location = location;
        this.status = status;
    }
}

