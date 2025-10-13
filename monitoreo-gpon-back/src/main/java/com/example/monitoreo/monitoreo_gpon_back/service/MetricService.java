package com.example.monitoreo.monitoreo_gpon_back.service;

import com.example.monitoreo.monitoreo_gpon_back.model.Metric;
import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
import com.example.monitoreo.monitoreo_gpon_back.repository.MetricRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class MetricService {

    private final MetricRepository metricRepository;

    public MetricService(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    public Metric saveNumeric(Olt olt, String metricType, Double value) {
        Metric m = new Metric();
        m.setOlt(olt);
        m.setMetricType(metricType);
        m.setValue(value);
        m.setCollectedAt(OffsetDateTime.now());
        return metricRepository.save(m);
    }

    public Metric saveString(Olt olt, String metricType, String value) {
        Metric m = new Metric();
        m.setOlt(olt);
        m.setMetricType(metricType);
        m.setStringValue(value);
        m.setCollectedAt(OffsetDateTime.now());
        return metricRepository.save(m);
    }

    public List<Metric> findLatestByOlt(Long oltId) {
        return metricRepository.findTop50ByOltIdOrderByCollectedAtDesc(oltId);
    }
}
