package com.example.swnuclearfusionwas.domain.meds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "AlertCompleteReq", description = "복용 완료 처리 요청 DTO. 복용 이벤트 완료 시 사용.")
public class AlertCompleteReq {
    @Schema(description = "복용 이벤트 고유 ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long eventId;
}