package com.example.monitoreo.monitoreo_gpon_back.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.example.monitoreo.monitoreo_gpon_back.model.enums.SnmpValueTypeEnum;

public class OidDefinitionTest {

    @Test
    public void constructWithEnum() {
        OidDefinition d = new OidDefinition("m", ".1.2.3", SnmpValueTypeEnum.NUMERIC);
        assertEquals(SnmpValueTypeEnum.NUMERIC, d.getValueType());
    }

    @Test
    public void constructWithStringNumeric() {
        OidDefinition d = new OidDefinition("m", ".1.2.3", "NUMERIC");
        assertEquals(SnmpValueTypeEnum.NUMERIC, d.getValueType());
    }

    @Test
    public void constructWithStringDefaultString() {
        OidDefinition d = new OidDefinition("m", ".1.2.3", "FOO");
        assertEquals(SnmpValueTypeEnum.STRING, d.getValueType());
    }

    @Test
    public void constructWithStringNumericLowercaseAndSpaces() {
        OidDefinition d = new OidDefinition("m", ".1.2.3", " numeric ");
        assertEquals(SnmpValueTypeEnum.NUMERIC, d.getValueType());
    }

    @Test
    public void constructWithNullDefaultsToString() {
        OidDefinition d = new OidDefinition("m", ".1.2.3", (String) null);
        assertEquals(SnmpValueTypeEnum.STRING, d.getValueType());
    }
}
