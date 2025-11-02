package com.example.back.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "daily_moods",
        uniqueConstraints = { // 한 유저가 같은 날짜에 감정 중복 기록 불가
                @UniqueConstraint(columnNames = {"user_id", "mood_date"})
        })
public class DailyMood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "mood_date", nullable = false)
    private LocalDate moodDate; // 감정 기록 날짜

    @Column(nullable = false)
    private String moodIcon; // 예: "happy", "sad", "emoji-unicode"

    @Builder
    public DailyMood(User user, LocalDate moodDate, String moodIcon) {
        this.user = user;
        this.moodDate = moodDate;
        this.moodIcon = moodIcon;
    }

    // 감정(이모티콘) 수정용 메서드
    public void updateMood(String moodIcon) {
        this.moodIcon = moodIcon;
    }
}