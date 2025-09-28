package com.example.swnuclearfusionwas.domain.home.dto;

import com.example.swnuclearfusionwas.domain.inventory.entity.ItemInventory;

public record ItemRow(String type, int quantity) {
    public static ItemRow from(ItemInventory i){ return new ItemRow(i.getType().name(), i.getQuantity()); }
}