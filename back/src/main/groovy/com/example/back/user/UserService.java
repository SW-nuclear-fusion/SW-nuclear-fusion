package com.example.back.user;

import com.example.back.domain.User;
import com.example.back.domain.UserRepository;
import com.example.back.user.dto.PlantSelectionRequest;
import com.example.back.user.dto.UserInfoResponse;
import com.example.back.domain.Alarm;         // [!] Alarm import
import com.example.back.domain.AlarmRepository; // [!] AlarmRepository import
import com.example.back.domain.AlarmCheckLog;       // [!] 추가
import com.example.back.domain.AlarmCheckLogRepository; // [!] 추가
import java.time.LocalDate;
import jakarta.persistence.EntityNotFoundException; // [!] 추가
import java.util.Random;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // [!] 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    private final AlarmCheckLogRepository alarmCheckLogRepository;
    private final Random random = new Random();

    /**
     * 식물 정보 저장
     */
    @Transactional
    public void savePlantSelection(String userId, PlantSelectionRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));

        user.setPlantColor(request.getPlantColor());
        user.setPlantName(request.getPlantName());
        // @Transactional에 의해 변경 감지되어 자동 저장됨
    }

    /**
     * 현재 로그인된 사용자 정보 조회
     */
    public UserInfoResponse getCurrentUserInfo(String userId) {
        User user = findUserByUserId(userId);
        return new UserInfoResponse(user);
    }

    /** [!] 물 주기 */
    @Transactional
    public UserInfoResponse waterPlant(String userId) {
        User user = findUserByUserId(userId);
        boolean success = user.useWater(1);
        if (!success) {
            throw new IllegalArgumentException("물이 부족합니다.");
        }
        // 변경된 유저 정보 반환
        return new UserInfoResponse(user);
    }

    /** [!] 애정 주기 */
    @Transactional
    public UserInfoResponse giveAffection(String userId) {
        User user = findUserByUserId(userId);
        boolean success = user.useAffection(1);
        if (!success) {
            throw new IllegalArgumentException("애정도가 부족합니다.");
        }
        // 변경된 유저 정보 반환
        return new UserInfoResponse(user);
    }

    /**
     * 알람 체크(복용 완료) 처리 및 보상 지급
     */
    @Transactional
    public UserInfoResponse checkOffAlarm(String userId, Long alarmId) {
        User user = findUserByUserId(userId);
        Alarm alarm = findAlarmByIdAndUserId(alarmId, userId); // 본인 알람인지 확인
        LocalDate today = LocalDate.now();

        // 중복 체크 로직
        boolean alreadyChecked = alarmCheckLogRepository.existsByUser_UserIdAndAlarm_IdAndCheckDate(userId, alarmId, today);
        if (alreadyChecked) {
            System.out.println("User " + userId + " already checked alarm " + alarmId + " today. No reward given.");
            // 이미 체크했으면 보상 없이 현재 사용자 정보 반환 (또는 예외 발생)
            return new UserInfoResponse(user);
            // 또는 throw new IllegalStateException("이미 오늘 체크한 알람입니다.");
        }

        // 정시 복용 확인 로직
        LocalTime scheduledTime = alarm.getNotificationTime(); // 알람 설정 시간
        LocalTime currentTime = LocalTime.now().plusHours(9); // 현재 시간
        long gracePeriodMinutes = 30;

        LocalTime lowerBound = scheduledTime.minusMinutes(gracePeriodMinutes);
        LocalTime upperBound = scheduledTime.plusMinutes(gracePeriodMinutes);

        boolean isOnTime = false; // 정시 여부
        boolean isTooEarly = false; // 너무 이른 시간인지 여부

        // 현재 시간이 허용 시작 시간보다 이전인지 확인
        // 자정 넘는 경우 고려: lower=23:30, upper=00:30, current=23:00 -> isTooEarly=true
        // 자정 넘는 경우 고려: lower=23:30, upper=00:30, current=00:10 -> isTooEarly=false
        if (lowerBound.isAfter(upperBound)) { // 허용 범위가 자정을 넘는 경우
            isTooEarly = currentTime.isAfter(upperBound) && currentTime.isBefore(lowerBound);
            isOnTime = !isTooEarly; // 너무 이르지만 않으면 일단 정시 또는 지각
        } else { // 일반적인 경우
            isTooEarly = currentTime.isBefore(lowerBound);
            // 정시 조건: 시작 시간 <= 현재 시간 <= 종료 시간
            isOnTime = !isTooEarly && !currentTime.isAfter(upperBound);
        }

        if (isTooEarly) {
            System.out.println("User " + userId + " tried to check alarm " + alarmId + " too early." + "체크 시간" + currentTime);
            System.out.println("User " + userId + " tried to check alarm " + alarmId + " too early.");
            throw new IllegalArgumentException("아직 복약할 시간이 아닙니다.");
        }

        if (isOnTime) {
            // 랜덤 보상 계산 (1~3개의 물 또는 애정)
            int rewardAmount = random.nextInt(3) + 1; // 1, 2, 3 중 하나
            boolean giveWater = random.nextBoolean(); // true면 물, false면 애정

            if (giveWater) {
                user.addWater(rewardAmount);
                System.out.println("User " + userId + " received " + rewardAmount + " water for checking alarm " + alarmId); // 로그 추가
            } else {
                user.addAffection(rewardAmount);
                System.out.println("User " + userId + " received " + rewardAmount + " affection for checking alarm " + alarmId); // 로그 추가
            }
            AlarmCheckLog checkLog = AlarmCheckLog.builder()
                    .user(user)
                    .alarm(alarm)
                    .checkDate(today)
                    .build();
            alarmCheckLogRepository.save(checkLog);
            System.out.println("Alarm check log saved for user " + userId + ", alarm " + alarmId);
        } else { // isOnTime이 false이고 isTooEarly도 false인 경우
            System.out.println("User " + userId + " checked alarm " + alarmId + " off time. No reward given.");
            // 체크 로그 저장
            AlarmCheckLog checkLog = AlarmCheckLog.builder().user(user).alarm(alarm).checkDate(today).build();
            alarmCheckLogRepository.save(checkLog);
            System.out.println("Alarm check log saved (off time).");
        }

        // 업데이트된 사용자 정보 반환
        return new UserInfoResponse(user);
    }

    // --- Helper Method ---
    /** userId로 User 엔티티 찾는 공통 메서드 */
    private User findUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));
    }

    // [!] AlarmService에서 가져옴 (UserService로 통합)
    private Alarm findAlarmByIdAndUserId(Long alarmId, String userId) {
        return alarmRepository.findByIdAndUser_UserId(alarmId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Alarm not found or permission denied: " + alarmId));
    }
}