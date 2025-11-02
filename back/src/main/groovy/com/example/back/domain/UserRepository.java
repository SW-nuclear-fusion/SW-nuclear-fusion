package com.example.back.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 아이디로 사용자 찾기 (로그인 시)
    Optional<User> findByUserId(String userId);

    // 아이디 중복 검사
    boolean existsByUserId(String userId);

    // 휴대폰 중복 검사
    boolean existsByPhone(String phone);

    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
