// package com.example.monitoreo.monitoreo_gpon_back.snmp;

// import static org.hamcrest.Matchers.hasSize;
// import static org.hamcrest.Matchers.startsWith;
// import static org.hamcrest.Matchers.nullValue;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyInt;
// import static org.mockito.ArgumentMatchers.anyList;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// import java.util.List;
// import java.util.Map;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
// import org.snmp4j.smi.OctetString;
// import org.snmp4j.smi.Variable;

// import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
// import com.example.monitoreo.monitoreo_gpon_back.model.Vendor;
// import com.example.monitoreo.monitoreo_gpon_back.model.DeviceType;
// import com.example.monitoreo.monitoreo_gpon_back.model.Ont;
// import com.example.monitoreo.monitoreo_gpon_back.model.IDevice;
// import com.example.monitoreo.monitoreo_gpon_back.model.OidDefinition;
// import com.example.monitoreo.monitoreo_gpon_back.model.enums.SnmpValueTypeEnum;
// import com.example.monitoreo.monitoreo_gpon_back.repository.OidDefinitionRepository;
// import com.example.monitoreo.monitoreo_gpon_back.repository.OltRepository;
// import com.example.monitoreo.monitoreo_gpon_back.repository.OntRepository;
// import com.example.monitoreo.monitoreo_gpon_back.repository.DeviceTypeRepository;

// public class SnmpControllerTest {

//     private OltRepository oltRepository;
//     private OntRepository ontRepository;
//     private SnmpService snmpService;
//     private SnmpOidResolver resolver;
//     private OidDefinitionRepository oidDefinitionRepository;
//     private DeviceTypeRepository deviceTypeRepository;

//     private MockMvc mvc;

//     private Olt testOlt;
//     private Vendor testVendor;
//     private DeviceType testDeviceType;
//     private OidDefinition testOid;

//     @BeforeEach
//     public void setUp() {
//         oltRepository = mock(OltRepository.class);
//         ontRepository = mock(OntRepository.class);
//         snmpService = mock(SnmpService.class);
//         resolver = mock(SnmpOidResolver.class);
//         oidDefinitionRepository = mock(OidDefinitionRepository.class);
//         deviceTypeRepository = mock(DeviceTypeRepository.class);

//         testVendor = new Vendor(); testVendor.setId(2L);
//         testDeviceType = new DeviceType(); testDeviceType.setId(3L);
//         testOlt = new Olt();
//         testOlt.setId(1L);
//         testOlt.setIpAddress("1.2.3.4");
//         testOlt.setSnmpCommunity("public");
//         testOlt.setVendor(testVendor);
//         testOlt.setDeviceType(testDeviceType);
//         testOlt.setSnmpPort(161);
//         testOlt.setSnmpTimeoutMs(2000);

//         testOid = new OidDefinition();
//         testOid.setMetricKey("m");
//         testOid.setOid(".1.2");
//         testOid.setValueType(SnmpValueTypeEnum.STRING);

//         SnmpController controller = new SnmpController(
//             oltRepository, ontRepository, snmpService, resolver, oidDefinitionRepository, deviceTypeRepository
//         );
//         mvc = MockMvcBuilders.standaloneSetup(controller).build();
//     }

//     @Test
//     public void probeOlt_noOidDefs_returnsBadRequest() throws Exception {
//         when(oltRepository.findById(1L)).thenReturn(java.util.Optional.of(testOlt));
//         when(oidDefinitionRepository.findByVendorAndDeviceType(any(), any())).thenReturn(List.of());

//         mvc.perform(post("/api/snmp/olts/1/probe").contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isBadRequest());
//     }

//     @Test
//     public void probeOlt_happyPath_returnsOk() throws Exception {
//         when(oltRepository.findById(1L)).thenReturn(java.util.Optional.of(testOlt));
//         when(oidDefinitionRepository.findByVendorAndDeviceType(testVendor, testDeviceType)).thenReturn(List.of(testOid));
//         when(resolver.resolve((IDevice)any(), eq("m"))).thenReturn(new SnmpOidResolver.ResolvedOid(".1.2", SnmpValueTypeEnum.STRING, "OID_DEFINITION"));
//         when(snmpService.getByOids(eq("1.2.3.4"), anyInt(), eq("public"), anyInt(), anyList()))
//             .thenReturn(Map.of(".1.2", (Variable)new OctetString("ok")));

