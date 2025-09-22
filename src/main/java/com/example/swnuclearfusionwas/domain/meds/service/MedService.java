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
                                schRepo.findByMedication_IdAndActiveTrue(m.getId()).stream()
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
        List<MedSchedule> ss = schRepo.findByMedication_IdAndActiveTrue(m.getId());
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

            int freq = (req.getFrequencyPerDay()!=null) ? req.getFrequencyPerDay() : m.getFrequencyPerDay();
            List<String> times = (req.getTimes()!=null) ? req.getTimes() : Collections.emptyList();
            if (times.size()!=freq)
                throw new IllegalArgumentException("times length must equal frequencyPerDay");

            boolean every = req.getEveryDay()==null || req.getEveryDay();
            Set<DayOfWeek> days = every
                    ? EnumSet.allOf(DayOfWeek.class)
                    : new LinkedHashSet<>(Optional.ofNullable(req.getDaysOfWeek())
                    .orElseThrow(() -> new IllegalArgumentException("daysOfWeek required when everyDay=false")));

            record Key(DayOfWeek d, LocalTime t) {}
            LinkedHashSet<Key> target = new LinkedHashSet<>();
            for (DayOfWeek d : days) for (String t : times) target.add(new Key(d, LocalTime.parse(t)));

            List<MedSchedule> existing = schRepo.findByMedication_IdAndActiveTrue(m.getId());
            Map<Key, MedSchedule> existMap = new HashMap<>();
            for (MedSchedule s : existing) existMap.put(new Key(s.getDayOfWeek(), s.getTime()), s);

            Set<Key> current = new LinkedHashSet<>(existMap.keySet());
            Set<Key> keep = new LinkedHashSet<>(current); keep.retainAll(target);
            LinkedHashSet<Key> toDelete = new LinkedHashSet<>(current); toDelete.removeAll(keep);
            LinkedHashSet<Key> toAdd    = new LinkedHashSet<>(target);  toAdd.removeAll(keep);

            Iterator<Key> delIt = toDelete.iterator();
            Iterator<Key> addIt = toAdd.iterator();
            while (delIt.hasNext() && addIt.hasNext()) {
                Key from = delIt.next();
                Key to   = addIt.next();
                MedSchedule s = existMap.get(from);
                s.setDayOfWeek(to.d());
                s.setTime(to.t());
                delIt.remove();
                addIt.remove();
            }

            for (Key k : toAdd) {
                schRepo.save(MedSchedule.builder()
                        .medication(m)
                        .dayOfWeek(k.d())
                        .time(k.t())
                        .active(true)
                        .build());
            }

            List<Long> deletableIds = new ArrayList<>();
            for (Key k : toDelete) {
                MedSchedule s = existMap.get(k);
                boolean hasEvents = eventRepo.existsBySchedule_Id(s.getId());
                if (hasEvents) {
                    s.setActive(false);
                } else {
                    deletableIds.add(s.getId());
                }
            }
            if (!deletableIds.isEmpty()) {
                schRepo.deleteAllByIdInBatch(deletableIds);
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