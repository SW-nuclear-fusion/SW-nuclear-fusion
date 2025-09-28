package com.example.swnuclearfusionwas.domain.user.service;

import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.user.entity.UserProfile;
import com.example.swnuclearfusionwas.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository profileRepo;

    @Transactional
    public UserProfile ensureProfile(UserEntity user) {
        return profileRepo.findByUser(user).orElseGet(() -> {
            var p = new UserProfile();
            p.setUser(user);
            return profileRepo.save(p);
        });
    }
}