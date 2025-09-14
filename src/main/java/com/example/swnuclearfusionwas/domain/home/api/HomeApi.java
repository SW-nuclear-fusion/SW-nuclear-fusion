package com.example.swnuclearfusionwas.domain.home.api;

import com.example.swnuclearfusionwas.domain.home.dto.HomeView;
import com.example.swnuclearfusionwas.domain.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeApi {
    private final HomeService homeService;

    // JWT/Security 사용 시
    @GetMapping
    public HomeView me(Authentication auth){
        Long userId = (Long) auth.getPrincipal();
        if (userId == null) throw new IllegalStateException("Unauthenticated");
        return homeService.viewByUserId(userId);
    }

    // 파라미터로 조회
    @GetMapping(params = "userId")
    public HomeView byId(@RequestParam Long userId){
        return homeService.viewByUserId(userId);
    }
}
