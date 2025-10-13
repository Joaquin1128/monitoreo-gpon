package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.util.Map;

public interface SnmpService {
    Map<String, String> probe(String host, int port, String community, int timeoutMs) throws Exception;
}
