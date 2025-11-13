package com.example.back.user.dto;

import lombok.Getter;
import java.util.List;

/**
 * 교환권 목록 화면에 필요한 데이터를 담는 DTO
 */
@Getter
public class RewardVoucherListResponse {
    private List<RewardVoucherDto> allVouchers;
    private List<RewardVoucherDto> unusedVouchers; // 구매했지만 사용하지 않은 목록
    private List<RewardVoucherDto> usedVouchers;   // 사용 완료된 목록

    public RewardVoucherListResponse(
            List<RewardVoucherDto> allVouchers,
            List<RewardVoucherDto> unusedVouchers,
            List<RewardVoucherDto> usedVouchers)
    {
        this.allVouchers = allVouchers;
        this.unusedVouchers = unusedVouchers;
        this.usedVouchers = usedVouchers;
    }
}