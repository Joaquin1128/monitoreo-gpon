package com.example.monitoreo.monitoreo_gpon_back.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "hub", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Olt> olts;

    public Hub() {}

    public Hub(String name) { this.name = name; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Olt> getOlts() { return olts; }
    public void setOlts(List<Olt> olts) { this.olts = olts; }
}
