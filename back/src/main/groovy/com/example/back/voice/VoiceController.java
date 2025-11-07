// 설명: 클라이언트로부터 텍스트를 받아 `VoiceService`로 변환 요청을 보내고 오디오(MP3)를 응답으로 반환하는 컨트롤러입니다.
package com.example.back.voice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    private final VoiceService voiceService;

    @Autowired
    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    @PostMapping(consumes = "application/json", produces = "audio/mpeg")
    public ResponseEntity<byte[]> synthesize(@RequestBody VoiceRequest request) {
        if (request == null || request.getText() == null || request.getText().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] audio = voiceService.synthesize(request.getText(), request.getVoice());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("audio/mpeg"));
            headers.setContentLength(audio.length);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"speech.mp3\"");
            return new ResponseEntity<>(audio, headers, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 간단한 요청 DTO
    public static class VoiceRequest {
        private String text;
        private String voice;

        public VoiceRequest() {}

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getVoice() {
            return voice;
        }

        public void setVoice(String voice) {
            this.voice = voice;
        }
    }
}

