package com.ark.bank;

import com.ark.centralbank.Transaction;

import javax.jws.WebService;

/**
 * @author Rick van der Heijden
 */
@WebService
public interface IBankForCentralBank {
    boolean executeTransaction(Transaction transaction);
}
