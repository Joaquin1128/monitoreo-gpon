package com.example.monitoreo.monitoreo_gpon_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.monitoreo.monitoreo_gpon_back.model.Olt;

public interface OltRepository extends JpaRepository<Olt, Long> {
    List<Olt> findByHubId(Long hubId);
}
