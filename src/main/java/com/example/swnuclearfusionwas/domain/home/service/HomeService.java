package com.example.swnuclearfusionwas.domain.home.service;

import com.example.swnuclearfusionwas.domain.home.dto.*;
import com.example.swnuclearfusionwas.domain.inventory.service.InventoryService;
import com.example.swnuclearfusionwas.domain.plant.service.PlantService;
import com.example.swnuclearfusionwas.domain.user.entity.UserProfile;
import com.example.swnuclearfusionwas.domain.user.service.UserProfileService;
import com.example.swnuclearfusionwas.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final UserQueryService users;
    private final UserProfileService profiles;
    private final InventoryService inventory;
    private final PlantService plantService;

    @Transactional(readOnly = true)
    public HomeView summaryByUsername(String username){
        var u = users.getByUsernameOrThrow(username);
        UserProfile p = profiles.ensureProfile(u);

        var active = plantService.getActiveOrNull(u);
        var canNext = plantService.canSelectNext(u);

        var seedRows = inventory.listSeeds(u).stream().map(SeedRow::from).toList();
        var itemRows = inventory.listItems(u).stream().map(ItemRow::from).toList();

        return new HomeView(
                u.getName(),
                p.getLevel(),
                p.getPoints(),
                PlantDto.of(active),
                canNext,
                seedRows,
                itemRows,
                PlantService.MAX_LEVEL,
                nextRewardOf(p.getLevel())
        );
    }

    private String nextRewardOf(int level) {
        return (level >= PlantService.MAX_LEVEL) ? "최대 레벨 도달"
                : switch (level + 1) {
            case 2 -> "1렙 보상";
            case 3 -> "2렙 보상";
            case 4 -> "3렙 보상";
            case 5 -> "4렙 보상";
            case 6 -> "5렙 보상";
            case 7 -> "6렙 보상";
            case 8 -> "7렙 보상";
            case 9 -> "8렙 보상";
            case 10 -> "9렙 보상";
            default -> "보상 준비중";
        };
    }
}