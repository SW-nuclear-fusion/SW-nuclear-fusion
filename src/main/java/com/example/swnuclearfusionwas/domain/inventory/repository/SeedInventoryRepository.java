package com.example.swnuclearfusionwas.domain.inventory.repository;

import com.example.swnuclearfusionwas.domain.catalog.model.SeedType;
import com.example.swnuclearfusionwas.domain.inventory.entity.SeedInventory;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeedInventoryRepository extends JpaRepository<SeedInventory, Long> {
    Optional<SeedInventory> findByUserAndType(UserEntity user, SeedType type);
    List<SeedInventory> findByUser(UserEntity user);
}