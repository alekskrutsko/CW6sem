package com.cw6sem.domain;

public enum Role{
    CUSTOMER(0),
    APPRAISER(1),
    ADMINISTRATOR(2);

    private int value;

    Role(int value) {
        this.value = value;
    }

}
