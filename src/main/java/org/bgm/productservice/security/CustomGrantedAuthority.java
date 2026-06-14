package org.bgm.productservice.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

import java.beans.ConstructorProperties;

@Getter
public class CustomGrantedAuthority implements GrantedAuthority {
    private final String authority;
    private CustomGrantedAuthority(String authority){
        this.authority = authority;
    }
    public static CustomGrantedAuthority fromRole(String role) {
        return new CustomGrantedAuthority(role);
    }
}
