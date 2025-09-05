package com.example.swnuclearfusionwas.domain.user.service;

import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.user.entity.UserProfile;
import com.example.swnuclearfusionwas.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository profileRepo;

    @Transactional
    public UserProfile ensureProfile(UserEntity u){
        return profileRepo.findByUser(u).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUser(u);
            p.setLevel(1);
            p.setPoints(0);
            p.setSeedsOwned(1); //임의값
            return profileRepo.save(p);
        });
    }
}
