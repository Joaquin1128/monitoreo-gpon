package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.util.List;
import java.util.Map;

import org.snmp4j.smi.Variable;

public interface SnmpService {
    Map<String, Variable> getByOids(String host, int port, String community, int timeoutMs, List<String> oids) throws Exception;
}
