package com.example.swnuclearfusionwas.domain.meds.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
@Schema(name = "MedSchedule", description = "복용 스케줄 엔티티. 복용약(Medication)별로 요일, 시간, 활성화 여부를 관리합니다.")
@Entity
@Table(
        name = "med_schedules",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_med_time_day_active",
                columnNames = {"med_id", "time", "day_of_week", "active"}
        )
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "스케줄 고유 ID (자동 생성)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "med_id")
    @Schema(description = "연결된 복용약 엔티티(Medication)", implementation = Medication.class)
    private Medication medication;

    @Column(nullable = false)
    @Schema(description = "복용 시간(HH:mm, 24시간제, 예: 08:00)", example = "08:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private java.time.LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    @Schema(description = "복용 요일(MONDAY~SUNDAY)", example = "MONDAY", requiredMode = Schema.RequiredMode.REQUIRED)
    private java.time.DayOfWeek dayOfWeek;

    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "스케줄 활성화 여부 (true: 활성, false: 비활성)", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean active = true;
}