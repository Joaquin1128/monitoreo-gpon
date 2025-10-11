package com.example.monitoreo.monitoreo_gpon_back.repository;

import com.example.monitoreo.monitoreo_gpon_back.model.Olt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OltRepository extends JpaRepository<Olt, Long> {
    List<Olt> findByHubId(Long hubId);
}
