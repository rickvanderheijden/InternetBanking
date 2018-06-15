package com.ark;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Base64;

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
        this.password = passwordEncode(password);
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
     * Checks if the password is the same as the provided password.
     * @param password The password to check.
     * @return True if password is correct, false otherwise.
     */
    public boolean isPasswordValid(String password) {
        return passwordDecode(this.password).equals(password);
    }

    private String passwordEncode(String password){
        String b64encoded = Base64.getEncoder().encodeToString(password.getBytes());

        String reverse = new StringBuffer(b64encoded).reverse().toString();

        StringBuilder tmp = new StringBuilder();
        final int offset = 4;
        for (int i = 0; i < reverse.length(); i++) {
            tmp.append((char)(reverse.charAt(i) + offset));
        }
        return tmp.toString();
    }

    private String passwordDecode(String secret){
        StringBuilder tmp = new StringBuilder();
        final int offset = 4;
        for (int i = 0; i < secret.length(); i++){
            tmp.append((char)(secret.charAt(i) - offset));
        }

        String reversed = new StringBuffer(tmp.toString()).reverse().toString();
        return new String(Base64.getDecoder().decode(reversed));
    }
}
