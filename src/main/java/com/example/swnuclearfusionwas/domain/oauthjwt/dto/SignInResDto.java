package com.example.swnuclearfusionwas.domain.oauthjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignInResDto {

    private String accessToken;
    private String name;
    private String role;

}
