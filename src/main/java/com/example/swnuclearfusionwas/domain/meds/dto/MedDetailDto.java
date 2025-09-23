package com.example.swnuclearfusionwas.domain.meds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "MedDetailDto", description = "복용약 상세 정보 응답 DTO. 복용약 상세 조회 시 반환.")
public class MedDetailDto {
    @Schema(description = "복용약 고유 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long medId;
    @Schema(description = "약 이름", example = "타이레놀", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "하루 복용 횟수", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer frequencyPerDay;
    @Schema(description = "알림 활성화 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean alertEnabled;
    @Schema(description = "스케줄 목록(HH:mm 요일)", example = "[\"08:00 MONDAY\", \"13:00 WEDNESDAY\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> schedules;
}
