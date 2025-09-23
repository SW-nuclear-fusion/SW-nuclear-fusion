package com.example.swnuclearfusionwas.domain.meds.service;

import com.example.swnuclearfusionwas.domain.meds.entity.MedSchedule;
import com.example.swnuclearfusionwas.domain.meds.entity.PushSubscription;
import com.example.swnuclearfusionwas.domain.meds.repository.MedScheduleRepository;
import com.example.swnuclearfusionwas.domain.meds.repository.PushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushScheduler {

    private final MedScheduleRepository medScheduleRepo;
    private final PushSubscriptionRepository pushRepo;
    private final PushService pushService;
    private final AlertService alertService;

    @Transactional(readOnly = false)
    @Scheduled(cron = "0 * * * * *")
    public void sendPushAlarms() {
        ZoneId zone = ZoneId.of("Asia/Seoul");
        LocalDateTime now = LocalDateTime.now(zone);
        LocalTime currentTime = now.toLocalTime().withSecond(0).withNano(0);
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        LocalDate today = now.toLocalDate();

        List<MedSchedule> schedules = medScheduleRepo.findByDayOfWeekAndTimeAndActive(dayOfWeek, currentTime, true);
        if (schedules.isEmpty()) return;

        for (MedSchedule schedule : schedules) {
            var med = schedule.getMedication();

            if (!Boolean.TRUE.equals(med.getAlertEnabled())) {
                continue;
            }

            Long userId = med.getUserId();

            try {
                log.debug("[SCHED] user={} medId={} schId={}",
                        userId, med.getId(), schedule.getId());
                Long eventId = alertService.dispatch(userId, schedule.getId(), today);

                List<PushSubscription> subs = pushRepo.findByUserId(userId);
                if (subs.isEmpty()) {
                    log.debug("No push subscribers for user {}", userId);
                    continue;
                }

                String title = "약 복용 알림";
                String body = med.getName() + " 복용 시간입니다. (" + dayOfWeek + " " + currentTime + ")";
                String url = "/meds/alert/complete?eventId=" + eventId;

                for (PushSubscription sub : subs) {
                    pushService.sendPush(sub, title, body, url);
                }

                log.info("Push dispatched user={} medId={} schId={} eventId={}",
                        userId, med.getId(), schedule.getId(), eventId);

            } catch (Exception e) {
                log.error("Push dispatch failed user={} schId={} err={}",
                        userId, schedule.getId(), e.getMessage(), e);
            }
        }
    }
}