package com.example.monitoreo.monitoreo_gpon_back.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hub")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "hub", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Olt> olts;
}

