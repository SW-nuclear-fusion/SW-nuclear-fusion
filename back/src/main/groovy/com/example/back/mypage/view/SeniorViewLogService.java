package com.example.back.mypage.view;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class SeniorViewLogService {

    private final SeniorViewLogRepository repository;

    public SeniorViewLogService(SeniorViewLogRepository repository) {
        this.repository = repository;
    }

    public long getViewCount(Long caregiverId, Long seniorId) {
        if (caregiverId == null || seniorId == null) return 0L;
        return repository.countByCaregiverIdAndSeniorId(caregiverId, seniorId);
    }

    public SeniorViewLog recordView(Long caregiverId, Long seniorId) {
        SeniorViewLog log = SeniorViewLog.builder()
                .caregiverId(caregiverId)
                .seniorId(seniorId)
                .viewedAt(LocalDateTime.now())
                .build();
        return repository.save(log);
    }
}

