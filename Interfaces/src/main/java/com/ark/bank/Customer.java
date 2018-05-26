package com.ark.bank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Rick van der Heijden
 */
@Entity
public class Customer implements Serializable {

    private String name;
    private String password;
    private String residence;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) long id;

    public Customer(String name, String residence, String password) {
        this.name = name;
        this.password = password;
        this.residence = residence;
    }

    public Customer(){

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
