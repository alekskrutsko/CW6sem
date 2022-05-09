package com.cw6sem.domain;

public enum Status {
    NEW(0),
    WAITFORCUSTOMER(1),
    WAITFORAPPRAISER(2),
    TERMINATED(3),
    SIGNED(4);

    private int value;

    Status(int value) {
        this.value = value;
    }

}