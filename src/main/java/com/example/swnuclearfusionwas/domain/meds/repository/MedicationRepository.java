package com.example.swnuclearfusionwas.domain.meds.repository;

import com.example.swnuclearfusionwas.domain.meds.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    @Query("select distinct m from Medication m left join fetch m.schedules where m.userId = :userId")
    List<Medication> findAllWithSchedulesByUserId(Long userId);
    Optional<Medication> findByIdAndUserId(Long id, Long userId);
}