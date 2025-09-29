package com.example.swnuclearfusionwas.domain.quiz.service;

import com.example.swnuclearfusionwas.domain.quiz.dto.memory.MemoryDtos.*;

public interface MemoryService {
    StartResponse start(String difficulty, int count);
    SubmitResponse submit(SubmitRequest req);
}
