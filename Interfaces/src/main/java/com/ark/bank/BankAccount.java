package com.ark.bank;

import java.io.Serializable;

/**
 * @author Rick van der Heijden
 */
public class BankAccount implements Serializable {

    private final String number;
    private double creditLimit = 100.00;
    private final Customer owner;

    public BankAccount(Customer owner, String number) {
        this.owner = owner;
        this.number = number;
    }

    public Customer getOwner() {
        return owner;
    }

    public String getNumber() {
        return number;
    }

    public double getBalance() {
        double balance = 0.00;
        return balance;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }
}
