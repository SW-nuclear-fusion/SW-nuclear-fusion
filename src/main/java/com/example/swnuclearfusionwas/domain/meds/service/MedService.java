package com.example.swnuclearfusionwas.domain.meds.service;

import com.example.swnuclearfusionwas.domain.meds.dto.MedCreateReq;
import com.example.swnuclearfusionwas.domain.meds.dto.MedDetailDto;
import com.example.swnuclearfusionwas.domain.meds.dto.MedListItemDto;
import com.example.swnuclearfusionwas.domain.meds.dto.MedUpdateReq;
import com.example.swnuclearfusionwas.domain.meds.entity.MedSchedule;
import com.example.swnuclearfusionwas.domain.meds.entity.Medication;
import com.example.swnuclearfusionwas.domain.meds.repository.DoseEventRepository;
import com.example.swnuclearfusionwas.domain.meds.repository.MedScheduleRepository;
import com.example.swnuclearfusionwas.domain.meds.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MedService {

    private final MedicationRepository medRepo;
    public final MedScheduleRepository schRepo;
    public final DoseEventRepository eventRepo;

    private static LocalTime parseTime(String t) {
        if (t == null || !t.matches("^[0-2][0-9]:[0-5][0-9]$"))
            throw new IllegalArgumentException("time must be HH:mm");
        return LocalTime.parse(t);
    }

    @Transactional
    public Long create(Long userId, MedCreateReq req) {
        if (req.getName()==null || req.getName().isBlank()) throw new IllegalArgumentException("name required");
        if (req.getFrequencyPerDay()==null || req.getFrequencyPerDay()<1) throw new IllegalArgumentException("frequencyPerDay required");
        if (req.getTimes()==null || req.getTimes().size()!=req.getFrequencyPerDay()) throw new IllegalArgumentException("times length must equal frequencyPerDay");

        Set<DayOfWeek> days = new LinkedHashSet<>();
        if (Boolean.TRUE.equals(req.getEveryDay())) {
            days.addAll(Arrays.asList(DayOfWeek.values()));
        } else {
            if (req.getDaysOfWeek()==null || req.getDaysOfWeek().isEmpty()) throw new IllegalArgumentException("daysOfWeek required when everyDay=false");
            days.addAll(req.getDaysOfWeek());
        }

        Medication med = medRepo.save(
                Medication.builder()
                        .userId(userId)
                        .name(req.getName())
                        .frequencyPerDay(req.getFrequencyPerDay())
                        .build()
        );

        for (DayOfWeek d : days) {
            for (String t : req.getTimes()) {
                schRepo.save(MedSchedule.builder()
                        .medication(med)
                        .dayOfWeek(d)
                        .time(parseTime(t))
                        .build());
            }
        }
        return med.getId();
    }

    @Transactional(readOnly = true)
    public List<MedListItemDto> list(Long userId) {
        return medRepo.findAllWithSchedulesByUserId(userId).stream()
                .map(m -> MedListItemDto.builder()
                        .medId(m.getId())
                        .name(m.getName())
                        .frequencyPerDay(m.getFrequencyPerDay())
                        .alertEnabled(m.getAlertEnabled())
                        .schedules(
                                m.getSchedules().stream()
                                        .sorted(Comparator.comparing(MedSchedule::getDayOfWeek).thenComparing(MedSchedule::getTime))
                                        .map(s -> s.getDayOfWeek().name() + " " + s.getTime())
                                        .toList()
                        ).build())
                .toList();
    }

    @Transactional(readOnly = true)
    public MedDetailDto detail(Long userId, Long medId) {
        Medication m = medRepo.findByIdAndUserId(medId, userId)
                .orElseThrow(() -> new IllegalArgumentException("not found"));
        List<MedSchedule> ss = schRepo.findByMedication_Id(m.getId());
        return MedDetailDto.builder()
                .medId(m.getId()).name(m.getName())
                .frequencyPerDay(m.getFrequencyPerDay())
                .alertEnabled(m.getAlertEnabled())
                .schedules(ss.stream()
                        .sorted(Comparator.comparing(MedSchedule::getDayOfWeek).thenComparing(MedSchedule::getTime))
                        .map(s -> s.getDayOfWeek().name() + " " + s.getTime()).toList())
                .build();
    }

    @Transactional
    public void update(Long userId, Long medId, MedUpdateReq req) {
        Medication m = medRepo.findByIdAndUserId(medId, userId)
                .orElseThrow(() -> new IllegalArgumentException("not found"));

        if (req.getName()!=null) m.setName(req.getName());
        if (req.getFrequencyPerDay()!=null) m.setFrequencyPerDay(req.getFrequencyPerDay());
        if (req.getTimes()!=null || req.getEveryDay()!=null || req.getDaysOfWeek()!=null) {

            eventRepo.deleteAllByMedId(m.getId());
            schRepo.deleteAllByMedId(m.getId());

            int freq = (req.getFrequencyPerDay()!=null) ? req.getFrequencyPerDay() : m.getFrequencyPerDay();
            List<String> times = (req.getTimes()!=null) ? req.getTimes() : List.of();
            if (times.size()!=freq)
                throw new IllegalArgumentException("times length must equal frequencyPerDay");

            boolean every = req.getEveryDay()==null || req.getEveryDay();
            Set<DayOfWeek> days = every
                    ? EnumSet.allOf(DayOfWeek.class)
                    : new LinkedHashSet<>(Optional.ofNullable(req.getDaysOfWeek())
                    .orElseThrow(() -> new IllegalArgumentException("daysOfWeek required when everyDay=false")));

            for (DayOfWeek d : days) {
                for (String t : times) {
                    schRepo.save(MedSchedule.builder()
                            .medication(m)
                            .dayOfWeek(d)
                            .time(parseTime(t))
                            .build());
                }
            }
        }
    }

    @Transactional
    public void delete(Long userId, Long medId) {
        Medication m = medRepo.findByIdAndUserId(medId, userId)
                .orElseThrow(() -> new IllegalArgumentException("not found"));

        eventRepo.deleteAllByMedId(medId);
        schRepo.deleteAllByMedId(medId);
        medRepo.delete(m);
    }

    @Transactional
    public void toggleAlert(Long userId, Long medId, boolean enabled) {
        Medication m = medRepo.findByIdAndUserId(medId, userId)
                .orElseThrow(() -> new IllegalArgumentException("not found"));
        m.setAlertEnabled(enabled);
    }
}