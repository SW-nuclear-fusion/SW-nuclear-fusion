package com.example.back.link.dto;

import com.example.back.domain.SeniorGuardianLink;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LinkPendingResponseDto {

    private Long linkId;          // 연결 요청 ID (응답 시 필요)
    private String guardianName;  // 요청한 보호자 이름
    private LocalDateTime requestedAt; // 요청 온 시간

    // Service에서 Entity를 DTO로 변환하기 위한 생성자
    public LinkPendingResponseDto(SeniorGuardianLink link) {
        this.linkId = link.getId();
        this.guardianName = link.getGuardian().getName(); // Lazy Loading 주의
        this.requestedAt = link.getCreatedAt();
    }
}