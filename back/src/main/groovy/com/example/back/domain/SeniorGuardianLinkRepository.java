package com.example.back.domain;

import com.example.back.domain.LinkStatus;
import com.example.back.domain.SeniorGuardianLink;
import com.example.back.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeniorGuardianLinkRepository extends JpaRepository<SeniorGuardianLink, Long> {

    /** 보호자와 시니어로 특정 연결 관계 조회 (중복 요청 방지용) */
    Optional<SeniorGuardianLink> findByGuardianAndSenior(User guardian, User senior);

    /** (기존) 특정 시니어에게 온 요청 목록 조회 (N+1 발생 가능성 있음) */
    List<SeniorGuardianLink> findBySeniorAndStatus(User senior, LinkStatus status);

    /** (기존) 특정 보호자가 요청한 목록 조회 (N+1 발생 가능성 있음) */
    List<SeniorGuardianLink> findByGuardianAndStatus(User guardian, LinkStatus status);


    // --- [!] N+1 문제 해결을 위한 쿼리 추가 ---

    /**
     * (시니어) 본인에게 온 요청 목록을 '보호자 정보(guardian)'와 함께 조회
     * (getPendingRequests 에서 사용)
     */
    @Query("SELECT sl FROM SeniorGuardianLink sl JOIN FETCH sl.guardian g WHERE sl.senior = :senior AND sl.status = :status")
    List<SeniorGuardianLink> findBySeniorAndStatusWithGuardian(
            @Param("senior") User senior,
            @Param("status") LinkStatus status
    );

    /**
     * (보호자) 본인이 연결한 시니어 목록을 '시니어 정보(senior)'와 함께 조회
     * (getApprovedSeniors 에서 사용)
     */
    @Query("SELECT sl FROM SeniorGuardianLink sl JOIN FETCH sl.senior s WHERE sl.guardian = :guardian AND sl.status = :status")
    List<SeniorGuardianLink> findByGuardianAndStatusWithSenior(
            @Param("guardian") User guardian,
            @Param("status") LinkStatus status
    );

    Optional<Object> findByGuardianAndSeniorAndStatus(User guardian, User senior, LinkStatus linkStatus);
}