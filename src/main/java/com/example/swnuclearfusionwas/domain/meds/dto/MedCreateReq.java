package com.example.swnuclearfusionwas.domain.meds.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

@Getter @Setter
public class MedCreateReq {
    private String name;
    private Integer frequencyPerDay;
    private List<String> times;
    private Boolean everyDay = true;
    private Set<DayOfWeek> daysOfWeek;
}