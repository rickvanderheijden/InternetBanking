package com.ark.bankingapplication;

import com.ark.bank.BankAccount;
import com.ark.bank.Customer;
import com.ark.bank.IBankForClientLogin;
import com.ark.bank.IBankForClientSession;
import com.ark.centralbank.Transaction;
import fontyspublisher.IRemotePublisherForListener;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class BankConnector {

    private String sessionKey;
    private IBankForClientLogin bankForClientLogin;
    private IBankForClientSession bankForClientSession;

    public String getSessionKey() {
        return sessionKey;
    }

    public boolean connect(String bankId) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IRemotePublisherForListener remotePublisher = (IRemotePublisherForListener) registry.lookup("bankPublisher" + bankId);
        //remotePublisher.subscribeRemoteListener(this, "");

        bankForClientLogin = (IBankForClientLogin) registry.lookup("bank" + bankId);
        bankForClientSession = (IBankForClientSession) registry.lookup("bank" + bankId);

        return ((bankForClientLogin != null) && (bankForClientSession != null));
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public IBankForClientSession getIBankForClientSession() {
        return bankForClientSession;
    }

    public IBankForClientLogin getIBankForClientLogin() {
        return bankForClientLogin;
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

    public BankAccount createBankAccount(String sessionKey, Customer customer) throws RemoteException {
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
}
