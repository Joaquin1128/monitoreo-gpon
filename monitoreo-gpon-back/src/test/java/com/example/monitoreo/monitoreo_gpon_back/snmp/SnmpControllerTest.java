package com.example.monitoreo.monitoreo_gpon_back.snmp;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
import com.example.monitoreo.monitoreo_gpon_back.model.Vendor;
import com.example.monitoreo.monitoreo_gpon_back.model.DeviceType;
import com.example.monitoreo.monitoreo_gpon_back.model.OidDefinition;
import com.example.monitoreo.monitoreo_gpon_back.model.enums.SnmpValueTypeEnum;
import com.example.monitoreo.monitoreo_gpon_back.repository.OidDefinitionRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.OltRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.OntRepository;
import com.example.monitoreo.monitoreo_gpon_back.repository.DeviceTypeRepository;

import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;

public class SnmpControllerTest {

    @Test
    public void probeOlt_noOidDefs_returnsBadRequest() throws Exception {
        OltRepository oltRepository = mock(OltRepository.class);
        OntRepository ontRepository = mock(OntRepository.class);
        SnmpService snmpService = mock(SnmpService.class);
        SnmpOidResolver resolver = mock(SnmpOidResolver.class);
        OidDefinitionRepository oidDefinitionRepository = mock(OidDefinitionRepository.class);
        DeviceTypeRepository deviceTypeRepository = mock(DeviceTypeRepository.class);

        Olt o = new Olt(); o.setId(1L); o.setIpAddress("1.2.3.4"); o.setSnmpCommunity("public");
        when(oltRepository.findById(1L)).thenReturn(java.util.Optional.of(o));
        when(oidDefinitionRepository.findByVendorAndDeviceType(any(), any())).thenReturn(List.of());

        SnmpController controller = new SnmpController(oltRepository, ontRepository, snmpService, resolver, oidDefinitionRepository, deviceTypeRepository);
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(post("/api/snmp/olts/1/probe").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void probeOlt_happyPath_returnsOk() throws Exception {
        OltRepository oltRepository = mock(OltRepository.class);
        OntRepository ontRepository = mock(OntRepository.class);
        SnmpService snmpService = mock(SnmpService.class);
        SnmpOidResolver resolver = mock(SnmpOidResolver.class);
        OidDefinitionRepository oidDefinitionRepository = mock(OidDefinitionRepository.class);
        DeviceTypeRepository deviceTypeRepository = mock(DeviceTypeRepository.class);

        Vendor v = new Vendor(); v.setId(2L);
        DeviceType dt = new DeviceType(); dt.setId(3L);
    Olt o = new Olt(); o.setId(1L); o.setIpAddress("1.2.3.4"); o.setSnmpCommunity("public"); o.setVendor(v); o.setDeviceType(dt);
    o.setSnmpPort(161); o.setSnmpTimeoutMs(2000);
        OidDefinition d = new OidDefinition(); d.setMetricKey("m"); d.setOid(".1.2"); d.setValueType(SnmpValueTypeEnum.STRING);

        when(oltRepository.findById(1L)).thenReturn(java.util.Optional.of(o));
        when(oidDefinitionRepository.findByVendorAndDeviceType(v, dt)).thenReturn(List.of(d));
        when(resolver.resolve(v, dt, "m")).thenReturn(new SnmpOidResolver.ResolvedOid(".1.2", SnmpValueTypeEnum.STRING, "OID_DEFINITION"));
        when(snmpService.getByOids(eq("1.2.3.4"), anyInt(), eq("public"), anyInt(), anyList())).thenReturn(Map.of(".1.2", (Variable)new OctetString("ok")));

        SnmpController controller = new SnmpController(oltRepository, ontRepository, snmpService, resolver, oidDefinitionRepository, deviceTypeRepository);
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();

        mvc.perform(post("/api/snmp/olts/1/probe").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
}
