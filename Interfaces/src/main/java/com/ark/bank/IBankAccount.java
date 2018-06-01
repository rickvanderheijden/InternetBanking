package com.ark.bank;

import com.ark.Customer;

import java.io.Serializable;

public interface IBankAccount extends Serializable {
    /**
     * Gets the owner (Customer) of the bank account.
     * @return The owner of the bank account. Can be null.
     */
    Customer getOwner();

    /**
     * Gets the number of the bank account.
     * @return The number of the bank account.
     */
    String getNumber();

    /**
     * Gets the balance of the bank account.
     * @return The balance of the bank account in cents.
     */
    long getBalance();

    /**
     * Gets the credit limit of the bank account.
     * @return Credit limit in cents.
     */
    long getCreditLimit();

    /**
     * Sets the credit limit of the bank account.
     * @param creditLimit The credit limit in cents. Can not be negative.
     * @return True if credit limit has been set succesfully, false otherwise.
     */
    boolean setCreditLimit(long creditLimit);

    /**
     * Increases the balance of the bank account.
     * @param amount The amount in cents to increase the balance with. Can not be negative or zero.
     * @return True is balance has been increased succesfully, false otherwise.
     */
    boolean increaseBalance(long amount);

    /**
     * Decreases the balance of the bank account.
     * @param amount The amount in cents to decrease the balance with. Can not be negative or zero.
     * @return True is balance has been increased succesfully, false otherwise.
     */
    boolean decreaseBalance(long amount);
}
