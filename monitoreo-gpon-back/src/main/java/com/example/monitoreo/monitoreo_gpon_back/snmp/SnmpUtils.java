package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.math.BigInteger;

import org.snmp4j.smi.Variable;

public final class SnmpUtils {

    private SnmpUtils() {}

    public static Object parseVariable(Variable var, SnmpValueType type) {
        if (var == null) {
            return null;
        }

        if (type == null) {
            type = SnmpValueType.STRING;
        }

        String valueStr = var.toString();
        
        switch (type) {
            case INTEGER32:
            case UNSIGNED32:
            case COUNTER32:
            case GAUGE32:
                try {
                    return Long.parseLong(valueStr);
                } catch (NumberFormatException e) {
                    return 0L;
                }
            case COUNTER64:
            case GAUGE64:
                try {
                    return new BigInteger(valueStr);
                } catch (NumberFormatException e) {
                    return BigInteger.ZERO;
                }
            case TIMETICKS:
                try {
                    return Long.parseLong(valueStr);
                } catch (NumberFormatException e) {
                    return 0L;
                }
            case BOOLEAN:
                return "1".equals(valueStr) || "true".equalsIgnoreCase(valueStr);
            case IP_ADDRESS:
            case OID:
            case STRING:
            default:
                return valueStr;
        }
    }
}
