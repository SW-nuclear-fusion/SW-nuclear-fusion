package com.example.swnuclearfusionwas.domain.meds.controller;

import com.example.swnuclearfusionwas.domain.meds.dto.*;
import com.example.swnuclearfusionwas.domain.meds.service.AlertService;
import com.example.swnuclearfusionwas.domain.meds.service.MedService;
import com.example.swnuclearfusionwas.domain.oauthjwt.entity.UserEntity;
import com.example.swnuclearfusionwas.domain.oauthjwt.jwt.JWTUtil;
import com.example.swnuclearfusionwas.domain.oauthjwt.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.ZoneId;
import java.util.Map;

@RestController
@RequestMapping("/api/meds")
@RequiredArgsConstructor
public class MedController {

    private final MedService medService;
    private final AlertService alertService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepo;

    private Long currentUserId(HttpServletRequest req) {
        Cookie[] cs = req.getCookies(); if (cs==null) return null;
        String token=null; for (Cookie c: cs) if ("Authorization".equals(c.getName())) token=c.getValue();
        if (token==null || Boolean.TRUE.equals(jwtUtil.isExpired(token))) return null;
        Long userId = jwtUtil.parseUserId(token);

        return userRepo.findById(userId)
                .map(UserEntity::getId)
                .orElse(null);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MedCreateReq req, HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        Long id = medService.create(uid, req);
        return ResponseEntity.created(URI.create("/api/meds/medId="+id)).body(Map.of("medId", id));
    }

    @GetMapping
    public ResponseEntity<?> list(HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        return ResponseEntity.ok(medService.list(uid));
    }

    @GetMapping("/medId={medId}")
    public ResponseEntity<?> detail(@PathVariable Long medId, HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        return ResponseEntity.ok(medService.detail(uid, medId));
    }

    @PatchMapping("/medId={medId}")
    public ResponseEntity<?> update(@PathVariable Long medId, @RequestBody MedUpdateReq req, HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        medService.update(uid, medId, req);
        return ResponseEntity.ok(Map.of("medId", medId, "updated", true));
    }

    @DeleteMapping("/medId={medId}")
    public ResponseEntity<?> delete(@PathVariable Long medId, HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        medService.delete(uid, medId);
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    @PatchMapping("/alert/toggle")
    public ResponseEntity<?> toggle(@RequestBody AlertToggleReq req, HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        medService.toggleAlert(uid, req.getMedId(), Boolean.TRUE.equals(req.getEnabled()));
        return ResponseEntity.ok(Map.of("medId", req.getMedId(), "alertEnabled", req.getEnabled()));
    }

    @PostMapping("/alert/complete")
    public ResponseEntity<?> complete(@RequestBody AlertCompleteReq req, HttpServletRequest request) {
        Long uid = currentUserId(request);
        if (uid==null) return ResponseEntity.status(401).body("{\"error\":\"unauthorized\"}");
        alertService.complete(uid, req.getEventId());
        return ResponseEntity.ok(Map.of("eventId", req.getEventId(), "taken", true));
    }


}