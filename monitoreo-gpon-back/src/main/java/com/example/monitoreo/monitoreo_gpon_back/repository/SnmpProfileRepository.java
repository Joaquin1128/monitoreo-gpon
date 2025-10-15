package com.example.monitoreo.monitoreo_gpon_back.repository;

import com.example.monitoreo.monitoreo_gpon_back.model.SnmpProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SnmpProfileRepository extends JpaRepository<SnmpProfile, Long> {
    Optional<SnmpProfile> findByName(String name);
}