//         mvc.perform(post("/api/snmp/olts/1/probe").contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$", hasSize(1)))
//            .andExpect(jsonPath("$[0].metricKey").value("m"))
//            .andExpect(jsonPath("$[0].value").value("ok"))
//            .andExpect(jsonPath("$[0].parsedValue").value("ok"))
//            .andExpect(jsonPath("$[0].valueType").value("STRING"))
//            .andExpect(jsonPath("$[0].source").value("OID_DEFINITION"));
//     }

//     @Test
//     public void probeOlt_snmpServiceThrows_resultsContainErrorEntries() throws Exception {
//         when(oltRepository.findById(1L)).thenReturn(java.util.Optional.of(testOlt));
//         when(oidDefinitionRepository.findByVendorAndDeviceType(testVendor, testDeviceType)).thenReturn(List.of(testOid));
//         when(resolver.resolve((IDevice)any(), eq("m"))).thenReturn(new SnmpOidResolver.ResolvedOid(".1.2", SnmpValueTypeEnum.STRING, "OID_DEFINITION"));
//         when(snmpService.getByOids(eq("1.2.3.4"), anyInt(), eq("public"), anyInt(), anyList()))
//             .thenThrow(new RuntimeException("snmp-fail"));

//         mvc.perform(post("/api/snmp/olts/1/probe").contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$", hasSize(1)))
//            .andExpect(jsonPath("$[0].metricKey").value("m"))
//            .andExpect(jsonPath("$[0].source", startsWith("ERROR")));
//     }

//     @Test
//     public void probeOlt_resolverReturnsNull_oidNotFoundResult() throws Exception {
//         when(oltRepository.findById(1L)).thenReturn(java.util.Optional.of(testOlt));
//         when(oidDefinitionRepository.findByVendorAndDeviceType(testVendor, testDeviceType)).thenReturn(List.of(testOid));
//         when(resolver.resolve((IDevice)any(), eq("m"))).thenReturn(null);

//         mvc.perform(post("/api/snmp/olts/1/probe").contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$", hasSize(1)))
//            .andExpect(jsonPath("$[0].metricKey").value("m"))
//            .andExpect(jsonPath("$[0].source").value("OID_NOT_FOUND"));
//     }

//     @Test
//     public void probeOnt_fallbackToOltVendorDeviceType() throws Exception {
//         OltRepository oltRepository = mock(OltRepository.class);
//         OntRepository ontRepository = mock(OntRepository.class);
//         SnmpService snmpService = mock(SnmpService.class);
//         SnmpOidResolver resolver = mock(SnmpOidResolver.class);
//         OidDefinitionRepository oidDefinitionRepository = mock(OidDefinitionRepository.class);
//         DeviceTypeRepository deviceTypeRepository = mock(DeviceTypeRepository.class);

//         Vendor v = new Vendor(); v.setId(2L);
//         DeviceType dt = new DeviceType(); dt.setId(3L);
//         Olt o = new Olt(); o.setId(10L); o.setIpAddress("1.2.3.4"); o.setSnmpCommunity("public"); o.setVendor(v); o.setDeviceType(dt);
//         o.setSnmpPort(161); o.setSnmpTimeoutMs(2000);
//         Ont ont = new Ont(); ont.setId(5L); ont.setOlt(o); // ont has no vendor/deviceType

//         OidDefinition d = new OidDefinition(); d.setMetricKey("m"); d.setOid(".1.2"); d.setValueType(SnmpValueTypeEnum.STRING);

