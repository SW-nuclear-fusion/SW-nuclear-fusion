package com.example.swnuclearfusionwas.domain.quiz.controller;

import com.example.swnuclearfusionwas.domain.quiz.dto.memory.MemoryDtos.*;
import com.example.swnuclearfusionwas.domain.quiz.service.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz/memory")
@RequiredArgsConstructor
public class MemoryController {
    private final MemoryService memoryService;

    @PostMapping("/start")
    public StartResponse start(@RequestBody(required=false) StartRequest req) {
        String diff = req==null? "MEDIUM" : req.difficulty();
        int count = req==null? 5 : req.count();
        return memoryService.start(diff, count);
    }

    @PostMapping("/submit")
    public SubmitResponse submit(@RequestBody SubmitRequest req) {
        return memoryService.submit(req);
    }
}
