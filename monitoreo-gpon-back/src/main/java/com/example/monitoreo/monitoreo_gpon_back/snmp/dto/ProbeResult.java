package com.example.monitoreo.monitoreo_gpon_back.snmp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProbeResult {
    private String metricKey;
    private String value;
    private String source;
    private Object parsedValue;
    private String valueType;

    public ProbeResult(String metricKey, String value, String source) {
        this.metricKey = metricKey;
        this.value = value;
        this.source = source;
    }
}
