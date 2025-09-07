package com.example.swnuclearfusionwas.domain.mypage.service;

import org.springframework.stereotype.Component;

@Component
public class PhoneValidator {

    public String validatePhone(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("전화번호를 입력하세요.");
        }
        if (!raw.matches("^\\d+$")) {
            throw new IllegalArgumentException("전화번호는 하이픈/공백 없이 숫자만 입력해야 합니다.");
        }
        if (raw.length() != 11) {
            throw new IllegalArgumentException("전화번호는 11자리 숫자여야 합니다.");
        }
        if (!raw.startsWith("010")) {
            throw new IllegalArgumentException("휴대전화번호는 010으로 시작해야 합니다.");
        }
        return raw;
    }

    public String mask(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        String prefix = phone.substring(0, 3);
        String suffix = phone.substring(phone.length() - 4);
        return prefix + "****" + suffix;
    }
}