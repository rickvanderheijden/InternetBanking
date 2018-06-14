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
    private IRemotePublisherForListener remotePublisherForListener;

    public BankConnector() throws RemoteException {
        super();
        UnicastRemoteObject.exportObject(this, 0);
    }

    @Override
    public boolean connect(String bankId) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        this.remotePublisherForListener = (IRemotePublisherForListener) registry.lookup("bankPublisher" + bankId);
        bankForClientLogin = (IBankForClientLogin) registry.lookup("bank" + bankId);
        bankForClientSession = (IBankForClientSession) registry.lookup("bank" + bankId);

        return ((bankForClientLogin != null) && (bankForClientSession != null));
    }

    @Override
    public String login(String name, String residence, String password) throws RemoteException {
        if (this.bankForClientLogin == null) {
            return null;
        }

        String sessionKey = this.bankForClientLogin.login(name, residence, password);
        this.subscribeToSession(sessionKey);
        return sessionKey;
    }

    @Override
    public Customer createCustomer(String name, String residence, String password) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }
        return this.bankForClientSession.createCustomer(name, residence, password);
    }

    @Override
    public IBankAccount createBankAccount(String sessionKey, Customer customer) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }
        return this.bankForClientSession.createBankAccount(sessionKey, customer);
    }

    @Override
    public List<BankTransaction> getTransactions(String sessionKey, String bankNumber) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }

        return this.bankForClientSession.getTransactions(sessionKey, bankNumber);
    }

    @Override
    public List<String> getBankAccountNumbers(String sessionKey) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }

        return this.bankForClientSession.getBankAccountNumbers(sessionKey);
    }

    @Override
    public boolean executeTransaction(String sessionKey, BankTransaction bankTransaction) throws RemoteException {
        if (this.bankForClientSession == null) {
            return false;
        }
        return this.bankForClientSession.executeTransaction(sessionKey, bankTransaction);
    }

    @Override
    public Customer getCustomer(String sessionKey, String name, String residence) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }
        return this.bankForClientSession.getCustomer(sessionKey, name, residence);
    }

    @Override
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
        if (propertyChangeEvent.getPropertyName().contains("transactionExecuted")) {
            setChanged();
            notifyObservers("transactionExecuted");
            System.out.println("PropertyChange Transaction: " + propertyChangeEvent.getPropertyName());
        } else if (propertyChangeEvent.getPropertyName().contains("sessionTerminated")) {
            setChanged();
            notifyObservers("sessionTerminated");
        }
    }

    @Override
    public boolean logout(String sessionKey) throws RemoteException {
        if (this.bankForClientLogin == null) {
            return false;
        }
        remotePublisherForListener.unsubscribeRemoteListener(this, "sessionTerminated" + sessionKey);
        return this.bankForClientLogin.logout(sessionKey);
    }

    @Override
    public boolean setCreditLimit(String sessionKey, String bankAccountNr, long limit) throws RemoteException {
        if (this.bankForClientSession == null) {
            return false;
        }
        return this.bankForClientSession.setCreditLimit(sessionKey, bankAccountNr, limit);
    }

    private void subscribeToSession(String sessionKey) throws RemoteException {
        if (this.remotePublisherForListener != null)
            remotePublisherForListener.subscribeRemoteListener(this, "sessionTerminated" + sessionKey);
    }

    @Override
    public void subscribeToTransaction(String bankAccountNumber) throws RemoteException {
        if (this.remotePublisherForListener != null)
            remotePublisherForListener.subscribeRemoteListener(this, "transactionExecuted" + bankAccountNumber);
    }

    @Override
    public void unsubscribeToTransaction(String bankAccountNumber) throws RemoteException {
        if (this.remotePublisherForListener != null)
            remotePublisherForListener.unsubscribeRemoteListener(this, "transactionExecuted" + bankAccountNumber);
    }
}
