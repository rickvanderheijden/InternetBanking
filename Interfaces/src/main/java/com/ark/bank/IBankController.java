package com.ark.bank;

import com.ark.centralbank.BankConnectionInfo;
import com.ark.centralbank.Transaction;

import java.util.List;

/**
 * @author Rick van der Heijden
 */
public interface IBankController {
    String login(String name, String residence, String password);
    boolean logout(String sessionKey);

    //TODO: Is name and residence needed? Only sessionKey? Extra security.... ?
    Customer getCustomer(String sessionKey, String name, String residence);
    Customer createCustomer(String name, String residence, String password);
    boolean removeCustomer(String sessionKey, String name, String residence);

    //TODO: Is owner needed? Only sessionKey?
    BankAccount createBankAccount(String sessionKey, Customer owner);
    BankAccount getBankAccount(String sessionKey, String bankAccountNumber);

    List<String> getBankAccountNumbers(String sessionKey);
    boolean executeTransaction(Transaction transaction);
    boolean executeTransaction(String sessionKey, Transaction transaction);

    /**
     * Checks if the session is still active.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @return True if the session is active, false if not.
     */
    boolean isSessionActive(String sessionKey);

    /**
     * Refreshes the session. This resets the timer for the active session.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @return True if the session is active and has been reset, false if not.
     */
    boolean refreshSession(String sessionKey);

    /**
     * Refreshes the session. This resets the timer for the active session.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @return True if the session is active and has been reset, false if not.
     */
    boolean terminateSession(String sessionKey);

    /**
     * Gets the id of the bank.
     * @return The id of the bank.
     */
    String getBankId();

    /**
     * Checks if a bank account number is valid; it will check the existence of the bank account on the bank.
     * @param bankAccountNumber This is the bank account number. Can not be null or empty.
     * @return True if the bank account exists, false otherwise.
     */
    boolean isValidBankAccountNumber(String bankAccountNumber);

    /**
     * Registers a bank to the central bank, if not already registered.
     * @param bankConnectionInfo Information needed to create the connection. Can not be null.
     * @return True if bank is registered correctly, false otherwise.
     */
    boolean registerBank(BankConnectionInfo bankConnectionInfo);

    /**
     * Gets a list of transactions of a bank account (both incoming and uitgoing).
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param bankAccountNumber This is the bank account number. Can not be null or empty.
     * @return List of transactions. Will be empty (not null) if parameters are not valid.
     */
    List<Transaction> getTransactions(String sessionKey, String bankAccountNumber);
}
