package com.example.back.link.dto;

import com.example.back.domain.SeniorGuardianLink;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LinkedSeniorDto {

    private Long seniorId;      // 시니어의 DB ID (상세보기용)
    private String seniorName;    // 시니어 이름
    private String seniorPhone;   // 시니어 전화번호 (식별용)
    private LocalDateTime linkedAt;    // 연결이 승인된 날짜

    // Service에서 Entity를 DTO로 변환하기 위한 생성자
    public LinkedSeniorDto(SeniorGuardianLink link) {
        this.seniorId = link.getSenior().getId();
        this.seniorName = link.getSenior().getName();
        this.seniorPhone = link.getSenior().getPhone();
        this.linkedAt = link.getApprovedAt(); // 승인된 시간이 저장됨
    }
}