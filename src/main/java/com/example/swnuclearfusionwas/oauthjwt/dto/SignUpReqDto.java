package com.example.swnuclearfusionwas.oauthjwt.dto;

import com.example.swnuclearfusionwas.oauthjwt.entity.UserEntity;
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
