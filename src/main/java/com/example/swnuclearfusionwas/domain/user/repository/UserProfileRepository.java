package com.example.swnuclearfusionwas.domain.user.repository;

import com.example.swnuclearfusionwas.domain.user.entity.UserProfile;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUser(UserEntity user);
}
