package com.example.monitoreo.monitoreo_gpon_back.model;

import jakarta.persistence.*;

@Entity
public class Olt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String ipAddress;
    private String model;
    private String vendor;
    private String location;
    private String status;

    @ManyToOne
    @JoinColumn(name = "hub_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Hub hub;

    public Olt() {}

    public Olt(String name, String ipAddress, String model, String vendor, String location, String status) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.model = model;
        this.vendor = vendor;
        this.location = location;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
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
}
