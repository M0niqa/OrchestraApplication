package com.monika.worek.orchestra.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    public CustomUserDetails(String mail, String password, Collection<? extends GrantedAuthority> authorities) {
        super(mail, password, authorities);
    }

}
