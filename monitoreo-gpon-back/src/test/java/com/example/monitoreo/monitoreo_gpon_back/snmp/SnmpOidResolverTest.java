package com.example.monitoreo.monitoreo_gpon_back.snmp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.example.monitoreo.monitoreo_gpon_back.model.OidDefinition;
import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
import com.example.monitoreo.monitoreo_gpon_back.model.Vendor;
import com.example.monitoreo.monitoreo_gpon_back.model.DeviceType;
import com.example.monitoreo.monitoreo_gpon_back.model.enums.SnmpValueTypeEnum;
import com.example.monitoreo.monitoreo_gpon_back.repository.OidDefinitionRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.OltRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.OntRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.DeviceTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SnmpOidResolverTest {

    private OidDefinitionRepository oidRepo;
    private DeviceTypeRepository deviceTypeRepo;
    private OltRepository oltRepo;
    private OntRepository ontRepo;
    private SnmpOidResolver resolver;

    @BeforeEach
    public void setup() {
        oidRepo = Mockito.mock(OidDefinitionRepository.class);
        deviceTypeRepo = Mockito.mock(DeviceTypeRepository.class);
        oltRepo = Mockito.mock(OltRepository.class);
        ontRepo = Mockito.mock(OntRepository.class);
        resolver = new SnmpOidResolver(deviceTypeRepo, oidRepo, oltRepo, ontRepo);
    }

    @Test
    public void resolveForDeviceReturnsResolvedOid() {
        Vendor v = new Vendor(); v.setId(1L); v.setName("ACME");
        DeviceType dt = new DeviceType(); dt.setId(2L); dt.setName("OLT");
        OidDefinition d = new OidDefinition(); d.setId(10L); d.setMetricKey("cpu"); d.setOid(".1.2.3"); d.setValueType(SnmpValueTypeEnum.NUMERIC);

    when(oidRepo.findByVendorAndDeviceType(v, dt)).thenReturn(List.of(d));
    when(deviceTypeRepo.findByName("OLT")).thenReturn(Optional.of(dt));
    when(oltRepo.findById(5L)).thenReturn(Optional.of(new Olt(){ { setId(5L); setVendor(v); setDeviceType(dt); } }));

    var res = resolver.resolve("OLT", 5L, "cpu", java.util.Map.of());
    assertNotNull(res);
    assertEquals(".1.2.3", res.getOid());
    assertEquals(SnmpValueTypeEnum.NUMERIC, res.getValueType());
    }
}
