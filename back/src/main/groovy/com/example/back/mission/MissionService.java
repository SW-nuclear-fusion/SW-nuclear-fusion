package com.example.back.mission; // mission 패키지 생성

import com.example.back.domain.*;
import com.example.back.mission.dto.*; // DTO 필요 (다음 단계에서 생성)
import com.example.back.user.UserService; // UserService 주입
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final UserRepository userRepository; // User 엔티티 조회용
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final LocationMissionRepository locationMissionRepository;
    private final MissionLogRepository missionLogRepository;
    private final Random random = new Random();

    /** 오늘 풀 퀴즈 5개 가져오기 */
    public List<QuizQuestionDto> getDailyQuiz(String userId) {
        // 1. 오늘 이미 퀴즈를 풀었는지 확인
        if (quizAttemptRepository.existsByUser_UserIdAndAttemptDate(userId, LocalDate.now())) {
            throw new IllegalStateException("오늘의 퀴즈를 이미 완료했습니다.");
        }
        // 2. 랜덤 퀴즈 5개 조회
        List<QuizQuestion> questions = quizQuestionRepository.findRandom5Questions();
        // 3. DTO로 변환 (정답 제외)
        return questions.stream()
                .map(QuizQuestionDto::new) // DTO 생성자 (엔티티 -> DTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuizQuestionDto> getPracticeQuizByCategory(String category) {
        // 1. 레포지토리에서 카테고리별 랜덤 5개 퀴즈 조회
        List<QuizQuestion> questions = quizQuestionRepository.findRandomQuestionsByCategory(category);

        // 2. DTO로 변환
        return questions.stream()
                .map(QuizQuestionDto::new) // (QuizQuestionDto가 QuizQuestion을 인자로 받는 생성자 필요)
                .collect(Collectors.toList());
    }

    /** 퀴즈 답안 제출 및 채점 */
    @Transactional
    public QuizResultDto submitQuiz(String userId, List<QuizAnswerDto> answers) {
        User user = findUserByUserId(userId);
        LocalDate today = LocalDate.now();
        if (quizAttemptRepository.existsByUser_UserIdAndAttemptDate(userId, today)) {
            throw new IllegalStateException("퀴즈를 이미 제출했습니다.");
        }

        int correctCount = 0;
        // 채점
        for (QuizAnswerDto answer : answers) {
            QuizQuestion question = quizQuestionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid question ID"));
            if (question.getCorrectAnswer().equals(answer.getSubmittedAnswer())) {
                correctCount++;
            }
        }

        // 퀴즈 시도 기록 저장
        QuizAttempt attempt = QuizAttempt.builder()
                .user(user)
                .correctCount(correctCount)
                .totalCount(answers.size())
                .build();
        quizAttemptRepository.save(attempt);

        // 보상 지급
        RewardDto reward = null;
        if (correctCount > 0) {
            user.addWater(correctCount); // User 엔티티에 addWater 메서드가 있다고 가정
            reward = new RewardDto("water", correctCount);
        }

        // 5. 결과 반환
        return new QuizResultDto(correctCount, answers.size(), reward);
    }


    // 방문 미션 관련
    // ---------------------------------

    /** 모든 방문 미션 목록 조회 */
    public List<LocationMission> getAllLocationMissions() {
        return locationMissionRepository.findAll();
    }

    /** 방문 인증 시도 */
    @Transactional
    public RewardDto completeVisitMission(String userId, Long missionId, VisitRequestDto visitRequest) {
        User user = findUserByUserId(userId);
        LocalDate today = LocalDate.now();
        // 1. 오늘 이미 완료한 미션인지 확인
        if (missionLogRepository.existsByUser_UserIdAndMission_IdAndCompletionDate(userId, missionId, today)) {
            throw new IllegalStateException("오늘 이미 완료한 방문 미션입니다.");
        }

        // 2. 미션 정보 조회
        LocationMission mission = locationMissionRepository.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid mission ID"));

        // 3. 거리 계산 (Haversine formula)
        double distance = calculateDistance(
                visitRequest.getLatitude(), visitRequest.getLongitude(),
                mission.getLatitude(), mission.getLongitude()
        );

        // 4. 거리 확인 (미터 단위)
        if (distance <= mission.getRadiusMeters()) {
            // 5. 성공: 로그 저장 및 보상 지급
            MissionLog log = MissionLog.builder().user(user).mission(mission).build();
            missionLogRepository.save(log);

            int rewardAmount = random.nextInt(3) + 1; // 1~3개
            user.addAffection(rewardAmount); // 예: 방문 미션은 애정도 지급

            return new RewardDto("affection", rewardAmount);
        } else {
            // 6. 실패: 너무 멈
            throw new IllegalArgumentException("인증 장소에서 너무 멉니다. (거리: " + (int)distance + "m)");
        }
    }


    // --- Helper Methods ---
    private User findUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
    }

    // Haversine 공식 (GPS 거리 계산)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // 미터(m) 단위로 변환
    }
}