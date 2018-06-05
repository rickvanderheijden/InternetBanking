package com.ark;

import javax.jws.WebService;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Rick van der Heijden
 */
@WebService
public class Transaction implements Serializable {
    Transaction_ent trans;

    public Transaction() {
        this.trans = new Transaction_ent();
    }

    /**
     * Creates an instance of Transaction
     * @param amount The amount of the transaction. Can not be zero of negative.
     * @param description The description of the transaction. Can not null or empty.
     * @param accountFrom The account from which the amount is deducted. Can not null or empty.
     * @param accountTo The account to which the amount is transfered. Can not null or empty.
     */
    public Transaction(long amount, String description, String accountFrom, String accountTo) throws IllegalArgumentException {
        this.trans = new Transaction_ent(amount, description, accountFrom, accountTo);
    }


    /**
     * Returns a string representation of the object.
     * @return A string representation of the object
     */
    @Override
    public String toString() {
        return this.trans.toString();
    }

    /**
     * Gets the amount of the transaction_entity.
     * @return The amount of the transaction in cents.
     */
    public long getAmount() {
        return this.trans.getAmount();
    }

    /**
     * Sets the amount of the transaction_entity.
     * @param amount The amount in cents. Can not be negative.
     * @return True if the amount has been set succesfully, false otherwise.
     */
    public boolean setAmount(long amount) {
        return this.trans.setAmount(amount);
    }

    /**
     * Gets the description of the transaction_entity.
     * @return The desciption of the transaction. This can be null or empty.
     */
    public String getDescription() {
        return this.trans.getDescription();
    }

    /**
     * Sets the description of the transaction_entity.
     * @param description The desciption of the transaction. This can be null or empty.
     */
    public void setDescription(String description) {
        this.trans.setDescription(description);
    }

    /**
     * Gets the bank account number from which the amount should be deducted.
     * @return The bank account number.
     */
    public String getAccountFrom() {
        return this.trans.getAccountFrom();
    }

    /**
     * Sets the bank account number from which the amount should be deducted.
     * @param accountFrom The bank account number.
     */
    public void setAccountFrom(String accountFrom) {
        this.trans.setAccountFrom(accountFrom);
    }

    /**
     * gets the bank account number to which the amount should be tranfererd.
     * @return The bank account number.
     */
    public String getAccountTo() {
        return this.trans.getAccountTo();
    }

    /**
     * Sets the bank account number to which the amount should be tranfererd.
     * @param accountTo The bank account number.
     */
    public void setAccountTo(String accountTo) {
        this.trans.setAccountTo(accountTo);
    }

    /**
     * Gets the date of the transaction_entity.
     * @return Date of the transaction.
     */
    public Date getDate() {
        return this.trans.getDate();
    }

    /**
     * Gets the date of the transaction_entity.
     * @param date The date of the transaction.
     */
    public void setDate(Date date) {
        this.trans.setDate(date);
    }
}
