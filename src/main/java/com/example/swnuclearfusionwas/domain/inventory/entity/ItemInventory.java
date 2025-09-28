package com.example.swnuclearfusionwas.domain.inventory.entity;

import com.example.swnuclearfusionwas.domain.catalog.model.ItemType;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class ItemInventory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Column(nullable = false)
    private int quantity = 0;

    @Version
    private Long version;
}