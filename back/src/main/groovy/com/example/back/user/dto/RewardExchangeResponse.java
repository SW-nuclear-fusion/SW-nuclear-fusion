package com.example.back.user.dto;

import lombok.Getter;
import com.example.back.user.dto.UserInfoResponse; // 기존 DTO 가정

@Getter
public class RewardExchangeResponse {
    private UserInfoResponse userInfo; // 갱신된 유저 정보 (잔여 포인트 포함)
    private RewardVoucherDto voucher; // 새로 획득한 교환권 정보

    public RewardExchangeResponse(UserInfoResponse userInfo, RewardVoucherDto voucher) {
        this.userInfo = userInfo;
        this.voucher = voucher;
    }
}