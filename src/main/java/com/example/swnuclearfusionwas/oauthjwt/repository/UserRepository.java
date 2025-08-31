package com.example.swnuclearfusionwas.oauthjwt.repository;

import com.example.swnuclearfusionwas.oauthjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

}
