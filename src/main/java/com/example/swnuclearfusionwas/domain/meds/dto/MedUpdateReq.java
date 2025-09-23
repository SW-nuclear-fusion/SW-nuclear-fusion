package com.example.swnuclearfusionwas.domain.meds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Schema(name = "MedUpdateReq", description = "복용약 정보 수정 요청 DTO. 복용약의 이름, 횟수, 시간, 요일 등 수정 시 사용.")
public class MedUpdateReq {
    @Schema(description = "약 이름 (최대 50자)", example = "타이레놀", maxLength = 50)
    private String name;
    @Schema(description = "하루 복용 횟수 (1 이상)", example = "3", minimum = "1")
    private Integer frequencyPerDay;
    @Schema(description = "복용 시간 목록(HH:mm, 24시간제, frequencyPerDay와 길이 일치, 최소 1개 이상)", example = "[\"08:00\", \"13:00\", \"20:00\"]")
    private List<String> times;
    @Schema(description = "매일 복용 여부 (true면 daysOfWeek 무시)", example = "true", defaultValue = "true")
    private Boolean everyDay;
    @Schema(description = "복용 요일 집합 (everyDay=false일 때 필수, MONDAY~SUNDAY)", example = "[\"MONDAY\", \"WEDNESDAY\"]", nullable = true)
    private Set<DayOfWeek> daysOfWeek;
}