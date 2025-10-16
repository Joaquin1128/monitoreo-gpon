package com.example.monitoreo.monitoreo_gpon_back.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "port")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Port {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "olt_id", nullable = false, foreignKey = @ForeignKey(name = "fk_port_olt"))
    private Olt olt;

    @Column(nullable = false)
    private Integer slot;

    @Column(name = "port_number", nullable = false)
    private Integer portNumber;

    @Column(length = 255)
    private String description;

    @Column(length = 50)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
