package com.example.swnuclearfusionwas.domain.meds.repository;

import com.example.swnuclearfusionwas.domain.meds.entity.DoseEvent;
import com.example.swnuclearfusionwas.domain.meds.entity.MedSchedule;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

public interface DoseEventRepository extends JpaRepository<DoseEvent, Long> {
    Optional<DoseEvent> findByScheduleAndDate(MedSchedule schedule, LocalDate date);
    boolean existsBySchedule_Id(Long scheduleId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        delete from DoseEvent e
         where e.schedule.id in (
           select s.id from MedSchedule s
            where s.medication.id = :medId
         )
    """)
    int deleteAllByMedId(Long medId);
}