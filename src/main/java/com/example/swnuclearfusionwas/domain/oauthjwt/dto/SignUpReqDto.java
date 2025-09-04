package com.example.swnuclearfusionwas.domain.oauthjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpReqDto {

    private String userId;
    private String userPW;
    private String name;

}
