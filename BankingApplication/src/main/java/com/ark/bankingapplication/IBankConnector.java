package com.ark.bankingapplication;

import com.ark.BankTransaction;
import com.ark.Customer;
import com.ark.bank.IBankAccount;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public interface IBankConnector {
    /**
     * Method to connect to the Remote BankHost
     *
     * @param bankId String of the Bank
     * @return Returns a True when the the connection to the bank is succesfull, Otherwise a False is returned.
     * @throws RemoteException   Thrown when remote method call fails.
     * @throws NotBoundException is thrown if an attempt is made to lookup or unbind in the registry a name that has
     *                           * no associated binding.
     */
    boolean connect(String bankId) throws RemoteException, NotBoundException;


    /**
     * Method to login to the bank
     *
     * @param name      String
     * @param residence String
     * @param password  String
     * @return SessionKey when login is succesfull, otherwise Null
     * @throws RemoteException Thrown when remote method call fails.
     */
    String login(String name, String residence, String password) throws RemoteException;

    /**
     * Method to create a new customer
     * @param name String
     * @param residence String
     * @param password String
     * @return Customer when succesfull, otherwise Null
     * @throws RemoteException Thrown when remote method call fails.
     */
    Customer createCustomer(String name, String residence, String password) throws RemoteException;


    /**
     * Method to create a new BankAccount
     * @param sessionKey to verify that the customer is logged in.
     * @param customer to connect BankAccount to
     * @return IBankAccount of the just created bankAccount
     * @throws RemoteException Thrown when remote method call fails.
     */
    IBankAccount createBankAccount(String sessionKey, Customer customer) throws RemoteException;

    /**
     * Method to get all the transactions connected to a bankaccount with the given bankaccountNR
     * @param sessionKey to verify that the customer is logged in.
     * @param bankNumber of the bankAccount
     * @return a List op Transactions
     * @throws RemoteException Thrown when remote method call fails.
     */
    List<BankTransaction> getTransactions(String sessionKey, String bankNumber) throws RemoteException;

    /**
     *  Method to get all bankAccount Numbers of the logged in customer by SessionKey
     * @param sessionKey to verify that the customer is logged in.
     * @return List of BankAccount numbers
     * @throws RemoteException Thrown when remote method call fails.
     */
    List<String> getBankAccountNumbers(String sessionKey) throws RemoteException;


    /**
     *  Method to execute a bankTransaction
     * @param sessionKey to verify that the customer is logged in.
     * @param bankTransaction that needs to be executed
     * @return True if the BankTransaction is succesfull, otherswise false
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean executeTransaction(String sessionKey, BankTransaction bankTransaction) throws RemoteException;

    /**
     *  Method to get the Customer by SessionKey, Name and Residence
     * @param sessionKey to verify that the customer is logged in.
     * @param name of the Customer
     * @param residence of the Customer
     * @return Customer when found, otherwise null
     * @throws RemoteException Thrown when remote method call fails.
     */
    Customer getCustomer(String sessionKey, String name, String residence) throws RemoteException;

    /**
     * Method to get a bankAccount by its number
     * @param sessionKey to verify that the customer is logged in.
     * @param bankAccountNumber used to search by
     * @return IBankAccount if found, otherwise null
     * @throws RemoteException Thrown when remote method call fails.
     */
    IBankAccount getBankAccount(String sessionKey, String bankAccountNumber) throws RemoteException;

    /**
     *  Logout from the bankingApplication, used to terminate the session
     * @param sessionKey to verify that the customer is logged in.
     * @return True if logout is succesfull, False is session was not found, Same result
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean logout(String sessionKey) throws RemoteException;

    /**
     * Method to change the Credit Limit of a BankAccount.
     *
     * @param sessionKey    to verify that the customer is logged in.
     * @param bankAccountNumber of which the Credit limit has to change
     * @param limit         the new Credit limit
     * @return True if the Credit Limit was changed succesfull, otherwise false
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean setCreditLimit(String sessionKey, String bankAccountNumber, long limit) throws RemoteException;

    /**
     * Method to subscribe to a remote publisher for transactions
     * @param bankAccountNumber to specify the remote publisher to subscribe to
     * @throws RemoteException Thrown when remote method call fails.
     */
    void subscribeToTransaction(String bankAccountNumber) throws RemoteException;

    /**
     * Method to unsubscribe to a remote publisher for transactions
     * @param bankAccountNumber to specify the remote publisher to subscribe to
     * @throws RemoteException Thrown when remote method call fails.
     */
    void unsubscribeToTransaction(String bankAccountNumber) throws RemoteException;
}
