package com.example.swnuclearfusionwas.domain.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLinkReq {

    @NotBlank
    @Schema(
            description = "상대방 전화번호 (하이픈 없이 11자리, 예: 01012345678)",
            example = "01012345678"
    )
    private String phone;

}