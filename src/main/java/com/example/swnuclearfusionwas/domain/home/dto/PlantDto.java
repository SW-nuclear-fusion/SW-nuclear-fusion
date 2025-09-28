package com.example.swnuclearfusionwas.domain.home.dto;

import com.example.swnuclearfusionwas.domain.plant.entity.Plant;

public record PlantDto(
        Long id, String seedType, String state,
        String nickname, int level, int exp
) {
    public static PlantDto of(Plant p){
        if (p == null) return null;
        return new PlantDto(
                p.getId(),
                p.getSeedType().name(),
                p.getState().name(),
                p.getNickname(),
                p.getLevel(),
                p.getExp()
        );
    }
}