package com.example.back.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private String phone; // 3. 휴대폰 (시니어-보호자 연결의 Key가 됩니다)

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

    // --- 식물 정보 필드 ---
    @Column(nullable = true)
    private String plantColor;

    @Column(nullable = true)
    private String plantName;

    @Column(nullable = false)
    private int plantExp = 0;

    @Column(nullable = false)
    private int userWater = 1;

    @Column(nullable = false)
    private int userAffection = 1;

    // --- 기존 연관관계 ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Alarm> alarms = new ArrayList<>();

    // --- [!] 시니어-보호자 연동 기능 추가 ---

    /** (시니어 전용) 보호자가 내 정보를 열람한 횟수 */
    @Column(nullable = false)
    private int guardianViewCount = 0;

    /** (보호자 전용) 내가 연결 요청한 시니어 목록 */
    @OneToMany(mappedBy = "guardian", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SeniorGuardianLink> guardianLinks = new ArrayList<>();

    /** (시니어 전용) 나에게 연결 요청한 보호자 목록 */
    @OneToMany(mappedBy = "senior", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SeniorGuardianLink> seniorLinks = new ArrayList<>();

    // --- Builder 생성자 ---
    @Builder
    public User(String userId, String password, String phone, RoleType role, String fontSize,
                String name, LocalDate birthdate, String gender,
                String provider, String providerId,
                String plantColor, String plantName) {
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
        this.plantColor = plantColor;
        this.plantName = plantName;
        // guardianViewCount는 기본값 0으로 자동 초기화됩니다.
    }

    // --- Setter 메서드들 ---
    // (기존 Setter 생략)
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(RoleType role) { this.role = role; }
    public void setFontSize(String fontSize) { this.fontSize = fontSize; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }
    public void setGender(String gender) { this.gender = gender; }
    public User updateSocialInfo(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        return this;
    }
    public void setPlantColor(String plantColor) { this.plantColor = plantColor; }
    public void setPlantName(String plantName) { this.plantName = plantName; }

    // --- 비즈니스 로직 메서드들 ---
    // (기존 식물 관련 메서드 생략)
    /** 물 사용 (성공 시 true, 물 부족 시 false 반환) */
    public boolean useWater(int amount) {
        // [!] 1. 검사: 현재 물이 사용할 양보다 많거나 같은지 확인
        if (this.userWater >= amount) {
            // [!] 2. 사용: 물을 차감
            this.userWater -= amount;
            // [!] 3. 보상: 경험치 증가 (예시: 1 물당 5 경험치)
            this.plantExp += (amount * 5);
            // [!] 4. 성공 반환
            return true;
        }
        // [!] 5. 실패 반환 (물이 부족함)
        return false;
    }

    /** 애정도 사용 (성공 시 true, 애정 부족 시 false 반환) */
    public boolean useAffection(int amount) {
        // [!] 1. 검사: 현재 애정도가 사용할 양보다 많거나 같은지 확인
        if (this.userAffection >= amount) {
            // [!] 2. 사용: 애정도 차감
            this.userAffection -= amount;
            // [!] 3. 보상: 경험치 증가 (예시: 1 애정당 10 경험치)
            this.plantExp += (amount * 10);
            // [!] 4. 성공 반환
            return true;
        }
        // [!] 5. 실패 반환 (애정도가 부족함)
        return false;
    }
    public void addWater(int amount) { this.userWater += amount; }
    public void addAffection(int amount) { this.userAffection += amount; }
    public void addAlarm(Alarm alarm) { this.alarms.add(alarm); }


    // --- [!] 연동 기능 관련 편의 메서드 추가 ---

    /** (시니어 전용) 보호자 열람 횟수 1 증가 */
    public void incrementGuardianViewCount() {
        this.guardianViewCount++;
    }
}