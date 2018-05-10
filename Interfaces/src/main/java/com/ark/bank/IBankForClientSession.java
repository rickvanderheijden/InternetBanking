package com.ark.bank;

import com.ark.centralbank.Transaction;

import javax.xml.crypto.dsig.TransformService;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IBankForClientSession extends Remote {
    /**
     * This method can be used to check if the session is still active.
     * @return boolean This returns true if the session is active, false if not.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean isActive() throws RemoteException;

    /**
     * This method can be used to refresh the session. This resets the timer for the active session.
     * If the session was already expired, this will fail.
     * @return boolean This returns true if the session is active and has been reset, false if not.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean refreshSession() throws RemoteException;

    /**
     * This method can be used to terminate the current session.
     * @throws RemoteException Thrown when remote method call fails.
     */
    void terminateSession() throws RemoteException;

    /**
     * This method can be used create a new bank account.
     * @param owner This is the owner of the new bank account. This should be an existing customer.
     * @return BankAccount This is the newly created bank account. Will be null if creation is not successful.
     * @throws RemoteException Thrown when remote method call fails.
     */
    BankAccount createBankAccount(Customer owner) throws RemoteException;

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
    Customer getCustomer(String name, String residence) throws RemoteException;


    BankAccount getBankAccount(String bankAccountNumber) throws RemoteException;
    List<Transaction> getTransactions(String bankAccountNumber) throws RemoteException;
    boolean executeTransaction(Transaction transaction) throws RemoteException;

    //TODO: Move to ILogin or ISession or something
    int login(String name, String residence, String password) throws RemoteException;
}
