package com.ark.bankingapplication;

import com.ark.BankTransaction;
import com.ark.Customer;
import com.ark.bank.IBankAccount;
import com.ark.bank.IBankForClientLogin;
import com.ark.bank.IBankForClientSession;
import fontyspublisher.IRemotePropertyListener;
import fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Observable;

/**
 * @author Arthur Doorgeest
 */
class BankConnector extends Observable implements IBankConnector, IRemotePropertyListener {

    private IBankForClientLogin bankForClientLogin;
    private IBankForClientSession bankForClientSession;

    public BankConnector() throws RemoteException {
        super();
        UnicastRemoteObject.exportObject(this, 0);
    }

    /**
     * Method to connect to the Remote BankHost
     *
     * @param bankId String of the Bank
     * @return Returns a True when the the connection to the bank is succesfull, Otherwise a False is returned.
     * @throws RemoteException   Thrown when remote method call fails.
     * @throws NotBoundException is thrown if an attempt is made to lookup or unbind in the registry a name that has
     *                           * no associated binding.
     */
    public boolean connect(String bankId) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IRemotePublisherForListener remotePublisherForListener = (IRemotePublisherForListener) registry.lookup("bankPublisher" + bankId);
        remotePublisherForListener.subscribeRemoteListener(this, "transactionExecuted");
        remotePublisherForListener.subscribeRemoteListener(this, "sessionTerminated");

        bankForClientLogin = (IBankForClientLogin) registry.lookup("bank" + bankId);
        bankForClientSession = (IBankForClientSession) registry.lookup("bank" + bankId);

        return ((bankForClientLogin != null) && (bankForClientSession != null));
    }

    /**
     * Method to login to the bank
     * @param name String
     * @param residence String
     * @param password String
     * @return SessionKey when login is succesfull, otherwise Null
     * @throws RemoteException Thrown when remote method call fails.
     */
    public String login(String name, String residence, String password) throws RemoteException {
        if (this.bankForClientLogin == null) {
            return null;
        }
        return this.bankForClientLogin.login(name, residence, password);
    }

    /**
     * Method to create a new customer
     * @param name String
     * @param residence String
     * @param password String
     * @return Customer when succesfull, otherwise Null
     * @throws RemoteException Thrown when remote method call fails.
     */
    public Customer createCustomer(String name, String residence, String password) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }
        return this.bankForClientSession.createCustomer(name, residence, password);
    }

    /**
     * Method to create a new BankAccount
     * @param sessionKey to verify that the customer is logged in.
     * @param customer to connect BankAccount to
     * @return IBankAccount of the just created bankAccount
     * @throws RemoteException Thrown when remote method call fails.
     */
    public IBankAccount createBankAccount(String sessionKey, Customer customer) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }
        return this.bankForClientSession.createBankAccount(sessionKey, customer);
    }

    /**
     * Method to get all the transactions connected to a bankaccount with the given bankaccountNR
     * @param sessionKey to verify that the customer is logged in.
     * @param bankNumber of the bankAccount
     * @return a List op Transactions
     * @throws RemoteException Thrown when remote method call fails.
     */
    public List<BankTransaction> getTransactions(String sessionKey, String bankNumber) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }

        return this.bankForClientSession.getTransactions(sessionKey, bankNumber);
    }

    /**
     *  Method to get all bankAccount Numbers of the logged in customer by SessionKey
     * @param sessionKey to verify that the customer is logged in.
     * @return List of BankAccount numbers
     * @throws RemoteException Thrown when remote method call fails.
     */
    public List<String> getBankAccountNumbers(String sessionKey) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }

        return this.bankForClientSession.getBankAccountNumbers(sessionKey);
    }

    /**
     *  Method to execute a bankTransaction
     * @param sessionKey to verify that the customer is logged in.
     * @param bankTransaction that needs to be executed
     * @return True if the BankTransaction is succesfull, otherswise false
     * @throws RemoteException Thrown when remote method call fails.
     */
    public boolean executeTransaction(String sessionKey, BankTransaction bankTransaction) throws RemoteException {
        if (this.bankForClientSession == null) {
            return false;
        }
        return this.bankForClientSession.executeTransaction(sessionKey, bankTransaction);
    }

    /**
     *  Method to get the Customer by SessionKey, Name and Residence
     * @param sessionKey to verify that the customer is logged in.
     * @param name of the Customer
     * @param residence of the Customer
     * @return Customer when found, otherwise null
     * @throws RemoteException Thrown when remote method call fails.
     */
    public Customer getCustomer(String sessionKey, String name, String residence) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }
        return this.bankForClientSession.getCustomer(sessionKey, name, residence);
    }

    /**
     * Method to get a bankAccount by its number
     * @param sessionKey to verify that the customer is logged in.
     * @param bankAccountNr used to search by
     * @return IBankAccount if found, otherwise null
     * @throws RemoteException Thrown when remote method call fails.
     */
    public IBankAccount getBankAccount(String sessionKey, String bankAccountNr) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }
        return this.bankForClientSession.getBankAccount(sessionKey, bankAccountNr);
    }

    /**
     * Method to change the bankingApplication in case of a change is subscribed events
     * @param propertyChangeEvent to check which property is changed
     */
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("transactionExecuted")) {
            setChanged();
            notifyObservers("transactionExecuted");
        } else if (propertyChangeEvent.getPropertyName().equals("sessionTerminated")) {
            setChanged();
            notifyObservers("sessionTerminated");
        }
    }

    /**
     *  Logout from the bankingApplication, used to terminate the session
     * @param sessionKey to verify that the customer is logged in.
     * @return True if logout is succesfull, False is session was not found, Same result
     * @throws RemoteException Thrown when remote method call fails.
     */
    public boolean logout(String sessionKey) throws RemoteException {
        if (this.bankForClientLogin == null) {
            return false;
        }
        return this.bankForClientLogin.logout(sessionKey);
    }

    /**
     * Method to change the Credit Limit of a BankAccount.
     *
     * @param sessionKey    to verify that the customer is logged in.
     * @param bankAccountNr of which the Credit limit has to change
     * @param limit         the new Credit limit
     * @return True if the Credit Limit was changed succesfull, otherwise false
     * @throws RemoteException Thrown when remote method call fails.
     */
    public boolean setCreditLimit(String sessionKey, String bankAccountNr, long limit) throws RemoteException {
        if (this.bankForClientSession == null) {
            return false;
        }
        return this.bankForClientSession.setCreditLimit(sessionKey, bankAccountNr, limit);
    }
}
