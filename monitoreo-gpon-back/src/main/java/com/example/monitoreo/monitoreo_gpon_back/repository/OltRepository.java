package com.example.monitoreo.monitoreo_gpon_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.monitoreo.monitoreo_gpon_back.model.Olt;

public interface OltRepository extends JpaRepository<Olt, Long> {
}
