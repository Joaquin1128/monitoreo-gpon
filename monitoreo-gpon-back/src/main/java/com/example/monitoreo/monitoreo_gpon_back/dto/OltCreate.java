package com.example.monitoreo.monitoreo_gpon_back.dto;

public class OltCreate {
    private String name;
    private String ipAddress;
    private String location;
    private String model;
    private String vendor;
    private Long hubId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }
    public Long getHubId() { return hubId; }
    public void setHubId(Long hubId) { this.hubId = hubId; }
}
