package com.example.back.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // @CreatedDate 활성화를 위해 추가
public class SeniorGuardianLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 연결을 요청한 보호자 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id", nullable = false)
    private User guardian;

    /** 연결 대상 시니어 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private User senior;

    /** 연결 상태 (PENDING, APPROVED, REJECTED) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LinkStatus status;

    /** 요청 생성 시간 */
    @CreatedDate // 엔티티 생성 시 자동으로 현재 시간 저장
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 요청 승인 시간 (승인될 때만 값이 설정됨) */
    @Column
    private LocalDateTime approvedAt;

    @Builder
    public SeniorGuardianLink(User guardian, User senior, LinkStatus status) {
        this.guardian = guardian;
        this.senior = senior;
        this.status = status;
        // createdAt은 @CreatedDate에 의해 자동 관리됩니다.
    }

    // --- 편의 메서드 (상태 변경) ---

    /** 연결 요청을 승인합니다. */
    public void approveLink() {
        if (this.status == LinkStatus.PENDING) {
            this.status = LinkStatus.APPROVED;
            this.approvedAt = LocalDateTime.now();
        }
    }

    /** 연결 요청을 거절합니다. */
    public void rejectLink() {
        if (this.status == LinkStatus.PENDING) {
            this.status = LinkStatus.REJECTED;
        }
    }
}