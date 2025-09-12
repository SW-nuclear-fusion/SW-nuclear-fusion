package com.example.swnuclearfusionwas.domain.meds.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medications", indexes = @Index(name = "idx_med_user", columnList = "userId"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Medication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(nullable = false) private Long userId;
    @Column(nullable = false, length = 50) private String name;
    @Column(nullable = false) private Integer frequencyPerDay;

    @Column(nullable = false) @Builder.Default
    private Boolean alertEnabled = true;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedSchedule> schedules = new ArrayList<>();
}