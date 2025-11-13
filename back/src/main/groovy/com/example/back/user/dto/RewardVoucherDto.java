package com.example.back.user.dto;

import com.example.back.domain.RewardVoucher;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class RewardVoucherDto {

    private Long id;
    private String voucherName;
    private LocalDateTime issuedAt;
    private boolean isUsed;

    public RewardVoucherDto(RewardVoucher voucher) {
        this.id = voucher.getId();
        this.voucherName = voucher.getVoucherName();
        this.issuedAt = voucher.getIssuedAt();
        this.isUsed = voucher.isUsed();
    }
}