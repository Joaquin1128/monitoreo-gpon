package com.example.monitoreo.monitoreo_gpon_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.monitoreo.monitoreo_gpon_back.model.DeviceType;

public interface DeviceTypeRepository extends JpaRepository<DeviceType, Long> {
    Optional<DeviceType> findByName(String name);
}
