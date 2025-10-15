package com.example.monitoreo.monitoreo_gpon_back.snmp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@ConditionalOnProperty(name = "app.snmp.mock", havingValue = "true")
@Service
public class MockSnmpService implements SnmpService {

    @Override
    public Map<String, String> probe(String host, int port, String community, int timeoutMs) {
        Map<String, String> m = new HashMap<>();
        m.put("sysName", "mock-olt");
        m.put("sysDescr", "Mock GPON OLT for testing");
        m.put("sysUpTime", "123456");
        return m;
    }

    @Override
    public Map<String, String> getByOids(String host, int port, String community, int timeoutMs, java.util.List<String> oids) {
        Map<String, String> m = new HashMap<>();
        for (String oid : oids) {
            switch (oid) {
                case "1.3.6.1.2.1.1.5.0":
                    m.put(oid, "mock-olt");
                    m.put("sysName", "mock-olt");
                    break;
                case "1.3.6.1.2.1.1.3.0":
                    m.put(oid, "123456");
                    m.put("sysUpTime", "123456");
                    break;
                default:
                    m.put(oid, "");
            }
        }
        return m;
    }
}
