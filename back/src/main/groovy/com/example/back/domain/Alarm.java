package com.example.back.domain; // 패키지 확인

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate; // [!] 추가
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // [!] 추가
import java.time.LocalDateTime;

import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "alarms")
@EntityListeners(AuditingEntityListener.class)
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String medicationName;

    @Column(nullable = false)
    private LocalTime notificationTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "alarm_days", joinColumns = @JoinColumn(name = "alarm_id"))
    @Column(name = "alarm_day", nullable = false)
    private Set<String> notificationDays;

    @Column(nullable = false)
    private boolean enabled = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Alarm(User user, String medicationName, LocalTime notificationTime, Set<String> notificationDays, Boolean enabled) {
        this.user = user;
        this.medicationName = medicationName;
        this.notificationTime = notificationTime;
        this.notificationDays = notificationDays;
        if (enabled != null) {
            this.enabled = enabled;
        }
    }

    public void updateAlarm(String medicationName, LocalTime notificationTime, Set<String> notificationDays, boolean enabled) {
        this.medicationName = medicationName;
        this.notificationTime = notificationTime;
        this.notificationDays = notificationDays;
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}