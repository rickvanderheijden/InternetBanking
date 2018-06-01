package com.ark;

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

    /**
     * Gets the name of the customer
     * @return The name of the customer. Can be null or empty.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the residence of the customer.
     * @return The name of the customer. Can be null or empty.
     */
    public String getResidence() {
        return residence;
    }

    /**
     * Checks if the password is the same as the provided password.
     * @param password The password to check.
     * @return True if password is correct, false otherwise.
     */
    public boolean isPasswordValid(String password) {
        return this.password.equals(password);
    }
}
