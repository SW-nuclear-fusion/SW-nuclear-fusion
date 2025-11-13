package com.example.back.user.dto;

import com.example.back.domain.RoleType;
import com.example.back.domain.User;
import com.example.back.domain.UserPlant;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserInfoResponse {
    // (사용자 기본 정보)
    private Long id;
    private String userId;
    private String name;
    private RoleType role;
    private String phone;
    private LocalDate birthdate;
    private String gender;
    private String fontSize;

    // (현재 활성화된 식물 정보)
    private String plantColor;
    private String plantName;
    private int plantExp;
    private int plantLevel;

    // --- [신규] ---
    // (활성화된 식물의) 포인트 수령 여부
    private boolean isPointsClaimed;
    // --- [신규 끝] ---

    // (보유 자원)
    private int userWater;
    private int userAffection;
    private int userPoints;

    /**
     * [수정] 생성자: User와 활성화된 UserPlant를 받음
     */
    public UserInfoResponse(User user, UserPlant activePlant) {
        // 1. User 정보 매핑
        this.id = user.getId();
        this.userId = user.getUserId();
        this.name = user.getName();
        this.role = user.getRole();
        this.phone = user.getPhone();
        this.birthdate = user.getBirthdate();
        this.gender = user.getGender();
        this.fontSize = user.getFontSize();
        this.userWater = user.getUserWater();
        this.userAffection = user.getUserAffection();
        this.userPoints = user.getUserPoints();

        // 2. 활성화된 UserPlant 정보 매핑
        if (activePlant != null) {
            this.plantColor = activePlant.getPlantType().name();
            this.plantName = activePlant.getPlantName();
            this.plantExp = activePlant.getPlantExp();
            this.plantLevel = activePlant.getPlantLevel();
            this.isPointsClaimed = activePlant.isPointsClaimed(); // [신규]
        } else {
            // 활성화된 식물이 없는 경우
            this.plantColor = null;
            this.plantName = null;
            this.plantExp = 0;
            this.plantLevel = 1;
            this.isPointsClaimed = false; // [신규]
        }
    }
}