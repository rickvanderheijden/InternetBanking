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
     * @param name This is the name of the customer. Can not be null or empty.
     * @param residence This is the residence of the customer. Can not be null or empty.
     * @param password This is the password. This should be between 8 and 50 characters long.
     * @return Customer This is the newly created customer. Will be null if creation is not successful.
     * @throws RemoteException Thrown when remote method call fails.
     */
    Customer createCustomer(String name, String residence, String password) throws RemoteException;

    /**
     * This method can be used to get the customer that has been created before, only of the logged in user.
     * @param sessionKey This is the session key that is given after a succesful login. It should be valid.
     * @param name This is the name of the customer. Can not be null or empty.
     * @param residence This is the residence of the customer. Can not be null or empty.
     * @return Customer This is the customer. Will be null if parameters are not valid.
     * @throws RemoteException Thrown when remote method call fails.
     */
    Customer getCustomer(String sessionKey, String name, String residence) throws RemoteException;

    /**
     * This method can be used to remove the customer that has been created before, only of the logged in user.
     * @param sessionKey This is the session key that is given after a succesful login. It should be valid.
     * @param name This is the name of the customer. Can not be null or empty.
     * @param residence This is the residence of the customer. Can not be null or empty.
     * @return boolean True if the customer has been removed succesfully, false otherwise.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean removeCustomer(String sessionKey, String name, String residence) throws RemoteException;

    /**
     * This method can be used to get all the bank account numbers of the logged in user.
     * @param sessionKey This is the session key that is given after a succesful login. It should be valid.
     * @return List<String> This is a list of bank account numbers. Empty (not null) when no bank accounts are found, or if the session is invalid.
     * @throws RemoteException Thrown when remote method call fails.
     */
    List<String> getBankAccountNumbers(String sessionKey) throws RemoteException;

    /**
     * This method can be used to get a bankaccount of the logged in user, using a bankaccount number.
     * @param sessionKey This is the session key that is given after a succesful login. It should be valid.
     * @param bankAccountNumber This is the bankaccount number. Can not be null or empty.
     * @return BankAccount This is the requested bank account. Will be null if parameters are not valid.
     * @throws RemoteException Thrown when remote method call fails.
     */
    BankAccount getBankAccount(String sessionKey, String bankAccountNumber) throws RemoteException;

    /**
     * This method can be used to get a list of transactions of a bankaccount (both incoming and uitgoing).
     * @param sessionKey This is the session key that is given after a succesful login. It should be valid.
     * @param bankAccountNumber This is the bankaccount number. Can not be null or empty.
     * @return List of Transactions. Will be empty (not null) if parameters are not valid.
     * @throws RemoteException Thrown when remote method call fails.
     */
    List<Transaction> getTransactions(String sessionKey, String bankAccountNumber) throws RemoteException;

    /**
     * This method can be used to execute a transaction. The owner of the sessionKey should match the owner of the
     * bankaccount that is used to get the money from.
     * @param sessionKey This is the session key that is given after a succesful login. It should be valid.
     * @param transaction This is the transaction which holds the amount and banknumbers. Should have valid entries and can not be null.
     * @return boolean True is the transaction can be executed succesfully (on both receiving and sending ends), false otherwise.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean executeTransaction(String sessionKey, Transaction transaction) throws RemoteException;
}
