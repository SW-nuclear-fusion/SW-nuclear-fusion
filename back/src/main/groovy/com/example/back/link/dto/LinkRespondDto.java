package com.example.back.link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LinkRespondDto {

    @NotNull(message = "요청 ID가 필요합니다.")
    private Long linkId; // 처리할 link_id

    @NotBlank(message = "응답(APPROVE 또는 REJECT)이 필요합니다.")
    private String action; // "APPROVE" 또는 "REJECT"

    // action 값을 Enum으로 안전하게 변환하기 위한 헬퍼 메서드
    public LinkActionType getActionType() {
        try {
            return LinkActionType.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("잘못된 요청입니다. 'APPROVE' 또는 'REJECT'만 가능합니다.");
        }
    }

    // DTO 내부에 간단한 Enum을 정의하거나, 별도 파일로 분리해도 됩니다.
    public enum LinkActionType {
        APPROVE,
        REJECT
    }
}