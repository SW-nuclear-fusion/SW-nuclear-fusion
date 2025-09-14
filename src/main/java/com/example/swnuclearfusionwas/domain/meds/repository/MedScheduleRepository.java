package com.example.swnuclearfusionwas.domain.meds.repository;

import com.example.swnuclearfusionwas.domain.meds.entity.MedSchedule;
import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface MedScheduleRepository extends JpaRepository<MedSchedule, Long> {

    List<MedSchedule> findByMedication_Id(Long medId);

    List<MedSchedule> findByDayOfWeekAndTime(DayOfWeek dayOfWeek, LocalTime time);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from MedSchedule s where s.medication.id = :medId")
    int deleteAllByMedId(Long medId);
}