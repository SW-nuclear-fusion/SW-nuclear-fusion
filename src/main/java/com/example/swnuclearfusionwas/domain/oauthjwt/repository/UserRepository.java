package com.example.swnuclearfusionwas.domain.oauthjwt.repository;

import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
    Optional<UserEntity> findByUserId(String userId);
    Optional<UserEntity> findByPhone(String phone);
    boolean existsByUserId(String userId);
    boolean existsByPhone(String phone);
}
