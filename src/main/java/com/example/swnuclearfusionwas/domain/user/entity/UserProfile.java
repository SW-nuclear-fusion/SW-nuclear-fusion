package com.example.swnuclearfusionwas.domain.user.entity;

import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity @Getter @Setter @NoArgsConstructor
public class UserProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    private int level = 1;
    private int points = 0;
    private int seedsOwned = 0;
    private LocalDate joinedAt = LocalDate.now();
}
