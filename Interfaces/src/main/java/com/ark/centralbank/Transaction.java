package com.ark.centralbank;

import javax.jws.WebService;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Rick van der Heijden
 */
@WebService
public class Transaction implements Serializable {
    private double amount;
    private String description;
    private String accountFrom;
    private String accountTo;
    private Date date;


    public Transaction() {
    }

    public Transaction(double amount, String description, String accountFrom, String accountTo) {
        this.amount = amount;
        this.description = description;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.date = new Date();
    }

   /* public Transaction(double amount, String description, String accountFrom, String accountTo, Date date) {
        this.amount = amount;
        this.description = description;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.date = date;
    }*/

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Van: " + this.accountFrom + " Naar: " + this.accountTo + " €" + String.valueOf(this.amount);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
