package com.example.monitoreo.monitoreo_gpon_back.snmp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.example.monitoreo.monitoreo_gpon_back.model.OidDefinition;
import com.example.monitoreo.monitoreo_gpon_back.model.Vendor;
import com.example.monitoreo.monitoreo_gpon_back.model.DeviceType;
import com.example.monitoreo.monitoreo_gpon_back.model.enums.SnmpValueTypeEnum;
import com.example.monitoreo.monitoreo_gpon_back.repository.OidDefinitionRepository;
import com.example.monitoreo.monitoreo_gpon_back.snmp.SnmpOidResolver.ResolvedOid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SnmpOidResolverTest {

    private OidDefinitionRepository oidRepo;
    private SnmpOidResolver resolver;

    @BeforeEach
    public void setup() {
        oidRepo = mock(OidDefinitionRepository.class);
        resolver = new SnmpOidResolver(oidRepo);
    }

    @Test
    public void resolveForDeviceReturnsResolvedOid() {
        Vendor v = new Vendor(); v.setId(1L); v.setName("ACME");
        DeviceType dt = new DeviceType(); dt.setId(2L); dt.setName("OLT");
        OidDefinition d = new OidDefinition(); d.setId(10L); d.setMetricKey("cpu"); d.setOid(".1.2.3"); d.setValueType(SnmpValueTypeEnum.NUMERIC);

        when(oidRepo.findByVendorAndDeviceType(v, dt)).thenReturn(List.of(d));

        ResolvedOid res = resolver.resolve(v, dt, "cpu");
        assertNotNull(res);
        assertEquals(".1.2.3", res.getOid());
        assertEquals(SnmpValueTypeEnum.NUMERIC, res.getValueType());
    }

    @Test
    public void resolveReturnsNullWhenNoDefs() {
        Vendor v = new Vendor(); v.setId(1L);
        DeviceType dt = new DeviceType(); dt.setId(2L);
        when(oidRepo.findByVendorAndDeviceType(v, dt)).thenReturn(List.of());
        assertNull(resolver.resolve(v, dt, "any"));
    }

    @Test
    public void resolveFiltersByMetricKey() {
        Vendor v = new Vendor(); v.setId(1L);
        DeviceType dt = new DeviceType(); dt.setId(2L);
        OidDefinition a = new OidDefinition(); a.setMetricKey("m1"); a.setOid(".1"); a.setValueType(SnmpValueTypeEnum.STRING);
        OidDefinition b = new OidDefinition(); b.setMetricKey("m2"); b.setOid(".2"); b.setValueType(SnmpValueTypeEnum.NUMERIC);
        when(oidRepo.findByVendorAndDeviceType(v, dt)).thenReturn(List.of(a, b));
        var r = resolver.resolve(v, dt, "m2");
        assertNotNull(r);
        assertEquals(".2", r.getOid());
    }
}
