package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.util.List;
import java.util.Map;

public interface SnmpService {
    Map<String, String> probe(String host, int port, String community, int timeoutMs) throws Exception;
    Map<String, String> getByOids(String host, int port, String community, int timeoutMs, List<String> oids) throws Exception;
}
