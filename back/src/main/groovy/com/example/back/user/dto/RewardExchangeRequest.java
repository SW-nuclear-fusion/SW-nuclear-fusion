package com.example.back.user.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RewardExchangeRequest {
    @NotBlank(message = "상품 ID는 필수입니다.")
    private String itemId;

    @NotBlank(message = "상품 이름은 필수입니다.")
    private String itemName;

    @Min(value = 1, message = "가격은 1포인트 이상이어야 합니다.")
    private int price;
}