package com.ark;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Rick van der Heijden
 */
@Entity
public class Customer implements Serializable {

    @NotNull
    private String name;
    @NotNull
    private String password;
    @NotNull
    private String residence;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) long id;

    public Customer(String name, String residence, String password) throws IllegalArgumentException {
        if ((name == null) || name.isEmpty()
            || (residence == null) || residence.isEmpty()
            || (password == null) || password.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.password = password;
        this.residence = residence;
    }

    public Customer() {
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
     * Gets the id of the customer
     * @return The id of the customer. Can be null or empty
     */
    public long getId() { return id; }

    /**
     * Checks if the password is the same as the provided password.
     * @param password The password to check.
     * @return True if password is correct, false otherwise.
     */
    public boolean isPasswordValid(String password) {
        return this.password.equals(password);
    }
}
