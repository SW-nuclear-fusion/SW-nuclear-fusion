package com.example.back.link.dto;

import com.example.back.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class SeniorStatusDto {

    // (시니어 기본 정보)
    private Long seniorId;
    private String seniorName;
    private String seniorPhoneNumber;
    private int guardianViewCount; // 보호자가 총 열람한 횟수

    // --- [!] '오늘의 상태' 데이터로 필드 수정 ---

    /** 오늘의 감정 아이콘 (예: "happy", "sad", null) */
    private String todayMoodIcon;

    /** 오늘 복용한 약물 이름 리스트 (MyPageService의 로직 활용) */
    private List<String> todayMedicationLogs;

    /** * 오늘의 퀴즈 결과 리스트 (예: ["퀴즈: 8/10"])
     * (퀴즈를 여러 번 풀 수 있으므로 List로 유지)
     */
    private List<String> todayQuizLogs;

    @Builder
    public SeniorStatusDto(User senior, String todayMoodIcon, List<String> todayMedicationLogs, List<String> todayQuizLogs) {
        // 시니어 기본 정보 매핑
        this.seniorId = senior.getId();
        this.seniorName = senior.getName();
        this.seniorPhoneNumber = senior.getPhone();
        this.guardianViewCount = senior.getGuardianViewCount();

        // 실제 데이터 매핑
        this.todayMoodIcon = todayMoodIcon;
        this.todayMedicationLogs = todayMedicationLogs;
        this.todayQuizLogs = todayQuizLogs;
    }
}