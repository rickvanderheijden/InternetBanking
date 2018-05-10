package com.ark.bank;

import com.ark.centralbank.Transaction;
import fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GUIConnector extends UnicastRemoteObject implements IBankForClientSession, IBankForClientLogin {

    private final Random random = new Random();
    private RemotePublisher remotePublisher;
    private BankController bankController;
    private Set<Integer> sessionIds = new HashSet<>();

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
    public boolean isActive() throws RemoteException {
        return false;
    }

    @Override
    public boolean refreshSession() throws RemoteException {
        return false;
    }

    @Override
    public void terminateSession() throws RemoteException {

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
    public Customer getCustomer(String name, String residence) throws RemoteException {
        return null;
    }

    @Override
    public BankAccount getBankAccount(String bankAccountNumber) throws RemoteException {
        return null;
    }

    @Override
    public List<Transaction> getTransactions(String bankAccountNumber) throws RemoteException {
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
    public int login(String name, String residence, String password) {

        //TODO: Move to session or something and add timer etc

        if (bankController == null) {
            return -1;
        }

        Customer customer = bankController.getCustomer(name, residence);

        if (customer == null || !customer.isPasswordValid(password)) {
            return -1;
        }

        int sessionId = random.nextInt(Integer.MAX_VALUE);

        while (!sessionIds.add(sessionId)) {
            sessionId = random.nextInt(Integer.MAX_VALUE);
        }

        return sessionId;
    }
}
