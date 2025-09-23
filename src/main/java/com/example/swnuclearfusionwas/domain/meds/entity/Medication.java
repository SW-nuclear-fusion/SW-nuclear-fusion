package com.example.swnuclearfusionwas.domain.meds.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "Medication", description = "복용약 엔티티. 사용자가 등록한 복용약의 기본 정보와 스케줄 정보를 포함합니다.")
@Entity
@Table(name = "medications", indexes = @Index(name = "idx_med_user", columnList = "userId"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Medication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "복용약 고유 ID (자동 생성)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "복용약 소유 사용자 ID", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
    @Column(nullable = false, length = 50)
    @Schema(description = "약 이름 (최대 50자)", example = "타이레놀", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    private String name;
    @Column(nullable = false)
    @Schema(description = "하루 복용 횟수 (1 이상)", example = "3", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    private Integer frequencyPerDay;

    @Column(nullable = false) @Builder.Default
    @Schema(description = "알림 활성화 여부 (true: 알림 ON, false: 알림 OFF)", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean alertEnabled = true;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "복용 스케줄 목록 (MedSchedule 엔티티 리스트)", implementation = MedSchedule.class)
    private List<MedSchedule> schedules = new ArrayList<>();
}