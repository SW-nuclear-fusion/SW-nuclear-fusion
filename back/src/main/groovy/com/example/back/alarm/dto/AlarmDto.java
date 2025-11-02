package com.example.back.alarm.dto; // alarm 패키지 확인

import com.example.back.domain.Alarm; // Alarm 엔티티 import
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern; // 시간 형식 검증용
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime; // [!] LocalDateTime 추가
import java.time.format.DateTimeFormatter;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class AlarmDto {

    private Long id;

    @NotBlank(message = "약 이름을 입력해주세요.")
    private String medicationName;

    @NotNull(message = "알림 시간을 입력해주세요.")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "시간 형식을 HH:mm으로 입력해주세요.") // HH:mm 형식 검증
    private String notificationTime;

    @NotEmpty(message = "알림 요일을 선택해주세요.")
    private Set<String> notificationDays;

    private Boolean enabled;
    private String createdAt;

    public AlarmDto(Alarm alarm) {
        this.id = alarm.getId();
        this.medicationName = alarm.getMedicationName();
        this.notificationTime = alarm.getNotificationTime().toString();
        this.notificationDays = alarm.getNotificationDays();
        this.enabled = alarm.isEnabled();this.createdAt = (alarm.getCreatedAt() != null) ? alarm.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }

    public static List<AlarmDto> fromEntities(List<Alarm> alarms) {
        if (alarms == null) return List.of();
        return alarms.stream().map(AlarmDto::new).collect(Collectors.toList());
    }
}