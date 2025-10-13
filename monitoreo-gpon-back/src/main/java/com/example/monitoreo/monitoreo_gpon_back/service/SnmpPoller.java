package com.example.monitoreo.monitoreo_gpon_back.service;

import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
import com.example.monitoreo.monitoreo_gpon_back.repository.OltRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnmpPoller {

    private static final Logger log = LoggerFactory.getLogger(SnmpPoller.class);

    private final OltRepository oltRepository;
    private final MetricService metricService;
    private final com.example.monitoreo.monitoreo_gpon_back.snmp.SnmpService snmpService;

    public SnmpPoller(OltRepository oltRepository, MetricService metricService, com.example.monitoreo.monitoreo_gpon_back.snmp.SnmpService snmpService) {
        this.oltRepository = oltRepository;
        this.metricService = metricService;
        this.snmpService = snmpService;
    }

    @Value("${app.snmp.poll-interval-ms:60000}")
    private long pollIntervalMs;

    @Scheduled(fixedDelayString = "${app.snmp.poll-interval-ms:60000}")
    public void poll() {
        try {
            List<Olt> olts = oltRepository.findAll();
            for (Olt olt : olts) {
                try {
                    var res = snmpService.probe(olt.getIpAddress(), olt.getSnmpPort(), olt.getSnmpCommunity(), olt.getSnmpTimeoutMs());
                    // save uptime if present
                    if (res.containsKey("sysUpTime")) {
                        try {
                            double up = Double.parseDouble(res.get("sysUpTime"));
                            metricService.saveNumeric(olt, "sysUpTime", up);
                        } catch (NumberFormatException ex) {
                            // ignore parsing
                        }
                    }
                    if (res.containsKey("sysName")) {
                        metricService.saveString(olt, "sysName", res.get("sysName"));
                    }
                } catch (Exception e) {
                    log.warn("Failed to probe OLT {}: {}", olt.getIpAddress(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("SnmpPoller unexpected error: {}", e.getMessage(), e);
        }
    }
}
