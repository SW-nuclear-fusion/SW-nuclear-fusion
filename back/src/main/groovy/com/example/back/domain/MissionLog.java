package com.example.back.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "mission_logs",
        uniqueConstraints = { // 한 유저가 같은 미션을 같은 날짜에 중복 완료 불가
                @UniqueConstraint(columnNames = {"user_id", "mission_id", "completion_date"})
        })
public class MissionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private LocationMission mission;

    @CreatedDate
    @Column(name = "completion_date", nullable = false, updatable = false)
    private LocalDate completionDate;

    @Builder
    public MissionLog(User user, LocationMission mission) {
        this.user = user;
        this.mission = mission;
    }
}