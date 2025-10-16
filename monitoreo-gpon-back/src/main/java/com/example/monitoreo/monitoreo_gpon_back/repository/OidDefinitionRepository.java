package com.example.monitoreo.monitoreo_gpon_back.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.monitoreo.monitoreo_gpon_back.model.OidDefinition;
import com.example.monitoreo.monitoreo_gpon_back.model.Vendor;
import com.example.monitoreo.monitoreo_gpon_back.model.DeviceType;

public interface OidDefinitionRepository extends JpaRepository<OidDefinition, Long> {
    List<OidDefinition> findByVendorAndDeviceType(Vendor vendor, DeviceType deviceType);
}
