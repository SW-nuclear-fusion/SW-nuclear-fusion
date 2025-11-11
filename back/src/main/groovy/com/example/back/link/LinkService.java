package com.example.back.link;
import com.example.back.domain.*;
import com.example.back.link.dto.*;
import com.example.back.mypage.dto.MedicationCheckDto;
import com.example.back.domain.DailyMoodRepository;
import com.example.back.domain.QuizAttemptRepository;
import com.example.back.domain.AlarmCheckLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // 클래스 레벨 트랜잭션 (기본값)
public class LinkService {

    private final UserRepository userRepository;
    private final SeniorGuardianLinkRepository linkRepository;

    // --- MyPageService의 의존성(Repository) 주입 ---
    private final AlarmCheckLogRepository alarmCheckLogRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final DailyMoodRepository dailyMoodRepository;

    /**
     * (보호자) 시니어에게 연결을 요청합니다.
     * @param guardianUserId (인증된 보호자의 userId)
     * @param requestDto (시니어의 전화번호가 담긴 DTO)
     */
    @Transactional // (데이터 변경이 있으므로)
    public void requestLink(String guardianUserId, LinkRequestDto requestDto) {

        // 1. 요청 보낸 보호자(본인) 정보 조회
        User guardian = userRepository.findByUserId(guardianUserId)
                .orElseThrow(() -> new RuntimeException("현재 로그인된 사용자 정보를 찾을 수 없습니다."));

        // 2. 보호자 계정이 맞는지 확인
        if (guardian.getRole() != RoleType.GUARDIAN) {
            throw new RuntimeException("보호자 계정만 연결 요청을 보낼 수 있습니다.");
        }

        // 3. 요청 대상 시니어 정보 조회 (전화번호 기준)
        User senior = userRepository.findByPhone(requestDto.getSeniorPhoneNumber())
                .orElseThrow(() -> new RuntimeException("해당 전화번호를 가진 시니어 사용자를 찾을 수 없습니다."));

        // 4. 시니어 계정이 맞는지 확인
        if (senior.getRole() != RoleType.SENIOR) {
            throw new RuntimeException("시니어 계정에만 연결을 요청할 수 있습니다.");
        }

        // 5. 자기 자신에게 요청하는지 확인
        if (guardian.getId().equals(senior.getId())) {
            throw new RuntimeException("자기 자신에게 연결을 요청할 수 없습니다.");
        }

        // 6. 이미 연결 요청이 있는지 확인 (중복 방지)
        linkRepository.findByGuardianAndSenior(guardian, senior).ifPresent(link -> {
            if (link.getStatus() == LinkStatus.PENDING) {
                throw new RuntimeException("이미 연결을 요청한 상태입니다. 승인을 기다려주세요.");
            } else if (link.getStatus() == LinkStatus.APPROVED) {
                throw new RuntimeException("이미 연결된 시니어입니다.");
            }
            // (REJECTED 상태는 다시 요청 가능하도록 함)
        });

        // 7. 새로운 연결(Link) 객체 생성 (상태: PENDING)
        SeniorGuardianLink newLink = SeniorGuardianLink.builder()
                .guardian(guardian)
                .senior(senior)
                .status(LinkStatus.PENDING)
                .build();

        // 8. DB에 저장
        linkRepository.save(newLink);

        // 9. (TODO) 시니어에게 푸시 알림 또는 SMS 전송 로직
    }

    /**
     * (시니어) 본인에게 온 'PENDING' 상태의 연결 요청 목록을 조회합니다.
     * [!] N+1 최적화 적용
     */
    @Transactional(readOnly = true) // 조회 전용 트랜잭션
    public List<LinkPendingResponseDto> getPendingRequests(String seniorUserId) {

        // 1. 시니어(본인) 정보 조회
        User senior = userRepository.findByUserId(seniorUserId)
                .orElseThrow(() -> new RuntimeException("현재 로그인된 사용자 정보를 찾을 수 없습니다."));

        // 2. [!] N+1 최적화 쿼리 사용 (Repository에 findBySeniorAndStatusWithGuardian 필요)
        List<SeniorGuardianLink> pendingLinks = linkRepository.findBySeniorAndStatusWithGuardian(senior, LinkStatus.PENDING);

        // 3. DTO 리스트로 변환하여 반환
        return pendingLinks.stream()
                .map(LinkPendingResponseDto::new) // (link) -> new LinkPendingResponseDto(link)
                .collect(Collectors.toList());
    }

