package com.example.monitoreo.monitoreo_gpon_back.snmp.dto;

import java.time.Instant;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OltSummaryResponse {
    private Long id;
    private String name;
    private String ipAddress;
    private String model;
    private String vendor;
    private String deviceType;
    private String overallStatus;
    private String uptime;
    private String lastUpdate;
    private String snmpStatus;
    private String errorMessage;

    public OltSummaryResponse(Long id, String name, String ipAddress) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.lastUpdate = Instant.now().toString();
    }
}
