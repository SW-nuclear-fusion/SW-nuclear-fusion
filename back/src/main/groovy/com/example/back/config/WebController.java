package com.example.back.config; // 본인 패키지 경로에 맞게 수정

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    /**
     * API(/api/**)나 정적 파일(.)이 아닌 모든 GET 요청을 index.html로 포워딩합니다.
     * React Router가 클라이언트 사이드 라우팅을 처리하도록 합니다.
     * 정규식: .(점)이 없는 모든 경로를 매칭 (예: /mypage, /main/alarm)
     * .(점)이 있는 경로는 제외 (예: /assets/index.js, /favicon.ico)
     */
    @GetMapping(value = {"/", "/{path:[^\\.]*}", "/**/{path:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}