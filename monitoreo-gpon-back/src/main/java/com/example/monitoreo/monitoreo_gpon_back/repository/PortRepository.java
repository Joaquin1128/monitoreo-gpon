package com.example.monitoreo.monitoreo_gpon_back.repository;

import com.example.monitoreo.monitoreo_gpon_back.model.Port;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortRepository extends JpaRepository<Port, Long> {
    List<Port> findByOltId(Long oltId);
}
