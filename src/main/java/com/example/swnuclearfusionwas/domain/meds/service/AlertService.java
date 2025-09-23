package com.example.swnuclearfusionwas.domain.meds.service;

import com.example.swnuclearfusionwas.domain.meds.entity.DoseEvent;
import com.example.swnuclearfusionwas.domain.meds.entity.MedSchedule;
import com.example.swnuclearfusionwas.domain.meds.entity.Medication;
import com.example.swnuclearfusionwas.domain.meds.repository.DoseEventRepository;
import com.example.swnuclearfusionwas.domain.meds.repository.MedScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final MedScheduleRepository schRepo;
    private final DoseEventRepository eventRepo;

    @Transactional
    public Long dispatch(Long userId, Long scheduleId, LocalDate date) {
        MedSchedule s = schRepo.findById(scheduleId).orElseThrow(() -> new IllegalArgumentException("schedule not found"));
        Medication m = s.getMedication();
        if (!m.getUserId().equals(userId)) throw new IllegalArgumentException("forbidden");

        DoseEvent ev = eventRepo.findByScheduleAndDate(s, date)
                .orElseGet(() -> eventRepo.save(DoseEvent.builder().schedule(s).date(date).taken(false).build()));

        return ev.getId();
    }

    @Transactional
    public void complete(Long userId, Long eventId) {
        DoseEvent ev = eventRepo.findById(eventId).orElseThrow(() -> new IllegalArgumentException("event not found"));
        if (!ev.getSchedule().getMedication().getUserId().equals(userId))
            throw new IllegalArgumentException("forbidden");
        ev.setTaken(true);
        ev.setTakenAt(OffsetDateTime.now(ZoneId.of("Asia/Seoul")));
    }
}