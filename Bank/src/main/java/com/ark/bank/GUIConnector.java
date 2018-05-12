package com.ark.bank;

import com.ark.centralbank.Transaction;
import fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * @author Rick van der Heijden
 */
public class GUIConnector extends UnicastRemoteObject implements IBankForClientSession, IBankForClientLogin {

    private RemotePublisher remotePublisher;
    private BankController bankController;

    public GUIConnector(BankController bankController) throws RemoteException {
        super();

        this.bankController = bankController;

        remotePublisher = new RemotePublisher();
        //remotePublisher.registerProperty("");

        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("bankPublisher", remotePublisher);
            registry.rebind("bank", this);
        } catch (RemoteException e) {

        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean refreshSession() {
        return false;
    }

    @Override
    public void terminateSession() {

    }

    @Override
    public BankAccount createBankAccount(Customer owner) {
        return bankController.createBankAccount(owner);
    }

    @Override
    public Customer createCustomer(String name, String residence, String password) {
        return bankController.createCustomer(name, residence, password);
    }

    @Override
    public Customer getCustomer(String name, String residence) {
        return null;
    }

    @Override
    public BankAccount getBankAccount(String bankAccountNumber) {
        return null;
    }

    @Override
    public List<Transaction> getTransactions(String bankAccountNumber) {
        return null;
    }

    @Override
    public boolean executeTransaction(Transaction transaction) {
        if (bankController == null) {
            return false;
        }

        return bankController.executeTransaction(transaction);
    }

    @Override
    public String login(String name, String residence, String password) {
        if (bankController == null) {
            return null;
        }

        return bankController.login(name, residence, password);
    }

    @Override
    public boolean logout(String sessionKey) {
        if (bankController == null) {
            return false;
        }

        return bankController.logout(sessionKey);
    }
}
