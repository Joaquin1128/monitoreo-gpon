package com.example.monitoreo.monitoreo_gpon_back.snmp.dto;

import java.util.List;
import java.util.Map;

public class ProbeRequest {
    private List<String> metrics;
    private Map<String, Object> context;

    public List<String> getMetrics() { return metrics; }
    public void setMetrics(List<String> metrics) { this.metrics = metrics; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
}
