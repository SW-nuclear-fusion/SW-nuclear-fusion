package com.example.back.mypage.view;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/views")
public class SeniorViewLogController {

    private final SeniorViewLogService service;

    public SeniorViewLogController(SeniorViewLogService service) {
        this.service = service;
    }

    @GetMapping("/count")
    public ResponseEntity<ViewCountResponse> getViewCount(
            @RequestParam("caregiverId") Long caregiverId,
            @RequestParam("seniorId") Long seniorId
    ) {
        long count = service.getViewCount(caregiverId, seniorId);
        ViewCountResponse resp = new ViewCountResponse(caregiverId, seniorId, count);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/record")
    public ResponseEntity<Void> recordView(
            @RequestParam("caregiverId") Long caregiverId,
            @RequestParam("seniorId") Long seniorId
    ) {
        service.recordView(caregiverId, seniorId);
        return ResponseEntity.ok().build();
    }
}
