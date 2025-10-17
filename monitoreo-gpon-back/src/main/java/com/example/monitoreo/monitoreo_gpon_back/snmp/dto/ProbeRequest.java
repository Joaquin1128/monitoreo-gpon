package com.example.monitoreo.monitoreo_gpon_back.snmp.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ProbeRequest {
    private List<String> metrics;
    private Map<String, Object> context;
}
