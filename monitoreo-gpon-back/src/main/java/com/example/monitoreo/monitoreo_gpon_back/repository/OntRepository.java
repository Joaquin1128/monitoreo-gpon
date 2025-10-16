package com.example.monitoreo.monitoreo_gpon_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.monitoreo.monitoreo_gpon_back.model.Ont;

public interface OntRepository extends JpaRepository<Ont, Long> {
    List<Ont> findByOltId(Long oltId);
}
