package com.example.swnuclearfusionwas.domain.meds.controller;

import com.example.swnuclearfusionwas.domain.meds.dto.PushSubscriptionReq;
import com.example.swnuclearfusionwas.domain.meds.entity.PushSubscription;
import com.example.swnuclearfusionwas.domain.meds.repository.PushSubscriptionRepository;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.oauthjwt.jwt.JWTUtil;
import com.example.swnuclearfusionwas.domain.oauthjwt.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.swnuclearfusionwas.domain.meds.service.PushService;

import java.util.Map;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushController {
    private final PushSubscriptionRepository pushRepo;
    private final PushService pushService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepo;

    private Long currentUserId(HttpServletRequest req) {
        Cookie[] cs = req.getCookies(); if (cs==null) return null;
        String token=null; for (Cookie c: cs) if ("Authorization".equals(c.getName())) token=c.getValue();
        if (token==null || Boolean.TRUE.equals(jwtUtil.isExpired(token))) return null;
        UserEntity u = userRepo.findByUsername(jwtUtil.getUsername(token));
        return (u==null? null : u.getId());
    }

    @GetMapping("/public-key")
    public ResponseEntity<?> publicKey() {
        return ResponseEntity.ok(Map.of("publicKey", pushService.getPublicKey()));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody PushSubscriptionReq req, HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");

        PushSubscription sub = PushSubscription.builder()
                .userId(userId)
                .endpoint(req.getEndpoint())
                .p256dh(req.getP256dh())
                .auth(req.getAuth())
                .build();
        pushRepo.save(sub);
        return ResponseEntity.ok().body("subscribed");
    }
}