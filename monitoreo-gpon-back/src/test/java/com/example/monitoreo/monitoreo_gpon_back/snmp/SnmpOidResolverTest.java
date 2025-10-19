// package com.example.monitoreo.monitoreo_gpon_back.snmp;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// import java.util.List;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import com.example.monitoreo.monitoreo_gpon_back.model.OidDefinition;
// import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
// import com.example.monitoreo.monitoreo_gpon_back.model.Vendor;
// import com.example.monitoreo.monitoreo_gpon_back.model.DeviceType;
// import com.example.monitoreo.monitoreo_gpon_back.repository.OidDefinitionRepository;
// import com.example.monitoreo.monitoreo_gpon_back.snmp.SnmpOidResolver.ResolvedOid;

// public class SnmpOidResolverTest {

//     private OidDefinitionRepository oidRepo;
//     private SnmpOidResolver resolver;

//     @BeforeEach
//     public void setup() {
//         oidRepo = mock(OidDefinitionRepository.class);
//         resolver = new SnmpOidResolver(oidRepo);
//     }

//     @Test
//     public void resolveForDeviceReturnsResolvedOid() {
//         Vendor vendor = new Vendor(); vendor.setId(1L); vendor.setName("ACME");
//         DeviceType deviceType = new DeviceType(); deviceType.setId(2L); deviceType.setName("OLT");

//         OidDefinition oidDef = new OidDefinition();
//         oidDef.setId(10L);
//         oidDef.setMetricKey("cpu");
//         oidDef.setOid(".1.2.3");
//         oidDef.setValueType(SnmpValueType.NUMERIC);

//         when(oidRepo.findByVendorAndDeviceType(vendor, deviceType)).thenReturn(List.of(oidDef));

//         Olt oltDevice = new Olt();
//         oltDevice.setVendor(vendor);
//         oltDevice.setDeviceType(deviceType);

//         ResolvedOid result = resolver.resolve(oltDevice, "cpu");
//         assertNotNull(result);
//         assertEquals(".1.2.3", result.getOid());
//         assertEquals(SnmpValueType.NUMERIC, result.getValueType());

//         verify(oidRepo).findByVendorAndDeviceType(vendor, deviceType);
//     }

//     @Test
//     public void resolveReturnsNullWhenNoDefs() {
//         Vendor vendor = new Vendor(); vendor.setId(1L);
//         DeviceType deviceType = new DeviceType(); deviceType.setId(2L);

//         when(oidRepo.findByVendorAndDeviceType(vendor, deviceType)).thenReturn(List.of());

//         Olt oltDevice = new Olt();
//         oltDevice.setVendor(vendor);
//         oltDevice.setDeviceType(deviceType);

//         assertNull(resolver.resolve(oltDevice, "any"));

//         verify(oidRepo).findByVendorAndDeviceType(vendor, deviceType);
//     }

//     @Test
//     public void resolveFiltersByMetricKey() {
//         Vendor vendor = new Vendor(); vendor.setId(1L);
//         DeviceType deviceType = new DeviceType(); deviceType.setId(2L);

//         OidDefinition oid1 = new OidDefinition();
//         oid1.setMetricKey("m1"); oid1.setOid(".1"); oid1.setValueType(SnmpValueType.STRING);

//         OidDefinition oid2 = new OidDefinition();
//         oid2.setMetricKey("m2"); oid2.setOid(".2"); oid2.setValueType(SnmpValueType.NUMERIC);

//         when(oidRepo.findByVendorAndDeviceType(vendor, deviceType)).thenReturn(List.of(oid1, oid2));

//         Olt oltDevice = new Olt();
//         oltDevice.setVendor(vendor);
//         oltDevice.setDeviceType(deviceType);

//         ResolvedOid result = resolver.resolve(oltDevice, "m2");
//         assertNotNull(result);
//         assertEquals(".2", result.getOid());
//         assertEquals(SnmpValueType.NUMERIC, result.getValueType());

//         verify(oidRepo).findByVendorAndDeviceType(vendor, deviceType);
//     }

//     @Test
//     public void resolveWithDeviceObject() {
//         Vendor vendor = new Vendor(); vendor.setId(5L); vendor.setName("Z");
//         DeviceType deviceType = new DeviceType(); deviceType.setId(6L); deviceType.setName("ONT");

//         OidDefinition oidDef = new OidDefinition();
//         oidDef.setMetricKey("m"); oidDef.setOid(".9"); oidDef.setValueType(SnmpValueType.STRING);

//         when(oidRepo.findByVendorAndDeviceType(vendor, deviceType)).thenReturn(List.of(oidDef));

//         Olt oltDevice = new Olt();
//         oltDevice.setVendor(vendor);
//         oltDevice.setDeviceType(deviceType);

//         ResolvedOid result = resolver.resolve(oltDevice, "m");
//         assertNotNull(result);
//         assertEquals(".9", result.getOid());
//         assertEquals(SnmpValueType.STRING, result.getValueType());

//         verify(oidRepo).findByVendorAndDeviceType(vendor, deviceType);
//     }
// }
