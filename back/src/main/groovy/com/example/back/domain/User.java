package com.example.back.domain;

// [신규] UserPlant, RewardVoucher 임포트 추가
import com.example.back.domain.UserPlant;
import com.example.back.domain.RewardVoucher;
// [신규] 누락된 임포트 추가
import com.example.back.domain.Alarm;
import com.example.back.domain.SeniorGuardianLink;
import org.hibernate.annotations.ColumnDefault; // [신규] ColumnDefault 임포트

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

    @Column(unique = true, nullable = true)
    private String phone; // 3. 휴대폰

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RoleType role; // 4. 역할 (SENIOR, GUARDIAN)

    @Column(nullable = true)
    private String fontSize; // 5. 폰트 크기

    @Column(nullable = true)
    private String name; // 6. 이름

    @Column(nullable = true)
    private LocalDate birthdate; // 7. 생년월일

    @Column(nullable = true)
    private String gender; // 8. 성별

    // (소셜 로그인용)
    private String provider;
    private String providerId;

    // --- [삭제] 식물 정보 필드 ---
    // (plantColor, plantName, plantExp 필드 제거됨)
    // --- [삭제] ---

    // --- [유지] 사용자의 재화 (물, 애정도) ---
    @Column(nullable = false)
    @ColumnDefault("1")
    private int userWater = 1;

    @Column(nullable = false)
    @ColumnDefault("1")
    private int userAffection = 1;

    // --- [신규] 사용자의 재화 (포인트) ---
    @Column(name = "user_points", nullable = false)
    @ColumnDefault("0")
    private int userPoints = 0;


    // --- [신규] 연관관계: 사용자가 보유한 식물 목록 ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserPlant> userPlants = new ArrayList<>();

    // --- [신규] 연관관계: 사용자가 획득한 보상 목록 ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RewardVoucher> rewardVouchers = new ArrayList<>();

    // --- 기존 연관관계 ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Alarm> alarms = new ArrayList<>();

    // --- [!] 시니어-보호자 연동 기능 추가 ---
    @Column(nullable = false)
    @ColumnDefault("0")
    private int guardianViewCount = 0;

    @OneToMany(mappedBy = "guardian", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SeniorGuardianLink> guardianLinks = new ArrayList<>();

    @OneToMany(mappedBy = "senior", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SeniorGuardianLink> seniorLinks = new ArrayList<>();


    // --- [수정] Builder 생성자 ---
    @Builder
    public User(String userId, String password, String phone, RoleType role, String fontSize,
                String name, LocalDate birthdate, String gender,
                String provider, String providerId) {
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
        // userWater(1), userAffection(1), userPoints(0)는 기본값으로 자동 초기화
    }

    // --- Setter 메서드들 ---
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

    // --- [수정] 비즈니스 로직 메서드들 ---

    /** 물 사용 (성공 시 true, 물 부족 시 false 반환) */
    public boolean useWater(int amount) {
        if (this.userWater >= amount) {
            this.userWater -= amount;
            return true;
        }
        return false;
    }

    /** 애정도 사용 (성공 시 true, 애정 부족 시 false 반환) */
    public boolean useAffection(int amount) {
        if (this.userAffection >= amount) {
            this.userAffection -= amount;
            return true;
        }
        return false;
    }

    // [신규] 포인트 사용 (성공 시 true, 포인트 부족 시 false 반환)
    public boolean usePoints(int amount) {
        if (this.userPoints >= amount) {
            this.userPoints -= amount;
            return true;
        }
        return false; // 포인트 부족
    }

    public void addWater(int amount) { this.userWater += amount; }
    public void addAffection(int amount) { this.userAffection += amount; }

    // [신규] 포인트 추가
    public void addPoints(int amount) { this.userPoints += amount; }

    public void addAlarm(Alarm alarm) { this.alarms.add(alarm); }


    // --- [!] 연동 기능 관련 편의 메서드 추가 ---
    /** (시니어 전용) 보호자가 내 정보를 열람 횟수 1 증가 */
    public void incrementGuardianViewCount() {
        this.guardianViewCount++;
    }
}