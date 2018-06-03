package com.ark.bank;

import com.ark.Customer;

import javax.persistence.*;

/**
 * @author Rick van der Heijden
 */
@Entity
public class BankAccount implements IBankAccount {

    private String number;
    private long balance = 0;
    private long creditLimit = 10000;
    @ManyToOne

    private Customer owner;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) long id;

    /**
     * Creates an instance of BankAccount.
     * @param owner The owner of the bank account. Can not be null.
     * @param number The bank account number. Can not be null or empty.
     * @throws IllegalArgumentException if owner is null or number is null or empty.
     */
    public BankAccount(Customer owner, String number) throws IllegalArgumentException {
        if ((owner == null) || (number == null) || number.isEmpty()) {
            throw new IllegalArgumentException("Argument can not be null or empty");
        }

        this.owner = owner;
        this.number = number;
    }

    @Override
    public Customer getOwner() {
        return owner;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public synchronized long getBalance() {
        return balance;
    }

    @Override
    public synchronized long getCreditLimit() {
        return creditLimit;
    }

    @Override
    public synchronized boolean setCreditLimit(long creditLimit) {
        if (creditLimit < 0) {
            return false;
        }

        this.creditLimit = creditLimit; //.set(creditLimit);

        return true;
    }

    @Override
    public synchronized boolean increaseBalance(long amount) {
        if (amount <= 0) {
            return false;
        }

        balance += amount;

        return true;
    }

    @Override
    public synchronized boolean decreaseBalance(long amount) {
        if (amount <= 0) {
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
