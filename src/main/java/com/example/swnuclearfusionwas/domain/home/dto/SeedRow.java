package com.example.swnuclearfusionwas.domain.home.dto;

import com.example.swnuclearfusionwas.domain.inventory.entity.SeedInventory;

public record SeedRow(String type, int quantity) {
    public static SeedRow from(SeedInventory s){ return new SeedRow(s.getType().name(), s.getQuantity()); }
}