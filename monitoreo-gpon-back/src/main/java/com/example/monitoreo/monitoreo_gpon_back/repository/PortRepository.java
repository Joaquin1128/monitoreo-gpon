package com.example.monitoreo.monitoreo_gpon_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.monitoreo.monitoreo_gpon_back.model.Port;

public interface PortRepository extends JpaRepository<Port, Long> {
    List<Port> findByOltId(Long oltId);
}