    /**
     * (시니어) 본인에게 온 연결 요청을 승인하거나 거절합니다.
     * @param seniorUserId (현재 로그인한 시니어의 userId)
     * @param respondDto (처리할 linkId, action)
     */
    @Transactional // (데이터 변경이 있으므로)
    public void respondToLinkRequest(String seniorUserId, LinkRespondDto respondDto) {

        // 1. 시니어(본인) 정보 조회
        User senior = userRepository.findByUserId(seniorUserId)
                .orElseThrow(() -> new RuntimeException("현재 로그인된 사용자 정보를 찾을 수 없습니다."));

        // 2. 처리할 연결 요청(Link) 정보 조회
        SeniorGuardianLink link = linkRepository.findById(respondDto.getLinkId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 요청입니다."));

        // 3. [보안] 이 요청이 나(시니어)에게 온 것이 맞는지 확인
        if (!link.getSenior().getId().equals(senior.getId())) {
            throw new RuntimeException("본인에게 온 요청만 처리할 수 있습니다.");
        }

        // 4. 이미 처리된 요청인지 확인 (PENDING 상태일 때만 처리 가능)
        if (link.getStatus() != LinkStatus.PENDING) {
            throw new RuntimeException("이미 처리되었거나 만료된 요청입니다.");
        }

        // 5. DTO의 action 값에 따라 분기 처리
        switch (respondDto.getActionType()) {
            case APPROVE:
                link.approveLink(); // 엔티티 내부 메서드 호출 (status -> APPROVED)
                break;
            case REJECT:
                link.rejectLink();  // 엔티티 내부 메서드 호출 (status -> REJECTED)
                break;
            default:
                throw new RuntimeException("알 수 없는 요청입니다.");
        }

        // @Transactional에 의해 link 객체의 변경 사항이 자동 커밋됩니다.
    }

    /**
     * (보호자) 연결이 승인된 특정 시니어의 상태 정보를 조회합니다.
     * [!] 이 메서드 호출 시, 시니어의 'guardianViewCount'가 1 증가합니다.
     *
     * @param guardianUserId (현재 로그인한 보호자의 userId)
     * @param seniorId (조회하려는 시니어의 DB ID - Long)
     * @return SeniorStatusDto
     */
    @Transactional // (조회수 증가(UPDATE)가 있으므로 readOnly=false)
    public SeniorStatusDto getSeniorStatus(String guardianUserId, Long seniorId) {

        // 1. 보호자(본인) 정보 조회
        User guardian = userRepository.findByUserId(guardianUserId)
                .orElseThrow(() -> new RuntimeException("현재 로그인된 사용자 정보를 찾을 수 없습니다."));

        // 2. 조회 대상 시니어 정보 조회
        User senior = userRepository.findById(seniorId)
                .orElseThrow(() -> new RuntimeException("조회하려는 시니어 정보를 찾을 수 없습니다."));

        // 3. [권한 확인] 보호자와 시니어가 'APPROVED' 상태로 연결되어 있는지 확인
        SeniorGuardianLink link = linkRepository.findByGuardianAndSenior(guardian, senior)
                .orElseThrow(() -> new RuntimeException("해당 시니어와 연결 관계가 없습니다."));

        if (link.getStatus() != LinkStatus.APPROVED) {
            throw new RuntimeException("아직 시니어의 승인을 받지 못했습니다.");
        }

        // 4. [핵심 로직] 시니어의 '보호자 열람 횟수' 1 증가
        senior.incrementGuardianViewCount();

        // 5. [!] 실제 시니어의 '오늘' 데이터 조회 (MyPageService 로직 활용)
        LocalDate today = LocalDate.now();
        String seniorUserIdString = senior.getUserId(); // Repository가 요구하는 String userId

        // 5a. 오늘 감정 기록 조회
        String todayMood = dailyMoodRepository.findByUser_UserIdAndMoodDate(seniorUserIdString, today)
                .map(DailyMood::getMoodIcon) // DailyMood 객체에서 아이콘 문자열만 추출
                .orElse(null); // 기록이 없으면 null

        // 5b. 오늘 복약 기록 조회 (MyPageService의 DTO 활용)
        List<String> todayMedications = alarmCheckLogRepository.findMedicationLogsByDateRange(seniorUserIdString, today, today)
                .stream()
                .map(MedicationCheckDto::getMedicationName) // 복용한 약 이름 리스트
                .collect(Collectors.toList());

        // 5c. 오늘 퀴즈 기록 조회 (QuizAttempt에 getTotalCount()가 있다는 가정)
        List<String> todayQuizzes = quizAttemptRepository.findAllByUser_UserIdAndAttemptDateBetween(seniorUserIdString, today, today)
                .stream()
                .map(quiz -> String.format("퀴즈: %d/%d",
                        quiz.getCorrectCount(),
                        quiz.getTotalCount())) // 예: "퀴즈: 8/10"
                .collect(Collectors.toList());

        // 6. 실제 데이터로 DTO를 빌드하여 반환
        return SeniorStatusDto.builder()
                .senior(senior) // (이름, ID, 뷰카운트 등을 DTO가 알아서 추출)
                .todayMoodIcon(todayMood)
                .todayMedicationLogs(todayMedications)
                .todayQuizLogs(todayQuizzes)
                .build();
    }

    /**
     * (보호자) 본인이 연결(승인)한 'APPROVED' 상태의 시니어 목록을 조회합니다.
     * [!] N+1 최적화 적용
     * @param guardianUserId (현재 로그인한 보호자의 userId)
     * @return DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<LinkedSeniorDto> getApprovedSeniors(String guardianUserId) {

        // 1. 보호자(본인) 정보 조회
        User guardian = userRepository.findByUserId(guardianUserId)
                .orElseThrow(() -> new RuntimeException("현재 로그인된 사용자 정보를 찾을 수 없습니다."));

        // 2. [!] N+1 최적화 쿼리를 사용하여 'APPROVED' 상태의 시니어 목록 조회
        // (Repository에 findByGuardianAndStatusWithSenior 필요)
        List<SeniorGuardianLink> approvedLinks = linkRepository.findByGuardianAndStatusWithSenior(guardian, LinkStatus.APPROVED);

        // 3. DTO 리스트로 변환하여 반환
        return approvedLinks.stream()
                .map(LinkedSeniorDto::new) // (link) -> new LinkedSeniorDto(link)
                .collect(Collectors.toList());
    }
}