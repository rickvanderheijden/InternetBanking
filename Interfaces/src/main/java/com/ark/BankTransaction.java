package com.ark;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Rick van der Heijden
 */
@Entity
public class BankTransaction implements Serializable {

    private long amount;
    private String description;
    private String accountFrom;
    private String accountTo;
    private Date transactionDate;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) long id;

    /**
     * Creates an instance of BankTransaction
     * @param amount The amount of the transaction. Can not be zero of negative.
     * @param description The description of the transaction. Can not null or empty.
     * @param accountFrom The account from which the amount is deducted. Can not null or empty.
     * @param accountTo The account to which the amount is transfered. Can not null or empty.
     */
    public BankTransaction(long amount, String description, String accountFrom, String accountTo) throws IllegalArgumentException {
        if ((amount <= 0)
            || (description == null) || description.isEmpty()
            || (accountFrom == null) || accountFrom.isEmpty()
            || (accountTo == null) || accountTo.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.amount = amount;
        this.description = description;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.transactionDate = new Date();
    }

    public BankTransaction() {
    }

    /**
     * Gets the amount of the transaction.
     * @return The amount of the transaction in cents.
     */
    public long getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the transaction.
     * @param amount The amount in cents. Can not be zero or negative.
     * @return True if the amount has been set succesfully, false otherwise.
     */
    public boolean setAmount(long amount) throws IllegalArgumentException{
        if (amount <= 0) {
            return false;
        }

        this.amount = amount;

        return true;
    }

    /**
     * Gets the description of the transaction.
     * @return The desciption of the transaction.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the transaction.
     * @param description The desciption of the transaction. Can not be null or empty.
     * @return True if set succesfully, false otherwise.
     */
    public boolean setDescription(String description) throws IllegalArgumentException {
        if ((description == null) || description.isEmpty()) {
            return false;
        }

        this.description = description;
        return true;
    }

    /**
     * Gets the bank account number from which the amount should be deducted.
     * @return The bank account number.
     */
    public String getAccountFrom() {
        return accountFrom;
    }

    /**
     * Sets the bank account number from which the amount should be deducted.
     * @param accountFrom The bank account number. Can not be null or empty.
     * @return True if set succesfully, false otherwise.
     */
    public boolean setAccountFrom(String accountFrom) throws IllegalArgumentException {
        if ((accountFrom == null) || accountFrom.isEmpty()) {
            return false;
        }

        this.accountFrom = accountFrom;
        return true;
    }

    /**
     * gets the bank account number to which the amount should be tranfererd.
     * @return The bank account number.
     */
    public String getAccountTo() {
        return accountTo;
    }

    /**
     * Sets the bank account number to which the amount should be tranfererd.
     * @param accountTo The bank account number. Can not be null or empty.
     * @return True if set succesfully, false otherwise.
     */
    public boolean setAccountTo(String accountTo) {
        if ((accountTo == null) || accountTo.isEmpty()) {
            return false;
        }
        this.accountTo = accountTo;
        return true;
    }

    /**
     * Gets the date of the transaction.
     * @return Date of the transaction.
     */
    public Date getDate() {
        return transactionDate;
    }

    /**
     * Gets the date of the transaction.
     * @param date The date of the transaction.
     */
    public void setDate(Date date) {
        this.transactionDate = date;
    }
}
