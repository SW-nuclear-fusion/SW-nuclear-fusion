package com.example.swnuclearfusionwas.domain.plant.entity;

import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor
public class Plant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserEntity owner;

    private String nickname;
    private int level = 1;
    private int exp = 0; // 0~100
}
