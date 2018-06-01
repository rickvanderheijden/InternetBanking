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

    /**
     * Gets the owner (Customer) of the bank account.
     * @return The owner of the bank account. Can be null.
     */
    public Customer getOwner() {
        return owner;
    }

    /**
     * Gets the number of the bank account.
     * @return The number of the bank account.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Gets the balance of the bank account.
     * @return The balance of the bank account in cents.
     */
    public synchronized long getBalance() {
        return balance;
    }

    /**
     * Gets the credit limit of the bank account.
     * @return Credit limit in cents.
     */
    public synchronized long getCreditLimit() {
        return creditLimit;
    }

    /**
     * Gets the credit limit of the bank account.
     * @param creditLimit The credit limit in cents. Can not be negative.
     * @return True if credit limit has been set succesfully, false otherwise.
     */
    public synchronized boolean setCreditLimit(long creditLimit) {
        if (creditLimit < 0) {
            return false;
        }

        this.creditLimit = creditLimit;

        return true;
    }

    /**
     * Increases the balance of the bank account.
     * @param amount The amount in cents to increase the balance with. Can not be negative.
     * @return True is balance has been increased succesfully, false otherwise.
     */
    public synchronized boolean increaseBalance(long amount) {
        if (amount < 0) {
            return false;
        }

        balance += amount;

        return true;
    }

    /**
     * Decreases the balance of the bank account.
     * @param amount The amount in cents to decrease the balance with. Can not be negative.
     * @return True is balance has been increased succesfully, false otherwise.
     */
    public synchronized boolean decreaseBalance(long amount) {
        if (amount < 0) {
            return false;
        }

        long amountAvailable = balance + creditLimit;
        if (amount > amountAvailable) {
            return false;
        }

        balance -= amount;

        return true;
    }
}
