package com.ark.bank;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Rick van der Heijden
 */
@Entity
public class BankAccount implements Serializable {

    private String number;
    private double balance = 0.00;
    private double creditLimit = 100.00;
    @ManyToOne
    private Customer owner;
    @Id @GeneratedValue(strategy = GenerationType.AUTO) long id;

    public BankAccount(Customer owner, String number) {
        this.owner = owner;
        this.number = number;
    }

    public  BankAccount(){

    };

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

    public void increaseBalance(double amount) {
        balance += amount;
    }

    public boolean decreaseBalance(double amount) {
        double amountAvailable = balance + creditLimit;
        if (amount > amountAvailable) {
            return false;
        }

        balance -= amount;

        return true;
    }
}
