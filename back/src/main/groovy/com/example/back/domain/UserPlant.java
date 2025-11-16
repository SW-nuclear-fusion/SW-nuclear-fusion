package com.example.back.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_plant")
public class UserPlant {

    // (누적 경험치 레벨 정의)
    public static final int[] CUMULATIVE_EXP_FOR_LEVEL = { 0, 0, 100, 300 };
    public static final int MAX_LEVEL = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "plant_type", nullable = false)
    private PlantType plantType;

    @Column(name = "plant_level")
    @ColumnDefault("1")
    private int plantLevel = 1;

    @Column(name = "plant_exp")
    @ColumnDefault("0")
    private int plantExp = 0;

    @Column(name = "plant_name", nullable = true)
    private String plantName;

    @Column(name = "is_active", nullable = false)
    @ColumnDefault("false")
    private boolean isActive = false;

    @Column(name = "is_max_level", nullable = false)
    @ColumnDefault("false")
    private boolean isMaxLevel = false;

    // --- [신규] ---
    // 만렙 달성 후 1000 포인트를 수령했는지 여부
    @Column(name = "is_points_claimed", nullable = false)
    @ColumnDefault("false")
    private boolean isPointsClaimed = false;
    // --- [신규 끝] ---


    // --- 생성자 (Builder) ---
    @Builder
    public UserPlant(User user, PlantType plantType) {
        this.user = user;
        this.plantType = plantType;
        this.plantLevel = 1;
        this.plantExp = 0;
        this.isActive = false;
        this.isMaxLevel = false;
        this.isPointsClaimed = false; // [신규]
    }

    // --- 비즈니스 로직 (Setter 대용) ---

    public void updatePlantName(String plantName) {
        this.plantName = plantName;
    }

    // (addExp 메서드는 변경 없음)
    public boolean addExp(int amount) {
        if (this.isMaxLevel) {
            return false;
        }
        this.plantExp += amount;
        while (!this.isMaxLevel) {
            int nextLevel = this.plantLevel + 1;
            if (nextLevel > MAX_LEVEL) {
                break;
            }
            int requiredExp = CUMULATIVE_EXP_FOR_LEVEL[nextLevel];
            if (this.plantExp >= requiredExp) {
                this.plantLevel = nextLevel;
                if (this.plantLevel == MAX_LEVEL) {
                    this.isMaxLevel = true;
                    return true; // 만렙 달성!
                }
            } else {
                break;
            }
        }
        return false;
    }

    /** [수정] 경험치 초기화 (월별 스케줄러용) */
    public void resetExp() {
        this.plantLevel = 1;
        this.plantExp = 0;
        this.isMaxLevel = false;
        this.isPointsClaimed = false; // [신규] 포인트 수령 여부도 초기화
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    // [신규] 포인트 수령 완료 처리
    public void claimPoints() {
        if (this.isMaxLevel) {
            this.isPointsClaimed = true;
        }
    }
}