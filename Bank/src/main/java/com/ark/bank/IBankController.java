package com.ark.bank;

import com.ark.BankAccount;
import com.ark.BankConnectionInfo;
import com.ark.Customer;
import com.ark.Transaction;

import java.util.List;
import java.util.Observer;

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
    IBankAccount createBankAccount(String sessionKey, Customer owner);
    IBankAccount getBankAccount(String sessionKey, String bankAccountNumber);

    List<String> getBankAccountNumbers(String sessionKey);
    boolean executeTransaction(Transaction transaction);
    boolean executeTransaction(String sessionKey, Transaction transaction);

    /**
     * Set the default time (in milliseconds) in which the session will expire.
     * @param sessionTime The time in which the session will expire.
     */
    void setSessionTime(int sessionTime);

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

    /**
     * Adds an observer to the object. Normally done by extending the class with the Observable class.
     * Done here as the interface cannot extend.
     * @param o an observer to be added.
     */

    public void addObserver(Observer o);

    /**
     * Sets the credit limit of the user to the given amount.
     *
     * @param sessionKey  The session key that is given after a succesful login. It should be valid and active.
     * @param bankAccount The bankAccount of which the credit limit had to be changed
     * @param limit       The new Credit limit
     * @return True if the limit has been changed, false otherwise
     */
    boolean setCreditLimit(String sessionKey, BankAccount bankAccount, long limit);

}
