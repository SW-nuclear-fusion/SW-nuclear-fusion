package com.example.back.mypage.dto;

import lombok.Getter;
import java.time.LocalDate;

// AlarmCheckLogRepository에서 JOIN 조회 결과를 받기 위한 DTO
@Getter
public class MedicationCheckDto {
    private LocalDate checkDate;
    private String medicationName;

    public MedicationCheckDto(LocalDate checkDate, String medicationName) {
        this.checkDate = checkDate;
        this.medicationName = medicationName;
    }
}