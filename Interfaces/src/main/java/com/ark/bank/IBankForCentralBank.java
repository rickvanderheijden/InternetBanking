package com.ark.bank;

import com.ark.centralbank.Transaction;

import javax.jws.WebService;

@WebService
public interface IBankForCentralBank {
    boolean executeTransaction(Transaction transaction);
}
