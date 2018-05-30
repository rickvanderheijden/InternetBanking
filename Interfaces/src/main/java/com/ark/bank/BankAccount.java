package com.ark.bank;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Rick van der Heijden
 */
@Entity
public class BankAccount implements Serializable {

    private String number;
    private long balance = 0;
    private long creditLimit = 10000;
    @ManyToOne

    private Customer owner;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) long id;

    public BankAccount(Customer owner, String number) {
        this.owner = owner;
        this.number = number;
    }

    public BankAccount(){
    }

    public Customer getOwner() {
        return owner;
    }

    public String getNumber() {
        return number;
    }

    public synchronized long getBalance() {
        return balance;
    }

    public synchronized long getCreditLimit() {
        return creditLimit;
    }

    public synchronized void setCreditLimit(long creditLimit) {
        this.creditLimit = creditLimit;
    }

    public synchronized void increaseBalance(long amount) {
        balance += amount;
    }

    public synchronized boolean decreaseBalance(long amount) {
        long amountAvailable = balance + creditLimit;
        if (amount > amountAvailable) {
            return false;
        }

        balance -= amount;

        return true;
    }
}
