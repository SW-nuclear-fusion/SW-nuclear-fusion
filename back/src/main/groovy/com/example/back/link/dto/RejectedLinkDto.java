package com.example.back.link.dto;

import com.example.back.domain.SeniorGuardianLink;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class RejectedLinkDto {

    private Long linkId;      // 연결 ID (나중에 삭제/관리용)
    private String seniorName;  // 거절한 시니어 이름
    private String seniorPhone; // 거절한 시니어 전화번호
    private LocalDateTime requestedAt; // 내가 요청했던 시간

    public RejectedLinkDto(SeniorGuardianLink link) {
        this.linkId = link.getId();
        this.seniorName = link.getSenior().getName();
        this.seniorPhone = link.getSenior().getPhone();
        this.requestedAt = link.getCreatedAt();
    }
}