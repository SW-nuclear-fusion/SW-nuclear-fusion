package com.example.back.domain; // (리포지토리 패키지 경로)

import com.example.back.domain.User;
import com.example.back.domain.UserPlant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserPlantRepository extends JpaRepository<UserPlant, Long> {

    // 사용자가 보유한 모든 식물을 조회
    List<UserPlant> findByUser(User user);

    // 사용자의 식물 중 현재 활성화된(is_active = true) 식물을 조회
    Optional<UserPlant> findByUserAndIsActiveTrue(User user);
}