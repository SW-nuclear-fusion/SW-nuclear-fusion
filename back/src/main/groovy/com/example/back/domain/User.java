package com.example.back.domain; // 패키지 이름은 사용자님의 프로젝트에 맞게 확인해주세요

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId; // 1. 아이디

    @Column(nullable = false)
    private String password; // 2. 비밀번호 (암호화될 예정)

    // (소셜 가입 시 null 허용)
    @Column(unique = true, nullable = true)
    private String phone; // 3. 휴대폰

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RoleType role; // 4. 역할 (SENIOR, GUARDIAN)

    @Column(nullable = true)
    private String fontSize; // 5. 폰트 크기

    @Column(nullable = true) // 소셜 로그인 시 이름이 없을 수 있으므로 true로 변경
    private String name; // 6. 이름

    @Column(nullable = true)
    private LocalDate birthdate; // 7. 생년월일

    @Column(nullable = true)
    private String gender; // 8. 성별

    // (소셜 로그인용)
    private String provider; // (예: "google", "kakao")
    private String providerId; // (소셜 서비스의 고유 ID)

    // --- [!] 식물 정보 필드 추가 ---
    @Column(nullable = true)
    private String plantColor; // "purple", "blue", "yellow", "pink"

    @Column(nullable = true)
    private String plantName; // 사용자가 지은 식물 이름

    @Column(nullable = false)
    private int plantExp = 0; // 식물 경험치 (기본값 0)

    @Column(nullable = false)
    private int userWater = 1; // 보유 물 (기본값 0)

    @Column(nullable = false)
    private int userAffection = 1; // 보유 애정도 (기본값 0)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Alarm> alarms = new ArrayList<>(); // 초기 빈 리스트 할당

    // --- Builder 생성자 ---
    // @Builder 어노테이션은 모든 필드를 포함해야 합니다.
    // 생성 시 null이 될 수 있는 필드는 호출 시 .field(null) 또는 생략합니다.
    @Builder
    public User(String userId, String password, String phone, RoleType role, String fontSize,
                String name, LocalDate birthdate, String gender,
                String provider, String providerId,
                String plantColor, String plantName) { // [!] 식물 필드 추가
        this.userId = userId;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.fontSize = fontSize;
        this.name = name;
        this.birthdate = birthdate;
        this.gender = gender;
        this.provider = provider;
        this.providerId = providerId;
        this.plantColor = plantColor; // [!] 식물 필드 초기화
        this.plantName = plantName;   // [!] 식물 필드 초기화
    }

    // --- Setter 메서드들 ---
    // (소셜 유저 추가 정보 업데이트용)
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(RoleType role) { this.role = role; }
    public void setFontSize(String fontSize) { this.fontSize = fontSize; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }
    public void setGender(String gender) { this.gender = gender; }

    // (소셜 로그인 시 이름 업데이트용)
    public User updateSocialInfo(String name) {
        // 이름이 null이거나 비어있지 않은 경우에만 업데이트
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        return this;
    }

    /** 물 사용 (성공 시 true, 물 부족 시 false 반환) */
    public boolean useWater(int amount) {
        if (this.userWater >= amount) {
            this.userWater -= amount;
            // 물 사용 시 경험치 증가 (예시: 1 물당 5 경험치)
            this.plantExp += (amount * 5);
            return true;
        }
        return false;
    }

    /** 애정도 사용 (성공 시 true, 애정 부족 시 false 반환) */
    public boolean useAffection(int amount) {
        if (this.userAffection >= amount) {
            this.userAffection -= amount;
            // 애정 사용 시 경험치 증가 (예시: 1 애정당 10 경험치)
            this.plantExp += (amount * 10);
            return true;
        }
        return false;
    }

    // (관리자용 또는 테스트용) 자원 직접 추가 메서드 (필요시 사용)
    public void addWater(int amount) { this.userWater += amount; }
    public void addAffection(int amount) { this.userAffection += amount; }
    public void addAlarm(Alarm alarm) { this.alarms.add(alarm); }

    // --- [!] 식물 정보 업데이트용 Setter 추가 ---
    public void setPlantColor(String plantColor) { this.plantColor = plantColor; }
    public void setPlantName(String plantName) { this.plantName = plantName; }

}