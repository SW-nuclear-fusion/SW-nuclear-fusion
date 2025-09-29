package com.example.swnuclearfusionwas.domain.quiz.service.impl;

import com.example.swnuclearfusionwas.domain.quiz.dto.memory.MemoryDtos.*;
import com.example.swnuclearfusionwas.domain.quiz.service.MemoryService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemoryServiceImpl implements MemoryService {
    private final SecureRandom rnd = new SecureRandom();
    private final Map<UUID, List<String>> sessions = new ConcurrentHashMap<>();

    @Override
    public StartResponse start(String difficulty, int count) {
        int len; int showMs; int answerMs;
        switch ((difficulty==null?"MEDIUM":difficulty).toUpperCase()) {
            case "EASY" -> { len=3; showMs=2000; answerMs=8000; }
            case "HARD" -> { len=7; showMs=3000; answerMs=12000; }
            default     -> { len=5; showMs=2500; answerMs=10000; }
        }
        count = Math.max(3, Math.min(10, count));

        List<String> sols = new ArrayList<>();
        List<Trial> trials = new ArrayList<>();
        for (int i=0;i<count;i++) {
            String s = genDigits(len);
            sols.add(s);
            trials.add(new Trial(s));
        }
        UUID sid = UUID.randomUUID();
        sessions.put(sid, sols);
        return new StartResponse(sid, trials, showMs, answerMs);
    }

    @Override
    public SubmitResponse submit(SubmitRequest req) {
        List<String> sol = sessions.remove(req.sessionId());
        if (sol == null) return new SubmitResponse(0,0,0);
        int total = Math.min(sol.size(), req.answers()==null?0:req.answers().size());
        int correct = 0;
        for (int i=0;i<total;i++) {
            if (Objects.equals(norm(sol.get(i)), norm(req.answers().get(i)))) correct++;
        }
        return new SubmitResponse(total, correct, correct*12);
    }

    private String genDigits(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<len;i++) {
            if (i>0) sb.append("-");
            sb.append(rnd.nextInt(10));
        }
        return sb.toString();
    }
    private String norm(String s){ return s==null?null:s.replaceAll("\\s",""); }
}
