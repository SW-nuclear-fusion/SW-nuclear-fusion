package com.example.swnuclearfusionwas.domain.meds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "AlertToggleReq", description = "복용약 알림 활성화/비활성화 요청 DTO. 알림 토글 시 사용.")
public class AlertToggleReq {
    @Schema(description = "복용약 고유 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long medId;
    @Schema(description = "알림 활성화 여부(true: 알림 ON, false: 알림 OFF)", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;
}
