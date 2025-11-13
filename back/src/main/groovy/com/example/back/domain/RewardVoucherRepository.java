package com.example.back.domain; // (리포지토리 패키지 경로)

import com.example.back.domain.User;
import com.example.back.domain.RewardVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RewardVoucherRepository extends JpaRepository<RewardVoucher, Long> {

    // 사용자가 보유한 모든 교환권을 조회
    List<RewardVoucher> findByUser(User user);

    // (선택) 사용자가 보유한 교환권 중 아직 사용하지 않은 것만 조회
    List<RewardVoucher> findByUserAndIsUsedFalse(User user);
}