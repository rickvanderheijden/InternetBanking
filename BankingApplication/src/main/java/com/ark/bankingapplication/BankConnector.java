package com.ark.bankingapplication;

import com.ark.Customer;
import com.ark.Transaction;
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

class BankConnector extends UnicastRemoteObject implements IRemotePropertyListener {

    private IBankForClientLogin bankForClientLogin;
    private IBankForClientSession bankForClientSession;
    private Controller controller;

    public BankConnector(Controller controller) throws RemoteException {
        super();
        this.controller = controller;
    }

    public boolean connect(String bankId) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IRemotePublisherForListener remotePublisherForListener = (IRemotePublisherForListener) registry.lookup("bankPublisher" + bankId);
        remotePublisherForListener.subscribeRemoteListener(this, "transactionExecuted");

        bankForClientLogin = (IBankForClientLogin) registry.lookup("bank" + bankId);
        bankForClientSession = (IBankForClientSession) registry.lookup("bank" + bankId);

        return ((bankForClientLogin != null) && (bankForClientSession != null));
    }

    public String login(String name, String residence, String password) throws RemoteException {
        if (this.bankForClientLogin == null) {
            return null;
        }

        return this.bankForClientLogin.login(name, residence, password);
    }

    public Customer createCustomer(String name, String residence, String password) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }

        return this.bankForClientSession.createCustomer(name, residence, password);
    }

    public IBankAccount createBankAccount(String sessionKey, Customer customer) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }

        return this.bankForClientSession.createBankAccount(sessionKey, customer);
    }

    public List<Transaction> getTransactions(String sessionKey, String bankNumber) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }

        return this.bankForClientSession.getTransactions(sessionKey, bankNumber);
    }

    public List<String> getBankAccountNumbers(String sessionKey) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }

        return this.bankForClientSession.getBankAccountNumbers(sessionKey);
    }

    public boolean executeTransaction(String sessionKey, Transaction transaction) throws RemoteException {
        if (this.bankForClientSession == null) {
            return false;
        }

        return this.bankForClientSession.executeTransaction(sessionKey, transaction);
    }

    public Customer getCustomer(String sessionKey, String name, String residence) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }
        return this.bankForClientSession.getCustomer(sessionKey, name, residence);
    }

    public IBankAccount getBankAccount(String sessionKey, String bankAccountNr) throws RemoteException {
        if (this.bankForClientSession == null) {
            return null;
        }
        return this.bankForClientSession.getBankAccount(sessionKey, bankAccountNr);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("transactionExecuted")) {
            controller.transactionExecuted();
        }
    }

    public boolean logout(String sessionKey) throws RemoteException {
        if (this.bankForClientLogin == null) {
            return false;
        }
        return this.bankForClientLogin.logout(sessionKey);
    }
}
