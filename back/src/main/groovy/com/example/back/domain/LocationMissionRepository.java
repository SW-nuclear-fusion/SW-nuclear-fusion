package com.example.back.domain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationMissionRepository extends JpaRepository<LocationMission, Long> {
    // 모든 미션 목록 조회
}