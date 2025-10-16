package com.example.monitoreo.monitoreo_gpon_back.snmp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class Snmp4jService implements SnmpService {

    private static final Logger log = LoggerFactory.getLogger(Snmp4jService.class);

    private Snmp snmp;
    private TransportMapping<UdpAddress> transport;

    @PostConstruct
    public void init() throws Exception {
        transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    @PreDestroy
    public void shutdown() throws Exception {
        if (snmp != null) snmp.close();
        if (transport != null) transport.close();
    }

    @Override
    public Map<String, Variable> getByOids(String host, int port, String community, int timeoutMs, java.util.List<String> oids) throws Exception {
        UdpAddress targetAddress = new UdpAddress(host + "/" + port);

        CommunityTarget<UdpAddress> target = new CommunityTarget<>();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setRetries(0);
        target.setTimeout(timeoutMs);
        target.setVersion(SnmpConstants.version2c);

        PDU pdu = new PDU();
        for (String oid : oids) {
            pdu.add(new VariableBinding(new OID(oid)));
        }
        pdu.setType(PDU.GET);

        ResponseEvent<UdpAddress> response = snmp.get(pdu, target);
        if (response == null || response.getResponse() == null) {
            throw new Exception("No SNMP response from target");
        }

        PDU responsePdu = response.getResponse();
        if (responsePdu.getErrorStatus() != PDU.noError) {
            throw new Exception("SNMP error: " + responsePdu.getErrorStatusText());
        }

        Map<String, Variable> result = new HashMap<>();
        for (VariableBinding vb : responsePdu.getVariableBindings()) {
            String oid = vb.getOid().toString();
            Variable var = vb.getVariable();
            result.put(oid, var);
        }

        if (result.isEmpty() || result.values().stream().allMatch(v -> v == null)) {
            log.warn("SNMP getByOids empty for {}:{} (community={})", host, port, community);
            throw new Exception("SNMP getByOids returned empty result for " + host + ":" + port);
        }

        return result;
    }
}
