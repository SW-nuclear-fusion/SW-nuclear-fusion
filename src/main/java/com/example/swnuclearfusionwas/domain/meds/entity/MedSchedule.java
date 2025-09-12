package com.example.swnuclearfusionwas.domain.meds.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
        name = "med_schedules",
        uniqueConstraints = @UniqueConstraint(name = "uk_med_time_day", columnNames = {"med_id", "time", "day_of_week"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MedSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "med_id")
    private Medication medication;

    @Column(nullable = false) private LocalTime time;
    @Enumerated(EnumType.STRING) @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;
}