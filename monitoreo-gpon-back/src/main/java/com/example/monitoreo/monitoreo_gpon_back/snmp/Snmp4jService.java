package com.example.monitoreo.monitoreo_gpon_back.snmp;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "app.snmp.mock", havingValue = "false", matchIfMissing = true)
@org.springframework.stereotype.Service
public class Snmp4jService implements SnmpService {

    private static final Logger log = LoggerFactory.getLogger(Snmp4jService.class);

    private Snmp snmp;
    private TransportMapping<UdpAddress> transport;

    private static final Map<String, String> OID_TO_NAME = Map.of(
            "1.3.6.1.2.1.1.5.0", "sysName",
            "1.3.6.1.2.1.1.1.0", "sysDescr",
            "1.3.6.1.2.1.1.3.0", "sysUpTime"
    );

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
    public Map<String, String> probe(String host, int port, String community, int timeoutMs) throws Exception {
        UdpAddress targetAddress = new UdpAddress(host + "/" + port);

        CommunityTarget<UdpAddress> target = new CommunityTarget<>();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        // reduce retries to avoid long waits (retries * timeout can add up)
        target.setRetries(0);
        target.setTimeout(timeoutMs);
        target.setVersion(SnmpConstants.version2c);

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0"))); // sysName
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.1.0"))); // sysDescr
        pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.3.0"))); // sysUpTime
        pdu.setType(PDU.GET);

        ResponseEvent<UdpAddress> response = snmp.get(pdu, target);

        if (response == null || response.getResponse() == null) {
            throw new Exception("No SNMP response from target");
        }

        PDU responsePdu = response.getResponse();
        if (responsePdu.getErrorStatus() != PDU.noError) {
            throw new Exception("SNMP error: " + responsePdu.getErrorStatusText());
        }

        Map<String, String> result = new HashMap<>();
        for (VariableBinding vb : responsePdu.getVariableBindings()) {
            String oid = vb.getOid().toString();
            String name = OID_TO_NAME.getOrDefault(oid, oid);
            String val = vb.getVariable() != null ? vb.getVariable().toString() : null;
            result.put(name, val == null ? "" : val);
        }

        if (result.isEmpty() || result.values().stream().allMatch(v -> v == null || v.isEmpty())) {
            log.warn("SNMP probe empty for {}:{} (community={})", host, port, community);
            throw new Exception("SNMP probe returned empty result for " + host + ":" + port);
        }

        log.debug("SNMP probe success for {}:{} -> {}", host, port, result);
        return result;
    }
}
