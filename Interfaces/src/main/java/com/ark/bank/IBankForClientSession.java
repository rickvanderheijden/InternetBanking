package com.ark.bank;

import com.ark.centralbank.Transaction;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Rick van der Heijden
 */
public interface IBankForClientSession extends Remote {
    /**
     * This method can be used to check if the session is still active.
     * @return boolean This returns true if the session is active, false if not.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean isSessionActive(String sessionKey) throws RemoteException;

    /**
     * This method can be used to refresh the session. This resets the timer for the active session.
     * If the session was already expired, this will fail.
     * @return boolean This returns true if the session is active and has been reset, false if not.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean refreshSession(String sessionKey) throws RemoteException;

    /**
     * This method can be used to terminate the current session.
     * @return boolean This returns true if the session is active and has been terminated, false if not.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean terminateSession(String sessionkey) throws RemoteException;

    /**
     * This method can be used create a new bank account.
     * @param sessionKey This is the session key that is given after a succesful login. It should be valid.
     * @param owner This is the owner of the new bank account. This should be an existing customer.
     * @return BankAccount This is the newly created bank account. Will be null if creation is not successful.
     * @throws RemoteException Thrown when remote method call fails.
     */
    BankAccount createBankAccount(String sessionKey, Customer owner) throws RemoteException;

    /**
     * This method can be used create a new customer. The combination of the name and residence should be unique.
     * @param name This is the name of the customer. Can not be null.
     * @param residence This is the residence of the customer. Can not be null.
     * @param password This is the password. This should be between 8 and 50 characters long.
     * @return Customer This is the newly created customer. Will be null if creation is not successful.
     * @throws RemoteException Thrown when remote method call fails.
     */
    Customer createCustomer(String name, String residence, String password) throws RemoteException;


    /**
     *
     * @param name
     * @param residence
     * @return
     * @throws RemoteException
     */
    Customer getCustomer(String sessionKey, String name, String residence) throws RemoteException;

    /**
     * This method can be used to get all the bank account numbers of the logged in user.
     *
     * @param sessionKey This is the session key that is given after a succesful login. It should be valid.
     * @return List<String> This is a list of bank account numbers. Empty (not null) when no bank accounts are found, or if the session is invalid.
     * @throws RemoteException Thrown when remote method call fails.
     */
    List<String> getBankAccountNumbers(String sessionKey) throws RemoteException;

    BankAccount getBankAccount(String sessionKey, String bankAccountNumber) throws RemoteException;

    List<Transaction> getTransactions(String sessionKey, String bankAccountNumber) throws RemoteException;

    boolean executeTransaction(String sessionKey, Transaction transaction) throws RemoteException;
    IBankForClientLogin getBankLogin();
}
