package com.example.swnuclearfusionwas.domain.plant.repository;

import com.example.swnuclearfusionwas.domain.catalog.model.PlantState;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.plant.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    long countByOwnerAndState(UserEntity owner, PlantState state);
    Optional<Plant> findFirstByOwnerAndState(UserEntity owner, PlantState state);
    List<Plant> findByOwner(UserEntity owner);
}