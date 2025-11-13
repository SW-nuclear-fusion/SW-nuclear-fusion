package com.example.back.user; // (새로운 service 패키지 또는 기존 user 패키지)

import com.example.back.domain.UserPlant;
import com.example.back.domain.UserPlantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j // (로그 출력을 위해)
@Service
@RequiredArgsConstructor
public class PlantSchedulerService {

    private final UserPlantRepository userPlantRepository;

    /**
     * [4번 목표] 월초 식물 경험치 초기화 스케줄러
     * - 매월 1일 00:00:00에 실행됩니다.
     * - (cron = "초 분 시 일 월 요일")
     */
    @Transactional
    @Scheduled(cron = "0 0 0 1 * ?") // 매월 1일 0시 0분
    public void resetMonthlyPlantExperience() {
        log.info("--- [스케줄러 시작] 월초 식물 경험치 초기화 ---");

        // 1. 모든 UserPlant 엔티티를 조회합니다.
        // (주의: 데이터가 매우 많다면 (수십만 개) Paging 처리가 필요할 수 있으나,
        //  현재 단계에서는 findAll()로 진행합니다.)
        List<UserPlant> allPlants = userPlantRepository.findAll();

        int count = 0;
        // 2. 각 식물의 resetExp() 메서드를 호출합니다.
        for (UserPlant plant : allPlants) {
            plant.resetExp(); // plantLevel=1, plantExp=0, isMaxLevel=false로 변경
            count++;
        }

        // @Transactional에 의해 변경 감지(dirty checking)되어
        // 메서드 종료 시 자동으로 모든 plant가 UPDATE 됩니다.

        log.info("--- [스케줄러 종료] 총 {}개의 식물 정보 초기화 완료 ---", count);
    }
}