package com.example.monitoreo.monitoreo_gpon_back.snmp.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProbeResult {
    private String metricKey;
    private String value;
    private Object parsedValue;
    private String valueType;
    private String logicalName;

    public ProbeResult(String metricKey, String value) {
        this.metricKey = metricKey;
        this.value = value;
        this.parsedValue = null;
        this.valueType = null;
        this.logicalName = null;
    }
}
