package com.ark.bank;

import java.io.Serializable;

public class BankAccount implements Serializable {

    private static long nextNumber = 1000000000;
    private String number;
    private double balance = 0.00;
    private double creditLimit = 100.00;
    private Customer owner;

    public BankAccount(Customer owner, String bankId) {
        this.owner = owner;
        number = bankId + Long.toString(nextNumber);
        nextNumber++;
    }

    public Customer getOwner() {
        return owner;
    }

    public String getNumber() {
        return number;
    }

    public double getBalance() {
        return balance;
    }

    public double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }
}
