package com.example.monitoreo.monitoreo_gpon_back.snmp.dto;

public class ProbeResult {
    private String metricKey;
    private String value;
    private String source; // DEVICE_OVERRIDE | DEVICE_PROFILE | VENDOR_PROFILE

    public ProbeResult() {}
    public ProbeResult(String metricKey, String value, String source) {
        this.metricKey = metricKey;
        this.value = value;
        this.source = source;
    }
    public String getMetricKey() { return metricKey; }
    public void setMetricKey(String metricKey) { this.metricKey = metricKey; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
