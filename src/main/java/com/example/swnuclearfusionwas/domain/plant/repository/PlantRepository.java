package com.example.swnuclearfusionwas.domain.plant.repository;

import com.example.swnuclearfusionwas.domain.plant.entity.Plant;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    List<Plant> findByOwner(UserEntity owner);
    long countByOwner(UserEntity owner);
}
