package com.example.monitoreo.monitoreo_gpon_back.snmp;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;

import com.example.monitoreo.monitoreo_gpon_back.model.enums.SnmpValueTypeEnum;

public class SnmpUtilsTest {

    @Test
    public void parseInteger32AsNumber() {
        Variable v = new Integer32(123);
        Object parsed = SnmpUtils.parseVariable(v, SnmpValueTypeEnum.NUMERIC);
        assertNotNull(parsed);
        assertTrue(parsed instanceof Number);
        assertEquals(123L, ((Number) parsed).longValue());
    }

    @Test
    public void parseCounter64AsLong() {
        Variable v = new Counter64(1234567890L);
        Object parsed = SnmpUtils.parseVariable(v, SnmpValueTypeEnum.NUMERIC);
        assertNotNull(parsed);
        assertTrue(parsed instanceof Number);
        assertEquals(1234567890L, ((Number) parsed).longValue());
    }

    @Test
    public void parseCounter64AsBigInteger() {
        BigInteger bigValue = new BigInteger("12345678901234567890");
        Variable v = new Counter64(bigValue.longValue());
        Object parsed = SnmpUtils.parseVariable(v, SnmpValueTypeEnum.NUMERIC);
        assertNotNull(parsed);
        assertTrue(parsed instanceof Number || parsed instanceof BigInteger);
    }

    @Test
    public void parseOctetStringAsString() {
        Variable v = new OctetString("hello-ñ");
        Object parsed = SnmpUtils.parseVariable(v, SnmpValueTypeEnum.STRING);
        assertNotNull(parsed);
        assertEquals(v.toString(), parsed);
    }

    @Test
    public void nullVariableReturnsNull() {
        Object parsed = SnmpUtils.parseVariable(null, SnmpValueTypeEnum.STRING);
        assertNull(parsed);
    }
}
