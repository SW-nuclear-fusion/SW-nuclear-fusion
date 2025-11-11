package com.example.back.link.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LinkRequestDto {

    @NotBlank(message = "시니어의 전화번호를 입력해주세요.")
    private String seniorPhoneNumber;
}