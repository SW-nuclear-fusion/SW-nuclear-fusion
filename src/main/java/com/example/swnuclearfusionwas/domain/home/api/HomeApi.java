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

    private final HomeService home;

    /** 인증 사용자 본인의 홈 요약 */
    @GetMapping
    public HomeView me(Authentication auth){
        if (auth == null || auth.getName() == null) throw new IllegalStateException("Unauthenticated");
        return home.summaryByUsername(auth.getName());
    }

    /** 디버그/관리용: username으로 조회 */
    @GetMapping(params = "username")
    public HomeView byUsername(@RequestParam String username){
        return home.summaryByUsername(username);
    }
}