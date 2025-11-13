package com.example.back.user.dto;

import com.example.back.domain.PlantType; // [!] PlantType 임포트
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PlantActivateRequest {
    @NotNull
    private PlantType plantType;
    @NotBlank
    private String plantName;
}