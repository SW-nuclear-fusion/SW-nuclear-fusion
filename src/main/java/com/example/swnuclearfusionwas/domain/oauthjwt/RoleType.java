package com.example.swnuclearfusionwas.domain.oauthjwt;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType implements GrantedAuthority {
    SENIOR, GUARDIAN;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
