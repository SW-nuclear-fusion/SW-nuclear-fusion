package com.example.back.user.dto;

import com.example.back.domain.PlantType;
import com.example.back.domain.UserPlant;
import lombok.Getter;

@Getter
public class UserPlantDto {

    private Long id;
    private PlantType plantType;
    private String plantName;
    private int plantLevel;
    private int plantExp;
    private boolean isActive;

    // 프론트엔드 필터링을 위해 'isMaxLevel' 필드 추가
    private boolean isMaxLevel;

    // 엔티티를 DTO로 변환하는 생성자
    public UserPlantDto(UserPlant plant) {
        this.id = plant.getId();
        this.plantType = plant.getPlantType();
        this.plantName = plant.getPlantName();
        this.plantLevel = plant.getPlantLevel();
        this.plantExp = plant.getPlantExp();
        this.isActive = plant.isActive();
        this.isMaxLevel = plant.isMaxLevel(); // [신규]
    }
}