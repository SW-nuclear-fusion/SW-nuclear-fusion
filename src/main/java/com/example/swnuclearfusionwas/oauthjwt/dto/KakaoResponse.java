package com.example.swnuclearfusionwas.oauthjwt.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override public String getName() {
        Map<String, Object> account = (Map<String, Object>) attribute.get("kakao_account");
        if (account == null) return null;
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        return profile == null ? null : (String) profile.get("nickname");
    }
}
