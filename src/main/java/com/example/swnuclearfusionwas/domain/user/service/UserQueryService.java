package com.example.swnuclearfusionwas.domain.user.service;

import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.oauthjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepo;

    // username으로 조회
    public UserEntity getByUsernameOrThrow(String username) {
        UserEntity u = userRepo.findByUsername(username);
        if (u == null) {
            throw new IllegalArgumentException("user not found by username: " + username);
        }
        return u;
    }

    // pk로 조회
    public UserEntity getByIdOrThrow(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("user not found by id: " + id));
    }

    // userId 존재 여부 검증
    public boolean existsByUserId(String userId) {
        return userRepo.existsByUserId(userId);
    }
}
