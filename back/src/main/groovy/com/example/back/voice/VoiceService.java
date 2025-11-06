// 설명: OpenAI TTS 엔드포인트로 텍스트를 전송하고 오디오 바이트를 받아 반환하는 서비스입니다.
package com.example.back.voice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

@Service
public class VoiceService {

    // application.properties 또는 환경변수에서 읽도록 변경
    @Value("${openai.api.key:}")
    private String openAiApiKey;

    @Value("${openai.tts.endpoint:https://api.openai.com/v1/audio/speech}")
    private String openAiEndpoint;

    @Value("${openai.tts.model:gpt-4o-mini-tts}")
    private String openAiModel;

    @Value("${openai.tts.response-format:audio/mpeg}")
    private String responseFormat;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * text를 OpenAI TTS로 보내서 오디오 바이트를 반환합니다.
     * MP3 바이트 또는 OpenAI가 JSON으로 base64 오디오를 반환하는 경우 모두 처리합니다.
     * @param text 변환할 텍스트
     * @param voice 선택적 음성 이름 (null이면 OpenAI 기본값 사용)
     * @return 오디오 바이트
     * @throws IOException
     * @throws InterruptedException
     */
    public byte[] synthesize(String text, String voice) throws IOException, InterruptedException {
        if (openAiApiKey == null || openAiApiKey.isEmpty()) {
            throw new IllegalStateException("OpenAI API key is not configured. Set openai.api.key in application.properties or OPENAI_API_KEY env variable.");
        }

        String url = openAiEndpoint;

        // 요청 본문: model, voice, input (OpenAI의 실제 파라미터 이름은 변경될 수 있으니 필요 시 수정)
        String model = openAiModel;
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"model\":\"").append(model).append("\"");
        if (voice != null && !voice.isEmpty()) {
            jsonBuilder.append(",\"voice\":\"").append(escapeJson(voice)).append("\"");
        }
        jsonBuilder.append(",\"input\":\"").append(escapeJson(text)).append("\"");
        jsonBuilder.append("}");
        String json = jsonBuilder.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .header("Accept", responseFormat + ", application/json") // 설정된 포맷 또는 JSON
                .POST(BodyPublishers.ofString(json))
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            Optional<String> contentTypeOpt = response.headers().firstValue("Content-Type");
            String contentType = contentTypeOpt.orElse("");

            if (contentType.contains("application/json")) {
                // JSON 응답일 경우 body를 문자열로 파싱해서 base64 오디오 필드를 찾음
                String bodyText = new String(response.body());
                JsonNode root = objectMapper.readTree(bodyText);

                // 다양한 필드명 시도
                String base64 = null;
                if (root.has("audio")) {
                    base64 = root.get("audio").asText();
                } else if (root.has("audio_base64")) {
                    base64 = root.get("audio_base64").asText();
                } else if (root.has("data")) {
                    JsonNode dataNode = root.get("data");
                    if (dataNode.isTextual()) {
                        base64 = dataNode.asText();
                    } else if (dataNode.isArray() && !dataNode.isEmpty() && dataNode.get(0).has("b64")) {
                        base64 = dataNode.get(0).get("b64").asText();
                    }
                }

                if (base64 != null && !base64.isEmpty()) {
                    return Base64.getDecoder().decode(base64);
                } else {
                    throw new IOException("OpenAI TTS returned JSON but no audio field found: " + bodyText);
                }
            } else {
                // 바이너리(MP3 등) 응답
                return response.body();
            }
        } else {
            String bodyText = new String(response.body());
            throw new IOException("OpenAI TTS request failed: status=" + status + " body=" + bodyText);
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
