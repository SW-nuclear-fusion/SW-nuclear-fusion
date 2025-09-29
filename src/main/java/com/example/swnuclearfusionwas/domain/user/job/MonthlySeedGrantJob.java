package com.example.swnuclearfusionwas.domain.user.job;

import com.example.swnuclearfusionwas.domain.catalog.model.SeedType;
import com.example.swnuclearfusionwas.domain.inventory.service.InventoryService;
import com.example.swnuclearfusionwas.domain.oauthjwt.repository.UserRepository;
import com.example.swnuclearfusionwas.domain.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class MonthlySeedGrantJob {

    private final UserRepository userRepo;
    private final UserProfileRepository profileRepo;
    private final InventoryService inventory;

    private static final int MONTHLY_SEED_COUNT = 4;

    //매월 1일 00:05 씨앗 지급
    @Scheduled(cron = "0 5 0 1 * *", zone = "Asia/Seoul")
    @Transactional
    public void grantSeeds() {
        var today = LocalDate.now();
        userRepo.findAll().forEach(u -> {
            var p = profileRepo.findByUser(u).orElse(null);
            if (p == null) return;
            if (p.getLastMonthlySeedAt() != null
                    && p.getLastMonthlySeedAt().getYear() == today.getYear()
                    && p.getLastMonthlySeedAt().getMonth() == today.getMonth()) {
                return; // 이미 지급됨
            }
            for (int i = 0; i < MONTHLY_SEED_COUNT; i++) {
                var types = SeedType.values();
                var type = types[ThreadLocalRandom.current().nextInt(types.length)];
                inventory.addSeed(u, type, 1);
            }
            p.setLastMonthlySeedAt(today);
            profileRepo.save(p);
        });
    }
}