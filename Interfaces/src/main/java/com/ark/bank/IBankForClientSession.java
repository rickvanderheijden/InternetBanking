package com.ark.bank;

import com.ark.BankTransaction;
import com.ark.Customer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Rick van der Heijden
 */
public interface IBankForClientSession extends Remote {
    /**
     * Checks if the session is still active.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @return True if the session is active, false if not.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean isSessionActive(String sessionKey) throws RemoteException;

    /**
     * Refreshes the session. This resets the timer for the active session.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @return True if the session is active and has been reset, false if not.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean refreshSession(String sessionKey) throws RemoteException;

    /**
     * Terminate the current session and remove the session from the list.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @return True if the session is active and has been terminated, false if not.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean terminateSession(String sessionKey) throws RemoteException;

    /**
     * Creates a new bank account.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param owner This is the owner of the new bank account. This should be an existing customer.
     * @return The newly created bank account. Will be null if creation is not successful.
     * @throws RemoteException Thrown when remote method call fails.
     */
    IBankAccount createBankAccount(String sessionKey, Customer owner) throws RemoteException;

    /**
     * Creates a new customer. The combination of the name and residence should be unique.
     * @param name This is the name of the customer. Can not be null or empty.
     * @param residence This is the residence of the customer. Can not be null or empty.
     * @param password This is the password. This should be between 8 and 50 characters long.
     * @return The newly created customer. Will be null if creation is not successful.
     * @throws RemoteException Thrown when remote method call fails.
     */
    Customer createCustomer(String name, String residence, String password) throws RemoteException;

    /**
     * Gets the customer that has been created before, only of the logged in user.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param name This is the name of the customer. Can not be null or empty.
     * @param residence This is the residence of the customer. Can not be null or empty.
     * @return The customer. Will be null if parameters are not valid.
     * @throws RemoteException Thrown when remote method call fails.
     */
    Customer getCustomer(String sessionKey, String name, String residence) throws RemoteException;

    /**
     * Removes the customer that has been created before, only of the logged in user.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param name This is the name of the customer. Can not be null or empty.
     * @param residence This is the residence of the customer. Can not be null or empty.
     * @return True if the customer has been removed succesfully, false otherwise.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean removeCustomer(String sessionKey, String name, String residence) throws RemoteException;

    /**
     * Gets all the bank account numbers of the logged in user.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @return A list of bank account numbers. Empty (not null) when no bank accounts are found, or if the session is invalid.
     * @throws RemoteException Thrown when remote method call fails.
     */
    List<String> getBankAccountNumbers(String sessionKey) throws RemoteException;

    /**
     * Gets a bank account of the logged in user, using a bank account number.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param bankAccountNumber This is the bank account number. Can not be null or empty.
     * @return The requested bank account. Will be null if parameters are not valid.
     * @throws RemoteException Thrown when remote method call fails.
     */
    IBankAccount getBankAccount(String sessionKey, String bankAccountNumber) throws RemoteException;

    /**
     * Gets a list of transactions of a bank account (both incoming and uitgoing).
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param bankAccountNumber This is the bank account number. Can not be null or empty.
     * @return List of transactions. Will be empty (not null) if parameters are not valid.
     * @throws RemoteException Thrown when remote method call fails.
     */
    List<BankTransaction> getTransactions(String sessionKey, String bankAccountNumber) throws RemoteException;

    /**
     * Executes a bankTransaction. The owner of the sessionKey should match the owner of the
     * bank account that is used to get the money from.
     * @param sessionKey The session key that is given after a succesful login. It should be valid and active.
     * @param bankTransaction This is the bankTransaction which holds the amount and banknumbers. Should have valid entries and can not be null.
     * @return True if the bankTransaction can be executed succesfully (on both receiving and sending ends), false otherwise.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean executeTransaction(String sessionKey, BankTransaction bankTransaction) throws RemoteException;

    /**
     * Sets the credit limit of the user to the given amount.
     *
     * @param sessionKey  The session key that is given after a succesful login. It should be valid and active.
     * @param bankAccountNr The bankAccountNr of which the credit limit had to be changed
     * @param limit       The new Credit limit
     * @return True if the limit has been changed, false otherwise
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean setCreditLimit(String sessionKey, String bankAccountNr, long limit) throws RemoteException;
}
