package com.example.myroom.global.jwt;

import java.beans.ConstructorProperties;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {
    private final Long id;

    @ConstructorProperties({"id", "email", "password", "authorities"})
    public CustomUserDetails(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
