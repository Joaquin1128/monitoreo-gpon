package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.monitoreo.monitoreo_gpon_back.snmp.dto.OltDetailedResponse;
import com.example.monitoreo.monitoreo_gpon_back.snmp.dto.OltSummaryResponse;

public class SnmpFieldMapper {
    
    private static final Logger log = LoggerFactory.getLogger(SnmpFieldMapper.class);
    private static final Map<String, Method> summarySetters = new HashMap<>();
    private static final Map<String, Method> detailedSetters = new HashMap<>();
    
    static {
        initializeMethodCache();
    }
    
    private static void initializeMethodCache() {
        for (SnmpField field : SnmpField.values()) {
            if (field.isSummaryField()) {
                String setterName = "set" + capitalize(field.getLogicalName().replace("_", ""));
                try {
                    Method method = findSetterMethod(OltSummaryResponse.class, setterName, String.class);
                    if (method != null) {
                        summarySetters.put(field.getLogicalName(), method);
                    }
                } catch (Exception e) {
                    log.debug("No setter found for summary field: {}", field.getLogicalName());
                }
            }
            
            if (field.isDetailedField()) {
                String setterName = "set" + capitalize(field.getLogicalName().replace("_", ""));
                try {
                    Method method = findSetterMethod(OltDetailedResponse.class, setterName, String.class);
                    if (method != null) {
                        detailedSetters.put(field.getLogicalName(), method);
                    }
                } catch (Exception e) {
                    log.debug("No setter found for detailed field: {}", field.getLogicalName());
                }
            }
        }
    }
    
    public static void mapToSummary(OltSummaryResponse summary, String logicalName, Object value, SnmpValueType valueType) {
        if (logicalName == null || value == null) {
            return;
        }
        
        SnmpField field = SnmpField.fromLogicalName(logicalName);
        if (field == null || !field.isSummaryField()) {
            log.warn("Unknown or invalid summary field: {}", logicalName);
            return;
        }
        
        try {
            Method setter = summarySetters.get(logicalName);
            if (setter != null) {
                String stringValue = formatValue(value, valueType);
                setter.invoke(summary, stringValue);
            } else {
                log.warn("No setter method found for summary field: {}", logicalName);
            }
        } catch (Exception e) {
            log.error("Error mapping field {} to summary: {}", logicalName, e.getMessage());
        }
    }
    
    public static void mapToDetailed(OltDetailedResponse detailed, String logicalName, Object value, SnmpValueType valueType) {
        if (logicalName == null || value == null) {
            return;
        }
        
        SnmpField field = SnmpField.fromLogicalName(logicalName);
        if (field == null || !field.isDetailedField()) {
            log.warn("Unknown or invalid detailed field: {}", logicalName);
            return;
        }
        
        try {
            Method setter = detailedSetters.get(logicalName);
            if (setter != null) {
                String stringValue = formatValue(value, valueType);
                setter.invoke(detailed, stringValue);
            } else {
                log.warn("No setter method found for detailed field: {}", logicalName);
            }
        } catch (Exception e) {
            log.error("Error mapping field {} to detailed: {}", logicalName, e.getMessage());
        }
    }
    
    private static String formatValue(Object value, SnmpValueType valueType) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Number) {
            if (value instanceof Long || value instanceof Integer) {
                return value.toString();
            } else if (value instanceof Double || value instanceof Float) {
                return String.format("%.2f", value);
            } else if (value instanceof BigInteger) {
                return value.toString();
            }
        }
        
        return value.toString();
    }
    
    
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    private static Method findSetterMethod(Class<?> clazz, String methodName, Class<?> paramType) {
        try {
            return clazz.getMethod(methodName, paramType);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    public static void validateMapping() {
        log.info("Validating SNMP field mapping...");
        
        for (SnmpField field : SnmpField.values()) {
            if (field.isSummaryField() && !summarySetters.containsKey(field.getLogicalName())) {
                log.warn("Summary field {} is not mapped to any setter", field.getLogicalName());
            }
            
            if (field.isDetailedField() && !detailedSetters.containsKey(field.getLogicalName())) {
                log.warn("Detailed field {} is not mapped to any setter", field.getLogicalName());
            }
        }
        
        log.info("SNMP field mapping validation completed");
    }
}
