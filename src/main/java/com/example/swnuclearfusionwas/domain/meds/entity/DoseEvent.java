package com.example.swnuclearfusionwas.domain.meds.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "dose_events",
        uniqueConstraints = @UniqueConstraint(name = "uk_schedule_date", columnNames = {"schedule_id","date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoseEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "schedule_id")
    private MedSchedule schedule;

    @Column(nullable = false) private LocalDate date;
    @Column(nullable = false) private Boolean taken=false;
    @Column private OffsetDateTime takenAt;
}