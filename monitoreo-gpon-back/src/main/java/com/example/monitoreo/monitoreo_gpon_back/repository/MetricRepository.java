package com.example.monitoreo.monitoreo_gpon_back.repository;

import com.example.monitoreo.monitoreo_gpon_back.model.Metric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetricRepository extends JpaRepository<Metric, Long> {
}
