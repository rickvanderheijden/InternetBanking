package com.ark.bank;

import java.io.Serializable;

public class Customer implements Serializable {

    private final String name;
    private final String password;
    private final String residence;

    public Customer(String name, String password, String residence) {
        this.name = name;
        this.password = password;
        this.residence = residence;
    }

    public String getName() {
        return name;
    }

    public String getResidence() {
        return residence;
    }

    public boolean isPasswordValid(String password) {
        return this.password.equals(password);
    }
}
