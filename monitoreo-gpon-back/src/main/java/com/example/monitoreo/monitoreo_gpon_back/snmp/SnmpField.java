package com.example.monitoreo.monitoreo_gpon_back.snmp;

import lombok.Getter;

@Getter
public enum SnmpField {
    UPTIME("uptime", "Tiempo de actividad del dispositivo"),
    OVERALL_STATUS("overall_status", "Estado general del dispositivo"),
    TEMPERATURE("temperature", "Temperatura del dispositivo"),
    CPU_USAGE("cpu_usage", "Uso de CPU"),
    MEMORY_USAGE("memory_usage", "Uso de memoria"),
    INTERFACE_STATUS("interface_status", "Estado de interfaces"),
    SYS_DESCRIPTION("sys_description", "Descripción del sistema"),
    PORT_STATUS("port_status", "Estado de puertos"),
    POWER_STATUS("power_status", "Estado de alimentación"),
    FAN_STATUS("fan_status", "Estado de ventiladores"),
    ALARM_STATUS("alarm_status", "Estado de alarmas");

    private final String logicalName;
    private final String description;
    private final boolean isSummaryField;
    private final boolean isDetailedField;

    SnmpField(String logicalName, String description) {
        this(logicalName, description, true, true);
    }

    SnmpField(String logicalName, String description, boolean isSummaryField, boolean isDetailedField) {
        this.logicalName = logicalName;
        this.description = description;
        this.isSummaryField = isSummaryField;
        this.isDetailedField = isDetailedField;
    }

    public static SnmpField fromLogicalName(String logicalName) {
        if (logicalName == null) {
            return null;
        }

        for (SnmpField field : values()) {
            if (field.logicalName.equals(logicalName)) return field;
        }
        
        return null;
    }

    public static boolean isValidLogicalName(String logicalName) {
        return fromLogicalName(logicalName) != null;
    }
}
