package com.example.back.mypage;

import com.example.back.domain.*;
import com.example.back.mypage.dto.DailyDataDto;
import com.example.back.mypage.dto.MedicationCheckDto;
import com.example.back.mypage.dto.MoodRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;
    private final AlarmCheckLogRepository alarmCheckLogRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final DailyMoodRepository dailyMoodRepository;

    /**
     * 월간 달력 데이터 조회
     */
    public List<DailyDataDto> getMonthlyData(String userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 1. 월간 복약 기록 조회 (DTO로 받음)
        List<MedicationCheckDto> medLogs = alarmCheckLogRepository.findMedicationLogsByDateRange(userId, startDate, endDate);
        // 날짜별로 복용약 리스트 묶기
        Map<LocalDate, List<String>> medsByDate = medLogs.stream()
                .collect(Collectors.groupingBy(
                        MedicationCheckDto::getCheckDate,
                        Collectors.mapping(MedicationCheckDto::getMedicationName, Collectors.toList())
                ));

        // 2. 월간 퀴즈 기록 조회
        List<QuizAttempt> quizLogs = quizAttemptRepository.findAllByUser_UserIdAndAttemptDateBetween(userId, startDate, endDate);
        Map<LocalDate, Integer> quizScoresByDate = quizLogs.stream()
                .collect(Collectors.toMap(QuizAttempt::getAttemptDate, QuizAttempt::getCorrectCount));

        List<DailyMood> moodLogs = dailyMoodRepository.findAllByUser_UserIdAndMoodDateBetween(userId, startDate, endDate);

        Map<LocalDate, String> moodsByDate = moodLogs.stream()
                .collect(Collectors.toMap(DailyMood::getMoodDate, DailyMood::getMoodIcon));

        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> DailyDataDto.builder()
                        .date(date)
                        .medicationsTaken(medsByDate.getOrDefault(date, List.of()))
                        .quizCorrectCount(quizScoresByDate.get(date))
                        .moodIcon(moodsByDate.get(date))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 오늘 감정 기록/수정
     */
    @Transactional
    public DailyDataDto saveOrUpdateDailyMood(String userId, MoodRequestDto request) {
        User user = findUserByUserId(userId);
        LocalDate today = LocalDate.now();

        // 1. 오늘 날짜로 기존 기록이 있는지 확인
        Optional<DailyMood> existingMood = dailyMoodRepository.findByUser_UserIdAndMoodDate(userId, today);

        DailyMood savedMood;
        if (existingMood.isPresent()) {
            // 2a. 기존 기록이 있으면: 아이콘 업데이트
            DailyMood mood = existingMood.get();
            mood.updateMood(request.getMoodIcon());
            savedMood = mood; // @Transactional에 의해 자동 저장 (save 호출 불필요)
        } else {
            // 2b. 기존 기록이 없으면: 새로 생성
            DailyMood newMood = DailyMood.builder()
                    .user(user)
                    .moodDate(today)
                    .moodIcon(request.getMoodIcon())
                    .build();
            savedMood = dailyMoodRepository.save(newMood);
        }

        // 3. 저장/수정된 정보로 DTO 만들어 반환 (다른 기록은 포함 X)
        return DailyDataDto.builder()
                .date(today)
                .moodIcon(savedMood.getMoodIcon())
                .medicationsTaken(List.of()) // 이 API는 감정만 반환
                .build();
    }

    private User findUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
    }
}