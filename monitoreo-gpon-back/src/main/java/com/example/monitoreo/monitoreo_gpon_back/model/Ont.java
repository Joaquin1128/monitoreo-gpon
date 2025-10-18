package com.example.monitoreo.monitoreo_gpon_back.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ont")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ont implements IDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(optional = false)
    @JoinColumn(name = "olt_id")
    private Olt olt;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "device_type_id")
    private DeviceType deviceType;

    @Column(name = "id_cliente")
    private String idCliente;

    @Column(name = "mac_addr")
    private String macAddr;

    @Column(name = "sn_ont")
    private String snOnt;

    private String model;
    private String tecnologia;

    @Column(name = "soft_version")
    private String softVersion;

    private String status;

    @Column(name = "box_name")
    private String boxName;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "cod_olt")
    private String codOlt;

    @Column(name = "fecha_act")
    private LocalDate fechaAct;
}
