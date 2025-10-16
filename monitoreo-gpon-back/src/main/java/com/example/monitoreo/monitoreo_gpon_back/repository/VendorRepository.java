package com.example.monitoreo.monitoreo_gpon_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.monitoreo.monitoreo_gpon_back.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByName(String name);
}
