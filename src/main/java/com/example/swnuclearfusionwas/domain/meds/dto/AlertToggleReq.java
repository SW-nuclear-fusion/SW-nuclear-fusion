package com.example.swnuclearfusionwas.domain.meds.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertToggleReq {
    private Long medId;
    private Boolean enabled;
}
