package com.ark.bank;

import com.ark.centralbank.Transaction;

/**
 * @author Rick van der Heijden
 */
public interface IBankController {
    String login(String name, String residence, String password);
    boolean logout(String sessionKey);
    Customer getCustomer(String name, String residence);
    Customer createCustomer(String name, String residence, String password);
    BankAccount createBankAccount(Customer owner);
    boolean executeTransaction(Transaction transaction);

}
