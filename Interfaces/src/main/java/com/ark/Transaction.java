package com.ark;

import javax.jws.WebService;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Rick van der Heijden
 */
@WebService
@Entity
public class Transaction implements Serializable {
    private long amount;
    private String description;
    private String accountFrom;
    private String accountTo;
    private Date date;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) long id;

    public Transaction() {
    }

    /**
     * Creates an instance of Transaction
     * @param amount The amount of the transaction. Can not be zero of negative.
     * @param description The description of the transaction. Can not null or empty.
     * @param accountFrom The account from which the amount is deducted. Can not null or empty.
     * @param accountTo The account to which the amount is transfered. Can not null or empty.
     */
    public Transaction(long amount, String description, String accountFrom, String accountTo) throws IllegalArgumentException {
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
        this.date = new Date();
    }


    /**
     * Returns a string representation of the object.
     * @return A string representation of the object
     */
    @Override
    public String toString() {
        return "Van: " + this.accountFrom + " Naar: " + this.accountTo + " €" + String.valueOf(this.amount / 100.0);
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
     * @param amount The amount in cents. Can not be negative.
     * @return True if the amount has been set succesfully, false otherwise.
     */
    public boolean setAmount(long amount) {
        if (amount < 0) {
            return false;
        }

        this.amount = amount;

        return true;
    }

    /**
     * Gets the description of the transaction.
     * @return The desciption of the transaction. This can be null or empty.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the transaction.
     * @param description The desciption of the transaction. This can be null or empty.
     */
    public void setDescription(String description) {
        this.description = description;
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
     * @param accountFrom The bank account number.
     */
    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
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
     * @param accountTo The bank account number.
     */
    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    /**
     * Gets the date of the transaction.
     * @return Date of the transaction.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the date of the transaction.
     * @param date The date of the transaction.
     */
    public void setDate(Date date) {
        this.date = date;
    }
}
