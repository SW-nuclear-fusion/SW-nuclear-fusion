package com.example.swnuclearfusionwas.domain.meds.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Schema(name = "DoseEvent", description = "복용 이벤트 엔티티. 실제 복용 기록(날짜, 시각, 완료 여부 등)을 저장합니다.")
@Entity
@Table(name = "dose_events",
        uniqueConstraints = @UniqueConstraint(name = "uk_schedule_date", columnNames = {"schedule_id","date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoseEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "복용 이벤트 고유 ID (자동 생성)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "schedule_id")
    @Schema(description = "연결된 스케줄 엔티티(MedSchedule)", implementation = MedSchedule.class)
    private MedSchedule schedule;

    @Column(nullable = false)
    @Schema(description = "복용 날짜(yyyy-MM-dd)", example = "2025-09-23", requiredMode = Schema.RequiredMode.REQUIRED, format = "date")
    private java.time.LocalDate date;
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "복용 완료 여부 (true: 복용함, false: 미복용)", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean taken=false;
    @Column
    @Schema(description = "복용 완료 시각(UTC+9, ISO8601)", example = "2025-09-23T08:00:00+09:00", nullable = true, format = "date-time")
    private java.time.OffsetDateTime takenAt;
}