//         when(oltRepository.findById(10L)).thenReturn(java.util.Optional.of(o));
//         when(ontRepository.findById(5L)).thenReturn(java.util.Optional.of(ont));
//         when(deviceTypeRepository.findByName("ONT")).thenReturn(java.util.Optional.of(dt));
//         when(oidDefinitionRepository.findByVendorAndDeviceType(v, dt)).thenReturn(List.of(d));
//         when(resolver.resolve((com.example.monitoreo.monitoreo_gpon_back.model.IDevice) any(), eq("m"))).thenReturn(new SnmpOidResolver.ResolvedOid(".1.2", SnmpValueTypeEnum.STRING, "OID_DEFINITION"));
//         when(snmpService.getByOids(eq("1.2.3.4"), anyInt(), eq("public"), anyInt(), anyList())).thenReturn(Map.of(".1.2", (org.snmp4j.smi.Variable)new org.snmp4j.smi.OctetString("ok")));

//         SnmpController controller = new SnmpController(oltRepository, ontRepository, snmpService, resolver, oidDefinitionRepository, deviceTypeRepository);
//         var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();

//         mvc.perform(post("/api/snmp/onts/5/probe").contentType(MediaType.APPLICATION_JSON))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$", hasSize(1)))
//             .andExpect(jsonPath("$[0].metricKey").value("m"))
//             .andExpect(jsonPath("$[0].value").value("ok"));
//     }

//     @Test
//     public void probeOlt_mixedOidResults_oneNullOnePresent() throws Exception {
//         OltRepository oltRepository = mock(OltRepository.class);
//         OntRepository ontRepository = mock(OntRepository.class);
//         SnmpService snmpService = mock(SnmpService.class);
//         SnmpOidResolver resolver = mock(SnmpOidResolver.class);
//         OidDefinitionRepository oidDefinitionRepository = mock(OidDefinitionRepository.class);
//         DeviceTypeRepository deviceTypeRepository = mock(DeviceTypeRepository.class);

//         Vendor v = new Vendor(); v.setId(2L);
//         DeviceType dt = new DeviceType(); dt.setId(3L);
//         Olt o = new Olt(); o.setId(1L); o.setIpAddress("1.2.3.4"); o.setSnmpCommunity("public"); o.setVendor(v); o.setDeviceType(dt);
//         o.setSnmpPort(161); o.setSnmpTimeoutMs(2000);

//         OidDefinition d1 = new OidDefinition(); d1.setMetricKey("m1"); d1.setOid(".1"); d1.setValueType(SnmpValueTypeEnum.STRING);
//         OidDefinition d2 = new OidDefinition(); d2.setMetricKey("m2"); d2.setOid(".2"); d2.setValueType(SnmpValueTypeEnum.STRING);

//         when(oltRepository.findById(1L)).thenReturn(java.util.Optional.of(o));
//         when(oidDefinitionRepository.findByVendorAndDeviceType(v, dt)).thenReturn(List.of(d1, d2));
//         when(resolver.resolve((com.example.monitoreo.monitoreo_gpon_back.model.IDevice) any(), eq("m1"))).thenReturn(new SnmpOidResolver.ResolvedOid(".1", SnmpValueTypeEnum.STRING, "OID_DEFINITION"));
//         when(resolver.resolve((com.example.monitoreo.monitoreo_gpon_back.model.IDevice) any(), eq("m2"))).thenReturn(new SnmpOidResolver.ResolvedOid(".2", SnmpValueTypeEnum.STRING, "OID_DEFINITION"));

//         Map<String, Variable> respMap = new java.util.HashMap<>();
//         respMap.put(".1", new org.snmp4j.smi.OctetString("v1"));
//         respMap.put(".2", null);
//         when(snmpService.getByOids(eq("1.2.3.4"), anyInt(), eq("public"), anyInt(), anyList()))
//             .thenReturn(respMap);

//         SnmpController controller = new SnmpController(oltRepository, ontRepository, snmpService, resolver, oidDefinitionRepository, deviceTypeRepository);
//         var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controller).build();

//         mvc.perform(post("/api/snmp/olts/1/probe").contentType(MediaType.APPLICATION_JSON))
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$", hasSize(2)))
//             .andExpect(jsonPath("$[0].metricKey").value("m1"))
//             .andExpect(jsonPath("$[0].value").value("v1"))
//             .andExpect(jsonPath("$[1].metricKey").value("m2"))
//             .andExpect(jsonPath("$[1].value").value(nullValue()));
//     }
// }
