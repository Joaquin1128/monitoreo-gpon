package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.math.BigInteger;

import org.snmp4j.smi.Variable;

import com.example.monitoreo.monitoreo_gpon_back.model.enums.SnmpValueTypeEnum;

public final class SnmpUtils {

    private SnmpUtils() {}

    public static Object parseVariable(Variable var, SnmpValueTypeEnum type) {
        if (var == null) {
            return null;
        }

        if (type == null) {
            type = SnmpValueTypeEnum.STRING;
        }

        try {
            switch (type) {
                case NUMERIC:
                    try {
                        return var.toLong();
                    } catch (Exception ex) {
                        try {
                            return new BigInteger(var.toString());
                        } catch (Exception ex2) {
                            return Double.parseDouble(var.toString());
                        }
                    }
                case STRING:
                default:
                    return var.toString();
            }
        } catch (Exception e) {
            return var.toString();
        }
    }
}
