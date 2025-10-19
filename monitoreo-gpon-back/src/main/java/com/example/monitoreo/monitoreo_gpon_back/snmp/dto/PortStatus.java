package com.example.monitoreo.monitoreo_gpon_back.snmp.dto;

public class PortStatus {
    private int portNumber;
    private String status;
    private String speed;
    private String description;
    private String adminStatus;
    private String operStatus;

    public PortStatus() {}

    public PortStatus(int portNumber, String status) {
        this.portNumber = portNumber;
        this.status = status;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(String adminStatus) {
        this.adminStatus = adminStatus;
    }

    public String getOperStatus() {
        return operStatus;
    }

    public void setOperStatus(String operStatus) {
        this.operStatus = operStatus;
    }
}
