package com.example.swnuclearfusionwas.domain.meds.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Schema(name = "PushSubscription", description = "웹 푸시 구독 정보 엔티티. 각 사용자의 브라우저 푸시 구독 정보를 저장합니다.")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "구독 고유 ID (자동 생성)", example = "1", requiredMode = Schema.RequiredMode.REQUIRED, accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "구독한 사용자 ID", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "Push 서비스 endpoint URL (브라우저에서 발급)", example = "https://fcm.googleapis.com/fcm/send/abc123...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endpoint;

    @Schema(description = "브라우저에서 발급된 p256dh 공개키 (base64 인코딩)", example = "BEx...==", requiredMode = Schema.RequiredMode.REQUIRED)
    private String p256dh;

    @Schema(description = "브라우저에서 발급된 auth 키 (base64 인코딩)", example = "abc123==", requiredMode = Schema.RequiredMode.REQUIRED)
    private String auth;
}
