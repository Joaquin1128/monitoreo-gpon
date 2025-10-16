package com.example.monitoreo.monitoreo_gpon_back.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
