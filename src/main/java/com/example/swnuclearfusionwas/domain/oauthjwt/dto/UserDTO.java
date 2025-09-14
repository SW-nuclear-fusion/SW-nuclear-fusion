package com.example.swnuclearfusionwas.domain.oauthjwt.dto;

import com.example.swnuclearfusionwas.domain.oauthjwt.RoleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    @Getter
    private Long id;
    private RoleType role;
    private String name;
    private String socialname;

}
