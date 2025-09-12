package com.example.swnuclearfusionwas.domain.meds.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class MedListItemDto {
    private Long medId;
    private String name;
    private Integer frequencyPerDay;
    private Boolean alertEnabled;
    private List<String> schedules;
}
