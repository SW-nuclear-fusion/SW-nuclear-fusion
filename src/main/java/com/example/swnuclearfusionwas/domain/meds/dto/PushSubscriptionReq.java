package com.example.swnuclearfusionwas.domain.meds.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushSubscriptionReq {
    private String endpoint;
    private String p256dh;
    private String auth;
}

