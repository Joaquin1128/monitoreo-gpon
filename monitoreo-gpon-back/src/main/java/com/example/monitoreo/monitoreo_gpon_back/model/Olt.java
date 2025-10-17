package com.example.monitoreo.monitoreo_gpon_back.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "olt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Olt implements IDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hub_id")
    private Hub hub;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "device_type_id")
    private DeviceType deviceType;

    private String name;

    @Column(name = "ip_address")
    private String ipAddress;

    private String serialNumber;
    private String model;
    private Integer cantPorts;

    @Column(name = "snmp_version")
    private String snmpVersion;

    @Column(name = "snmp_community")
    private String snmpCommunity;

    @Column(name = "snmp_port")
    private Integer snmpPort;

    @Column(name = "snmp_timeout_ms")
    private Integer snmpTimeoutMs;

    @Column(name = "soft_version")
    private String softVersion;

    @Column(name = "command_protection_password")
    private String commandProtectionPassword;

    @OneToMany(mappedBy = "olt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ont> onts;
}
