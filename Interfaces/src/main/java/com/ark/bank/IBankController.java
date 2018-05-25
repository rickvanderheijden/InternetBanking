package com.ark.bank;

import com.ark.centralbank.Transaction;

import java.util.List;

/**
 * @author Rick van der Heijden
 */
public interface IBankController {
    String login(String name, String residence, String password);
    boolean logout(String sessionKey);

    Customer getCustomer(String sessionKey, String name, String residence);
    Customer createCustomer(String name, String residence, String password);

    //TODO: Is owner needed? Only sessionKey?
    BankAccount createBankAccount(String sessionKey, Customer owner);

    BankAccount getBankAccount(String sessionKey, String bankAccountNumber);

    List<String> getBankAccountNumbers(String sessionKey);
    boolean executeTransaction(Transaction transaction);

    boolean executeTransaction(String sessionKey, Transaction transaction);
    boolean isSessionActive(String sessionKey);
    boolean refreshSession(String sessionKey);
    boolean terminateSession(String sessionKey);
    String getBankId();


    //TODO: use this for GUIConnector
    //boolean executeTransaction(String sessionKey, Transaction transaction );

}
