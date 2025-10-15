package com.example.monitoreo.monitoreo_gpon_back.repository;

import com.example.monitoreo.monitoreo_gpon_back.model.SnmpOid;
import com.example.monitoreo.monitoreo_gpon_back.model.SnmpProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SnmpOidRepository extends JpaRepository<SnmpOid, Long> {
    List<SnmpOid> findByProfile(SnmpProfile profile);
}
