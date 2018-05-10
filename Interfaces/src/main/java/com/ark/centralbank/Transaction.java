package com.ark.centralbank;

import javax.jws.WebService;
import java.io.Serializable;
import java.util.Date;

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
