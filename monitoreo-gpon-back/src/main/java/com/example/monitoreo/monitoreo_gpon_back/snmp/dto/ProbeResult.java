package com.example.monitoreo.monitoreo_gpon_back.snmp.dto;

public class ProbeResult {
    private String metricKey;
    private String value;
    private String source; // DEVICE_OVERRIDE | DEVICE_PROFILE | VENDOR_PROFILE
    private Object parsedValue;
    private String valueType;

    public ProbeResult() {}
    public ProbeResult(String metricKey, String value, String source) {
        this.metricKey = metricKey;
        this.value = value;
        this.source = source;
    }
    public ProbeResult(String metricKey, String value, Object parsedValue, String valueType, String source) {
        this.metricKey = metricKey;
        this.value = value;
        this.parsedValue = parsedValue;
        this.valueType = valueType;
        this.source = source;
    }
    public String getMetricKey() { return metricKey; }
    public void setMetricKey(String metricKey) { this.metricKey = metricKey; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Object getParsedValue() { return parsedValue; }
    public void setParsedValue(Object parsedValue) { this.parsedValue = parsedValue; }
    public String getValueType() { return valueType; }
    public void setValueType(String valueType) { this.valueType = valueType; }
}
