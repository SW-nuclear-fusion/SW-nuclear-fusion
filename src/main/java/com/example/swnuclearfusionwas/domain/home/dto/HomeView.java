package com.example.swnuclearfusionwas.domain.home.dto;

import java.util.List;

public record HomeView(
        String username,
        int userLevel,
        int userPoints,
        PlantDto activePlant,
        boolean canSelectNext,
        List<SeedRow> seedInventory,
        List<ItemRow> itemInventory,
        int maxLevel,
        String nextReward
) {}