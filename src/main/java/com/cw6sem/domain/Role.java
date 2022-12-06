package com.cw6sem.domain;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public enum Role{
    CUSTOMER(0),
    APPRAISER(1),
    ADMINISTRATOR(2);

    private int value;

    Role(int value) {
        this.value = value;
    }
    public Set<SimpleGrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority(name()));
    }
}
