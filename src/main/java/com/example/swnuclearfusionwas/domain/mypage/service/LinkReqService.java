package com.example.swnuclearfusionwas.domain.mypage.service;

import com.example.swnuclearfusionwas.domain.mypage.dto.CreateLinkReq;
import com.example.swnuclearfusionwas.domain.mypage.entity.LinkReq;
import com.example.swnuclearfusionwas.domain.mypage.model.LinkStatus;
import com.example.swnuclearfusionwas.domain.mypage.repository.LinkReqRepository;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.oauthjwt.jwt.JWTService;
import com.example.swnuclearfusionwas.domain.oauthjwt.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LinkReqService {

    private final UserRepository userRepo;
    private final LinkReqRepository linkRepo;
    private final PhoneValidator phoneValidator;
    private final JWTService jwtService;

    public Map<String, Object> create(CreateLinkReq in, HttpServletRequest req) {
        String me = getUsernameFromCookie(req);
        UserEntity meUser = userRepo.findByUsername(me);
        if (meUser == null) throw new IllegalArgumentException("요청자 계정을 찾을 수 없습니다.");

        boolean isGuardian = "GUARDIAN".equals(String.valueOf(meUser.getRole()));
        if (!isGuardian) throw new IllegalArgumentException("보호자만 연결 요청을 생성할 수 있습니다.");

        String normalized;
        try {
            normalized = phoneValidator.validatePhone(in.getPhone());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("전화번호 형식이 올바르지 않습니다. " + e.getMessage());
        }

        UserEntity target = userRepo.findByPhone(normalized)
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 가입된 사용자가 없습니다."));

        if (!"SENIOR".equals(String.valueOf(target.getRole()))) {
            throw new IllegalArgumentException("연결 요청의 수신자는 시니어여야 합니다.");
        }

        if (meUser.getId().equals(target.getId())) {
            throw new IllegalArgumentException("본인에게는 요청할 수 없습니다.");
        }

        boolean hasPending = linkRepo.existsByInitiatorUserIdAndRecipientUserIdAndStatus(
                meUser.getId(), target.getId(), LinkStatus.PENDING
        );
        if (hasPending) {
            throw new IllegalStateException("이미 대기 중인 요청이 있습니다.");
        }

        LinkReq lr = new LinkReq();
        lr.setInitiatorUserId(meUser.getId());
        lr.setRecipientUserId(target.getId());
        lr.setStatus(LinkStatus.PENDING);
        linkRepo.save(lr);

        return Map.of(
                "id", lr.getId(),
                "status", lr.getStatus().name(),
                "recipientMaskedPhone", phoneValidator.mask(normalized)
        );
    }

    public void accept(Long id, HttpServletRequest req) {
        String me = getUsernameFromCookie(req);
        UserEntity meUser = userRepo.findByUsername(me);
        if (meUser == null) throw new IllegalArgumentException("수신자 계정을 찾을 수 없습니다.");

        String myRole = String.valueOf(meUser.getRole());
        if (!"SENIOR".equals(myRole)) {
            throw new IllegalArgumentException("시니어만 연결 요청을 수락할 수 있습니다.");
        }

        LinkReq lr = linkRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (!lr.getRecipientUserId().equals(meUser.getId())) {
            throw new IllegalArgumentException("승인 권한이 없습니다.");
        }
        if (lr.getStatus() != LinkStatus.PENDING) {
            throw new IllegalStateException("요청이 유효하지 않습니다.");
        }

        lr.setStatus(LinkStatus.ACCEPTED);
        linkRepo.save(lr);
        // TODO: 실제 연결 테이블 생성
    }

    public void reject(Long id, HttpServletRequest req) {
        String me = getUsernameFromCookie(req);
        UserEntity meUser = Optional.ofNullable(userRepo.findByUsername(me))
                .orElseThrow(() -> new IllegalArgumentException("수신자 계정을 찾을 수 없습니다."));

        String myRole = String.valueOf(meUser.getRole());
        if (!"SENIOR".equals(myRole)) {
            throw new IllegalArgumentException("시니어만 연결 요청을 거절할 수 있습니다.");
        }

        LinkReq lr = linkRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("요청을 찾을 수 없습니다."));

        if (!lr.getRecipientUserId().equals(meUser.getId()))
            throw new IllegalArgumentException("권한 없음");
        if (lr.getStatus() != LinkStatus.PENDING)
            throw new IllegalStateException("이미 처리된 요청입니다.");

        lr.setStatus(LinkStatus.REJECTED);
        linkRepo.save(lr);
    }

    private String getUsernameFromCookie(HttpServletRequest req) {
        String token = getCookie(req, "Authorization");
        if (token == null || token.isBlank()) throw new IllegalArgumentException("인증 토큰이 없습니다.");
        if (Boolean.TRUE.equals(jwtService.isExpired(token))) throw new IllegalArgumentException("인증 토큰이 만료되었습니다.");
        return jwtService.parseUsername(token);
    }
    private static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) if (name.equals(c.getName())) return c.getValue();
        return null;
    }
}