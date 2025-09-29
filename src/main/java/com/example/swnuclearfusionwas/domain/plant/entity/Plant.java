package com.example.swnuclearfusionwas.domain.plant.entity;

import com.example.swnuclearfusionwas.domain.catalog.model.PlantState;
import com.example.swnuclearfusionwas.domain.catalog.model.SeedType;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Plant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserEntity owner;

    @Enumerated(EnumType.STRING)
    private SeedType seedType;

    @Enumerated(EnumType.STRING)
    private PlantState state = PlantState.ACTIVE;

    private String nickname;
    private int level = 1;
    private int exp = 0; // 0~99
    private LocalDateTime createdAt = LocalDateTime.now();
}