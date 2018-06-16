package com.ark.bank;

import com.ark.BankConnectionInfo;
import com.ark.BankTransaction;
import com.ark.Customer;

import java.util.List;
import java.util.Observer;

/**
 * @author Rick van der Heijden
 */
public interface IBankController {
    /**
     * Logs in the customer with the provided credentials.
     * @param name Name of the customer.
     * @param residence Residence of the customer.
     * @param password Password of the customer.
     * @return SessionKey if the login succeeds, null otherwise.
     */
    String login(String name, String residence, String password);

    /**
     * Logs out the customer with the procided session key.
     * @param sessionKey The session key of the logged in user. Can not be null or empty and should be valid.
     * @return True if logout succeeds, false otherwise.
     */
    boolean logout(String sessionKey);

    /**
     * Gets the customer that has been created before, only of the logged in user.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param name This is the name of the customer. Can not be null or empty.
     * @param residence This is the residence of the customer. Can not be null or empty.
     * @return The customer. Will be null if parameters are not valid.
     */
    Customer getCustomer(String sessionKey, String name, String residence);

    /**
     * Creates a new customer. The combination of the name and residence should be unique.
     * @param name This is the name of the customer. Can not be null or empty.
     * @param residence This is the residence of the customer. Can not be null or empty.
     * @param password This is the password. This should be between 8 and 50 characters long.
     * @return The newly created customer. Will be null if creation is not successful.
     */
    Customer createCustomer(String name, String residence, String password);


    /**
     * Removes the customer that has been created before, only of the logged in user.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param name This is the name of the customer. Can not be null or empty.
     * @param residence This is the residence of the customer. Can not be null or empty.
     * @return True if the customer has been removed succesfully, false otherwise.
     */
    boolean removeCustomer(String sessionKey, String name, String residence);

    /**
     * Creates a new bank account.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param owner This is the owner of the new bank account. This should be an existing customer.
     * @return The newly created bank account. Will be null if creation is not successful.
     */
    IBankAccount createBankAccount(String sessionKey, Customer owner);

    /**
     * Gets a bank account of the logged in user, using a bank account number.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param bankAccountNumber This is the bank account number. Can not be null or empty.
     * @return The requested bank account. Will be null if parameters are not valid.
     */
    IBankAccount getBankAccount(String sessionKey, String bankAccountNumber);

    /**
     * Gets all the bank account numbers of the logged in user.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @return A list of bank account numbers. Empty (not null) when no bank accounts are found, or if the session is invalid.
     */
    List<String> getBankAccountNumbers(String sessionKey);

    /**
     * Executes a bankTransaction (used for incoming transactions). The owner of the sessionKey should match the owner of the
     * bank account that is used to get the money from.
     * @param bankTransaction This is the bankTransaction which holds the amount and banknumbers. Should have valid entries and can not be null.
     * @return True if the bankTransaction can be executed succesfully (on both receiving and sending ends), false otherwise.
     */
    boolean executeTransaction(BankTransaction bankTransaction);

    /**
     * Executes a bankTransaction (used for outgoing transactions). The owner of the sessionKey should match the owner of the
     * bank account that is used to get the money from.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param bankTransaction This is the bankTransaction which holds the amount and banknumbers. Should have valid entries and can not be null.
     * @return True if the bankTransaction can be executed succesfully (on both receiving and sending ends), false otherwise.
     */
    boolean executeTransaction(String sessionKey, BankTransaction bankTransaction);

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
    List<BankTransaction> getTransactions(String sessionKey, String bankAccountNumber);

    /**
     * Adds an observer to the object. Normally done by extending the class with the Observable class.
     * Done here as the interface cannot extend.
     * @param o an observer to be added.
     */
    void addObserver(Observer o);

    /**
     * Sets the credit limit of the user to the given amount.
     *
     * @param sessionKey  The session key that is given after a succesful login. It should be valid and active.
     * @param bankAccountNr The bankAccountNr of which the credit limit had to be changed
     * @param limit       The new Credit limit
     * @return True if the limit has been changed, false otherwise
     */
    boolean setCreditLimit(String sessionKey, String bankAccountNr, long limit);

}
