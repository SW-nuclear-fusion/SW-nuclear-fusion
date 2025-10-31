package com.example.back.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PlantSelectionRequest {
    @NotBlank
    private String plantColor;
    @NotBlank
    private String plantName;
}