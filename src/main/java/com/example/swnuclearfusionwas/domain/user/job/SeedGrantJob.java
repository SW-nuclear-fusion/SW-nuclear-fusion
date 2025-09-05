package com.example.swnuclearfusionwas.domain.user.job;

import com.example.swnuclearfusionwas.domain.user.entity.UserProfile;
import com.example.swnuclearfusionwas.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component @RequiredArgsConstructor
public class SeedGrantJob {
    private final UserProfileRepository profileRepo;

    // 매일 새벽 1시, 15일이면 지급
    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void grantMonthlySeed(){
        if (LocalDate.now().getDayOfMonth() != 15) return;
        profileRepo.findAll().forEach(p -> {
            p.setSeedsOwned(p.getSeedsOwned()+1);
            profileRepo.save(p);
        });
    }
}
