package com.example.monitoreo.monitoreo_gpon_back.snmp.dto;

import java.time.Instant;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OltDetailedResponse {
    private Long id;
    private String name;
    private String ipAddress;
    private String model;
    private String vendor;
    private String deviceType;
    private String overallStatus;
    private String uptime;
    private String temperature;
    private String cpuUsage;
    private String memoryUsage;
    private String interfaceStatus;
    private String lastUpdate;
    private String snmpStatus;
    private String errorMessage;

    public OltDetailedResponse(Long id, String name, String ipAddress) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.lastUpdate = Instant.now().toString();
    }
}
