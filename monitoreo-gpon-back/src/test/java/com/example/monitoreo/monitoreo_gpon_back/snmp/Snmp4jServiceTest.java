package com.example.monitoreo.monitoreo_gpon_back.snmp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.PDU;
import org.snmp4j.smi.*;
import org.snmp4j.Snmp;
import org.snmp4j.Target;

public class Snmp4jServiceTest {

    private void setField(Object target, String name, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getByOids_successReturnsMap() throws Exception {
        Snmp snmp = mock(Snmp.class);
        ResponseEvent<Address> resp = mock(ResponseEvent.class);

        PDU responsePdu = new PDU();
        VariableBinding vb = new VariableBinding(new OID(".1.2"), new OctetString("ok"));
        responsePdu.add(vb);

        when(resp.getResponse()).thenReturn(responsePdu);
        when(snmp.get(any(PDU.class), any(Target.class))).thenReturn(resp);

        Snmp4jService svc = new Snmp4jService();
        setField(svc, "snmp", snmp);

        Map<String, Variable> res = svc.getByOids("127.0.0.1", 161, "public", 1000, List.of(".1.2"));
        assertNotNull(res);
        assertEquals(1, res.size());
        String foundKey = res.keySet().stream().filter(k -> k.endsWith("1.2")).findFirst().orElse(null);
        assertNotNull(foundKey);
        assertEquals("ok", res.get(foundKey).toString());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getByOids_nullResponse_throws() throws Exception {
        Snmp snmp = mock(Snmp.class);
        ResponseEvent<Address> resp = mock(ResponseEvent.class);

        when(resp.getResponse()).thenReturn(null);
        when(snmp.get(any(PDU.class), any(Target.class))).thenReturn(resp);

        Snmp4jService svc = new Snmp4jService();
        setField(svc, "snmp", snmp);

        Exception ex = assertThrows(Exception.class, () -> {
            svc.getByOids("127.0.0.1", 161, "public", 1000, List.of(".1.2"));
        });
        assertTrue(ex.getMessage().contains("No SNMP response"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getByOids_errorStatus_throws() throws Exception {
        Snmp snmp = mock(Snmp.class);
        ResponseEvent<Address> resp = mock(ResponseEvent.class);

        PDU responsePdu = mock(PDU.class);
        when(responsePdu.getErrorStatus()).thenReturn(1); // error
        when(responsePdu.getErrorStatusText()).thenReturn("err");

        when(resp.getResponse()).thenReturn(responsePdu);
        when(snmp.get(any(PDU.class), any(Target.class))).thenReturn(resp);

        Snmp4jService svc = new Snmp4jService();
        setField(svc, "snmp", snmp);

        Exception ex = assertThrows(Exception.class, () -> {
            svc.getByOids("127.0.0.1", 161, "public", 1000, List.of(".1.2"));
        });
        assertTrue(ex.getMessage().contains("SNMP error"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getByOids_emptyResult_throws() throws Exception {
        Snmp snmp = mock(Snmp.class);
        ResponseEvent<Address> resp = mock(ResponseEvent.class);

        PDU responsePdu = new PDU(); // vacío
        when(resp.getResponse()).thenReturn(responsePdu);
        when(snmp.get(any(PDU.class), any(Target.class))).thenReturn(resp);

        Snmp4jService svc = new Snmp4jService();
        setField(svc, "snmp", snmp);

        Exception ex = assertThrows(Exception.class, () -> {
            svc.getByOids("127.0.0.1", 161, "public", 1000, List.of(".1.2"));
        });
        assertTrue(ex.getMessage().contains("returned empty result"));
    }
}
