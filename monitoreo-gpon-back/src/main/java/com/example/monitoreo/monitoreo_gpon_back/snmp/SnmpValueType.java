package com.example.monitoreo.monitoreo_gpon_back.snmp;

import lombok.Getter;

@Getter
public enum SnmpValueType {

    STRING("OctetString", "Texto plano o identificadores"),
    
    INTEGER32("Integer32", "Entero con signo de 32 bits"),
    UNSIGNED32("Unsigned32", "Entero sin signo de 32 bits"),
    
    COUNTER32("Counter32", "Contador de 32 bits sin signo"),
    COUNTER64("Counter64", "Contador de 64 bits sin signo"),
    
    GAUGE32("Gauge32", "Medidor de 32 bits"),
    GAUGE64("Gauge64", "Medidor de 64 bits"),
    
    TIMETICKS("TimeTicks", "Tiempo en centisegundos"),
    IP_ADDRESS("IpAddress", "Dirección IPv4"),
    OID("OID", "Identificador de objeto"),
    BOOLEAN("Boolean", "Valor binario");

    private final String displayName;
    private final String description;

    SnmpValueType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public static SnmpValueType fromString(String valueType) {
        if (valueType == null) {
            return STRING;
        }

        try {
            return SnmpValueType.valueOf(valueType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return STRING;
        }
    }
}
