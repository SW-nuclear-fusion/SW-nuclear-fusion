package com.example.swnuclearfusionwas.domain.meds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Schema(name = "MedCreateReq", description = "복용약 등록 요청 DTO. 신규 복용약 등록 시 사용.\n- 요일(daysOfWeek)은 everyDay=false일 때만 필수입니다.")
@Getter @Setter
public class MedCreateReq {
    @Schema(description = "약 이름 (최대 50자)", example = "타이레놀", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    private String name;
    @Schema(description = "하루 복용 횟수 (1 이상)", example = "3", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    private Integer frequencyPerDay;
    @Schema(description = "복용 시간 목록(HH:mm, 24시간제, frequencyPerDay와 길이 일치, 최소 1개 이상)", example = "[\"08:00\", \"13:00\", \"20:00\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> times;
    @Schema(description = "매일 복용 여부 (true면 daysOfWeek 무시)", example = "true", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "true")
    private Boolean everyDay = true;
    @Schema(description = "복용 요일 집합 (everyDay=false일 때 필수, MONDAY~SUNDAY)", example = "[\"MONDAY\", \"WEDNESDAY\"]", nullable = true)
    private Set<DayOfWeek> daysOfWeek;
}