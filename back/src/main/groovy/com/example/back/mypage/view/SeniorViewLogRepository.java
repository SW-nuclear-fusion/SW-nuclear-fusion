package com.example.back.mypage.view;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeniorViewLogRepository extends JpaRepository<SeniorViewLog, Long> {
    long countByCaregiverIdAndSeniorId(Long caregiverId, Long seniorId);
    long countBySeniorId(Long seniorId);
}

