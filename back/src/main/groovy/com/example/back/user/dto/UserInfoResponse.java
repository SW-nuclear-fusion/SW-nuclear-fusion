package com.example.back.user.dto;

import com.example.back.domain.RoleType;
import com.example.back.domain.User;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserInfoResponse {
    // 사용자 기본 정보
    private Long id;
    private String userId;
    private String name;
    private RoleType role;
    private String phone; // (선택: 필요 없다면 제외)
    private LocalDate birthdate; // (선택: 필요 없다면 제외)
    private String gender; // (선택: 필요 없다면 제외)
    private String fontSize;

    // 식물 정보
    private String plantColor;
    private String plantName;
    private int plantExp;

    // 보유 자원
    private int userWater;
    private int userAffection;

    // User 엔티티를 UserInfoResponse DTO로 변환하는 생성자
    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.name = user.getName();
        this.role = user.getRole();
        this.phone = user.getPhone();
        this.birthdate = user.getBirthdate();
        this.gender = user.getGender();
        this.fontSize = user.getFontSize();
        this.plantColor = user.getPlantColor();
        this.plantName = user.getPlantName();
        this.plantExp = user.getPlantExp();
        this.userWater = user.getUserWater();
        this.userAffection = user.getUserAffection();
    }
}
