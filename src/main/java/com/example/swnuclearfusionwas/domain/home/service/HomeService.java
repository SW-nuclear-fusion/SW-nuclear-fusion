package com.example.swnuclearfusionwas.domain.home.service;

import com.example.swnuclearfusionwas.domain.home.dto.HomeView;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.plant.repository.PlantRepository;
import com.example.swnuclearfusionwas.domain.user.entity.UserProfile;
import com.example.swnuclearfusionwas.domain.user.service.UserProfileService;
import com.example.swnuclearfusionwas.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class HomeService {
    private final UserQueryService userQuery;
    private final UserProfileService profileService;
    private final PlantRepository plantRepo;

    @Transactional(readOnly = true)
    public HomeView viewByUsername(String username){
        UserEntity u = userQuery.getByUsernameOrThrow(username);
        UserProfile p = profileService.ensureProfile(u);
        var plants = plantRepo.findByOwner(u);
        return HomeView.of(p, u.getUsername(), plants);
    }

    @Transactional(readOnly = true)
    public HomeView viewByUserId(Long userId){
        UserEntity u = userQuery.getByIdOrThrow(userId);
        UserProfile p = profileService.ensureProfile(u);
        var plants = plantRepo.findByOwner(u);
        return HomeView.of(p, u.getUsername(), plants);
    }
}
