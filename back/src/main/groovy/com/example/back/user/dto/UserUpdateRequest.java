package com.example.back.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequest {

    @Size(min = 1, max = 20, message = "이름은 1자에서 20자 사이여야 합니다.")
    private String name;

    private String fontSize;
}