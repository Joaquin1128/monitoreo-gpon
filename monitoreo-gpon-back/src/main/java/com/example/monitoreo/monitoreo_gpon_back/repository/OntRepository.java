package com.example.monitoreo.monitoreo_gpon_back.repository;

import com.example.monitoreo.monitoreo_gpon_back.model.Ont;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OntRepository extends JpaRepository<Ont, Long> {
    List<Ont> findByOltId(Long oltId);
}
