package com.example.back.user;

import com.example.back.domain.*;
import com.example.back.domain.Alarm;
import com.example.back.domain.AlarmRepository;
import com.example.back.domain.AlarmCheckLog;
import com.example.back.domain.AlarmCheckLogRepository;
import com.example.back.domain.UserPlantRepository;
import com.example.back.user.dto.*;
import com.example.back.domain.RewardVoucherRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Random;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // [!] 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.example.back.user.dto.RewardExchangeRequest;
import com.example.back.user.dto.RewardExchangeResponse;
import com.example.back.user.dto.RewardVoucherListResponse;
import java.util.stream.Collectors;
import com.example.back.user.dto.PlantActivateRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;
    private final AlarmCheckLogRepository alarmCheckLogRepository;
    private final UserPlantRepository userPlantRepository; // [신규] 주입
    private final RewardVoucherRepository rewardVoucherRepository;
    private final Random random = new Random();

    // --- [신규] 1. (1번 목표) 회원가입 시 식물 4종 지급 ---
    /**
     * 신규 사용자에게 기본 식물 4종을 생성하여 지급합니다.
     * (AuthService 등 회원가입 로직에서 호출되어야 함)
     */
    @Transactional
    public void initializeNewUserPlants(User user) {
        List<UserPlant> initialPlants = new ArrayList<>();

        // PlantType Enum의 모든 값(PURPLE, BLUE, YELLOW, PINK)을 순회
        for (PlantType type : PlantType.values()) {
            UserPlant plant = UserPlant.builder()
                    .user(user)
                    .plantType(type)
                    .build(); // exp=0, active=false가 기본값
            initialPlants.add(plant);
        }

        userPlantRepository.saveAll(initialPlants); // 4개 식물 한 번에 저장
    }

    @Transactional
    public UserInfoResponse activatePlant(String userId, PlantActivateRequest request) {
        User user = findUserByUserId(userId);

        // 1. (검증용) 현재 활성화된 식물을 찾음
        Optional<UserPlant> currentActiveOpt = userPlantRepository.findByUserAndIsActiveTrue(user);

        // 2. (검증용) 교체 대상 식물을 찾음
        UserPlant targetPlant = userPlantRepository.findByUser(user).stream()
                .filter(p -> p.getPlantType() == request.getPlantType())
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("사용자에게 해당 타입의 식물이 존재하지 않습니다: " + request.getPlantType()));

        // --- 검증 로직 시작 ---

        // 3. (검증 1) 활성화하려는 식물이 이미 활성화된 식물인가?
        if (targetPlant.isActive()) {
            throw new IllegalStateException("이미 활성화된 식물입니다.");
        }

        // 4. (검증 2) [신규 규칙] 현재 활성화된 식물이 존재하는데, 만렙이 아닌가?
        if (currentActiveOpt.isPresent() && !currentActiveOpt.get().isMaxLevel()) {
            throw new IllegalStateException("현재 식물을 만렙(Lv 3)까지 키워야 교체할 수 있습니다.");
        }

        // 5. (검증 3) [신규 규칙] 교체하려는 식물이 (이번 달에) 이미 만렙을 달성했었나?
        if (targetPlant.isMaxLevel()) {
            throw new IllegalStateException("이미 만렙을 달성한 식물입니다. 다른 식물을 선택해주세요.");
        }

        // --- 검증 통과: 교체 실행 ---

        // 6. 기존 활성화 식물이 있었다면 비활성화
        currentActiveOpt.ifPresent(plant -> plant.setActive(false));

        // 7. 새 식물 활성화 및 이름 변경
        targetPlant.setActive(true);
        targetPlant.updatePlantName(request.getPlantName());

        // 8. 변경된 정보로 DTO 반환
        return new UserInfoResponse(user, targetPlant);
    }

    /**
     * [수정] 현재 로그인된 사용자 정보 + 활성화된 식물 정보 조회
     */
    public UserInfoResponse getCurrentUserInfo(String userId) {
        User user = findUserByUserId(userId);
        // [수정] 활성화된 식물을 찾아서 함께 DTO로 전달
        UserPlant activePlant = findActivePlantByUser(user);

        // [수정] 생성자 변경 (user, activePlant)
        return new UserInfoResponse(user, activePlant);
    }

    // --- [신규 API 1] 사용자가 보유한 모든 식물 목록 조회 ---
    public List<UserPlantDto> getAllUserPlants(String userId) {
        User user = findUserByUserId(userId);
        List<UserPlant> plants = userPlantRepository.findByUser(user);
        return plants.stream()
                .map(UserPlantDto::new)
                .collect(Collectors.toList());
    }

    // --- [신규 API 2] 사용자가 보유한 모든 보상 교환권 목록 조회 ---
    public List<RewardVoucherDto> getAllUserVouchers(String userId) {
        User user = findUserByUserId(userId);
        List<RewardVoucher> vouchers = rewardVoucherRepository.findByUser(user);
        return vouchers.stream()
                .map(RewardVoucherDto::new)
                .collect(Collectors.toList());
    }

    // --- [신규 API 3] 사용자 정보 변경 (이름, 폰트 크기 등) ---
    @Transactional
    public UserInfoResponse updateUserInfo(String userId, UserUpdateRequest request) {
        User user = findUserByUserId(userId);

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.updateSocialInfo(request.getName());
        }
        if (request.getFontSize() != null) {
            user.setFontSize(request.getFontSize());
        }

        UserPlant activePlant = findActivePlantByUser(user);
        return new UserInfoResponse(user, activePlant);
    }

    // --- [신규 1] 포인트로 물품 교환 (POST /api/user/rewards/exchange 처리) ---
    @Transactional
    public RewardExchangeResponse exchangeReward(String userId, RewardExchangeRequest request) {
        User user = findUserByUserId(userId);

        // 1. 포인트 검증 및 사용 (User.java의 usePoints 메서드 사용)
        int price = request.getPrice();
        if (!user.usePoints(price)) {
            throw new IllegalStateException("포인트가 부족합니다. (필요: " + price + ")");
        }

        // 2. 교환권 생성 및 저장
        // RewardVoucher.java에는 voucherCode가 없으므로 voucherName만 저장
        RewardVoucher voucher = RewardVoucher.builder()
                .user(user)
                .voucherName(request.getItemName())
                .build();

        rewardVoucherRepository.save(voucher);

        // 3. 응답 DTO 반환
        UserPlant activePlant = findActivePlantByUser(user);
        UserInfoResponse updatedUserInfo = new UserInfoResponse(user, activePlant);
        RewardVoucherDto voucherDto = new RewardVoucherDto(voucher);

        return new RewardExchangeResponse(updatedUserInfo, voucherDto);
    }

    // --- [신규 2] 교환권 목록 조회 (GET /api/user/rewards/vouchers 처리) ---
    @Transactional(readOnly = true)
    public RewardVoucherListResponse getVouchersByStatus(String userId) {
        User user = findUserByUserId(userId);

        // RewardVoucherRepository의 findByUser 메서드를 사용하여 모든 교환권 조회
        List<RewardVoucher> allVouchers = rewardVoucherRepository.findByUser(user);

        // DTO로 변환
        List<RewardVoucherDto> allDto = allVouchers.stream()
                .map(RewardVoucherDto::new)
                .collect(Collectors.toList());

        // 사용 여부로 분류
        List<RewardVoucherDto> unusedDto = allDto.stream()
                .filter(v -> !v.isUsed())
                .collect(Collectors.toList());

        List<RewardVoucherDto> usedDto = allDto.stream()
                .filter(RewardVoucherDto::isUsed)
                .collect(Collectors.toList());

        return new RewardVoucherListResponse(allDto, unusedDto, usedDto);
    }

    /** [!] [수정] 물 주기 (활성화된 식물 경험치 UP) */
    @Transactional
    public UserInfoResponse waterPlant(String userId) {
        User user = findUserByUserId(userId);

        // 1. 재화 사용 (User 엔티티의 useWater 호출)
        boolean success = user.useWater(1);
        if (!success) {
            throw new IllegalArgumentException("물이 부족합니다.");
        }

        // 2. 활성화된 식물 경험치 증가
        UserPlant activePlant = findActivePlantByUser(user);
        if (activePlant != null) {
            activePlant.addExp(5); // [!] 1 물 = 5 경험치 (예시)
            // userPlantRepository.save(activePlant); // @Transactional이므로 생략 가능
        }

        // 변경된 유저 + 식물 정보 반환
        return new UserInfoResponse(user, activePlant);
    }

    /** [!] [수정] 애정 주기 (활성화된 식물 경험치 UP) */
    @Transactional
    public UserInfoResponse giveAffection(String userId) {
        User user = findUserByUserId(userId);

        // 1. 재화 사용
        boolean success = user.useAffection(1);
        if (!success) {
            throw new IllegalArgumentException("애정도가 부족합니다.");
        }

        // 2. 활성화된 식물 경험치 증가
        UserPlant activePlant = findActivePlantByUser(user);
        if (activePlant != null) {
            activePlant.addExp(10); // [!] 1 애정 = 10 경험치 (예시)
        }

        // 변경된 유저 + 식물 정보 반환
        return new UserInfoResponse(user, activePlant);
    }

    @Transactional
    public UserInfoResponse claimPlantPoints(String userId) {
        User user = findUserByUserId(userId);
        UserPlant activePlant = findActivePlantByUser(user);

        if (activePlant == null) {
            throw new IllegalStateException("활성화된 식물이 없습니다.");
        }

        // 검증 1: 만렙이 아닌가?
        if (!activePlant.isMaxLevel()) {
            throw new IllegalStateException("아직 만렙(Lv 3)이 아닙니다.");
        }

        // 검증 2: 이미 포인트를 수령했는가?
        if (activePlant.isPointsClaimed()) {
            throw new IllegalStateException("이미 포인트를 수령했습니다.");
        }

        // 포인트 지급 및 상태 변경
        user.addPoints(1000);
        activePlant.claimPoints();

        // (JPA가 @Transactional에 의해 자동 저장)

        // 최신 정보 반환
        return new UserInfoResponse(user, activePlant);
    }

    /**
     * [수정] 알람 체크(복용 완료) 처리 및 보상 지급
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
            // [수정] 이미 체크했으면 보상 없이 '현재' 사용자/식물 정보 반환
            UserPlant activePlant = findActivePlantByUser(user);
            return new UserInfoResponse(user, activePlant);
        }

        // ... (중략: 정시 복용 확인 로직은 동일) ...
        LocalTime scheduledTime = alarm.getNotificationTime(); // 알람 설정 시간
        LocalTime currentTime = LocalTime.now().plusHours(9); // 현재 시간
        long gracePeriodMinutes = 30;
        LocalTime lowerBound = scheduledTime.minusMinutes(gracePeriodMinutes);
        LocalTime upperBound = scheduledTime.plusMinutes(gracePeriodMinutes);
        boolean isOnTime = false; // 정시 여부
        boolean isTooEarly = false; // 너무 이른 시간인지 여부
        if (lowerBound.isAfter(upperBound)) { // 허용 범위가 자정을 넘는 경우
            isTooEarly = currentTime.isAfter(upperBound) && currentTime.isBefore(lowerBound);
            isOnTime = !isTooEarly; // 너무 이르지만 않으면 일단 정시 또는 지각
        } else { // 일반적인 경우
            isTooEarly = currentTime.isBefore(lowerBound);
            isOnTime = !isTooEarly && !currentTime.isAfter(upperBound);
        }
        if (isTooEarly) {
            System.out.println("User " + userId + " tried to check alarm " + alarmId + " too early." + "체크 시간" + currentTime);
            System.out.println("User " + userId + " tried to check alarm " + alarmId + " too early.");
            throw new IllegalArgumentException("아직 복약할 시간이 아닙니다.");
        }

        if (isOnTime) {
            // ... (중략: 보상 지급 로직 동일) ...
            int rewardAmount = random.nextInt(3) + 1;
            boolean giveWater = random.nextBoolean();
            if (giveWater) {
                user.addWater(rewardAmount);
            } else {
                user.addAffection(rewardAmount);
            }
            // ... (중략: 로그 저장 로직 동일) ...
            AlarmCheckLog checkLog = AlarmCheckLog.builder().user(user).alarm(alarm).checkDate(today).build();
            alarmCheckLogRepository.save(checkLog);
        } else {
            // ... (중략: 지각 시 로그 저장 로직 동일) ...
            AlarmCheckLog checkLog = AlarmCheckLog.builder().user(user).alarm(alarm).checkDate(today).build();
            alarmCheckLogRepository.save(checkLog);
        }

        // [수정] 업데이트된 사용자 정보 + 활성화된 식물 정보 반환
        UserPlant activePlant = findActivePlantByUser(user);
        return new UserInfoResponse(user, activePlant);
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

    // [신규] 활성화된 식물 조회 (없으면 null 반환)
    /** 사용자의 식물 중 현재 활성화된(is_active = true) 식물을 조회합니다. */
    private UserPlant findActivePlantByUser(User user) {
        // userPlantRepository에서 Spring Data JPA가 자동으로 쿼리 생성
        return userPlantRepository.findByUserAndIsActiveTrue(user)
                .orElse(null); // 활성화된 식물이 없으면 null 반환
    }
}