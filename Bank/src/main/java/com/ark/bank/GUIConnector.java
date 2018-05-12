package com.ark.bank;

import com.ark.centralbank.Transaction;
import fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GUIConnector extends UnicastRemoteObject implements IBankForClientSession, IBankForClientLogin {

    private RemotePublisher remotePublisher;
    private BankController bankController;
    private Set<UUID> sessionKeys = new HashSet<>();

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

        //TODO: Move to session or something and add timer etc

        if (bankController == null) {
            return null;
        }

        Customer customer = bankController.getCustomer(name, residence);

        if (customer == null || !customer.isPasswordValid(password)) {
            return null;
        }

        UUID sessionKey = UUID.randomUUID();

        while (!sessionKeys.add(sessionKey)) {
            sessionKey = UUID.randomUUID();
        }

        return sessionKey.toString();
    }

    @Override
    public boolean logout(String sessionKey) {
        if ((sessionKey == null) || sessionKey.isEmpty()) {
            return false;
        }

        UUID uuid = UUID.fromString(sessionKey);

        if (!sessionKeys.contains(uuid)) {
            return false;
        } else {
            sessionKeys.remove(uuid);
        }

        return true;
    }
}
