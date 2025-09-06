package com.example.swnuclearfusionwas.domain.oauthjwt.repository;

import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
    boolean existsByUserId(String userId);
    Optional<UserEntity> findByUserId(String userId);
    boolean existsByPhone(String phone);
}
