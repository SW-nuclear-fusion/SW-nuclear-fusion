package com.example.swnuclearfusionwas.domain.inventory.entity;

import com.example.swnuclearfusionwas.domain.catalog.model.SeedType;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class SeedInventory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private SeedType type;

    @Column(nullable = false)
    private int quantity = 0;

    @Version
    private Long version; // 동시성 보호(선택)
}