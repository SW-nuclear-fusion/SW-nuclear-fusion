package com.example.back.domain; // 패키지 확인

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate; // LocalDate import

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alarm_check_logs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "alarm_id", "check_date"})
        })
public class AlarmCheckLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id", nullable = false)
    private Alarm alarm;

    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    @Builder
    public AlarmCheckLog(User user, Alarm alarm, LocalDate checkDate) {
        this.user = user;
        this.alarm = alarm;
        this.checkDate = checkDate;
    }
}