package com.example.back.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reward_voucher")
public class RewardVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // N(RewardVoucher) : 1(User) 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "voucher_name", nullable = false)
    private String voucherName; // 예: "2025년 11월 핑크 식물 만렙 보상"

    @CreationTimestamp // 엔티티 생성 시 자동으로 현재 시간 저장
    @Column(name = "issued_at", nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(name = "is_used", nullable = false)
    @ColumnDefault("false")
    private boolean isUsed = false;

    // --- 생성자 (Builder) ---
    @Builder
    public RewardVoucher(User user, String voucherName, String voucherCode) {
        this.user = user;
        this.voucherName = voucherName;
        this.isUsed = false; // 기본값 명시
    }

    // --- 비즈니스 로직 (Setter 대용) ---

    /** 교환권 사용 처리 */
    public void useVoucher() {
        this.isUsed = true;
        // TODO: 사용 일시(used_at) 필드가 필요하다면 추가 및 기록
    }
}