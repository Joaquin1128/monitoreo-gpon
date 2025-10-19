// package com.example.monitoreo.monitoreo_gpon_back.model;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import org.junit.jupiter.api.Test;

// import com.example.monitoreo.monitoreo_gpon_back.snmp.SnmpValueType;

// public class OidDefinitionTest {

//     @Test
//     public void constructWithEnum() {
//         OidDefinition d = new OidDefinition("m", ".1.2.3", SnmpValueType.NUMERIC);
//         assertEquals(SnmpValueType.NUMERIC, d.getValueType());
//     }

//     @Test
//     public void constructWithStringNumeric() {
//         OidDefinition d = new OidDefinition("m", ".1.2.3", "NUMERIC");
//         assertEquals(SnmpValueType.NUMERIC, d.getValueType());
//     }

//     @Test
//     public void constructWithStringDefaultString() {
//         OidDefinition d = new OidDefinition("m", ".1.2.3", "FOO");
//         assertEquals(SnmpValueType.STRING, d.getValueType());
//     }

//     @Test
//     public void constructWithStringNumericLowercaseAndSpaces() {
//         OidDefinition d = new OidDefinition("m", ".1.2.3", " numeric ");
//         assertEquals(SnmpValueType.NUMERIC, d.getValueType());
//     }

//     @Test
//     public void constructWithNullDefaultsToString() {
//         OidDefinition d = new OidDefinition("m", ".1.2.3", (String) null);
//         assertEquals(SnmpValueType.STRING, d.getValueType());
//     }
// }
