package com.example.swnuclearfusionwas.domain.inventory.repository;

import com.example.swnuclearfusionwas.domain.catalog.model.ItemType;
import com.example.swnuclearfusionwas.domain.inventory.entity.ItemInventory;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemInventoryRepository extends JpaRepository<ItemInventory, Long> {
    Optional<ItemInventory> findByUserAndType(UserEntity user, ItemType type);
    List<ItemInventory> findByUser(UserEntity user);
}