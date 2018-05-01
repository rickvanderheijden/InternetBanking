package com.ark.bank;

import com.ark.centralbank.Transaction;
import fontyspublisher.RemotePublisher;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class GUIConnector extends UnicastRemoteObject implements IBankForClient {

    private RemotePublisher remotePublisher;
    private BankController bankController;

    public GUIConnector(BankController bankController) throws RemoteException {
        super();

        this.bankController = bankController;

        remotePublisher = new RemotePublisher();
        //remotePublisher.registerProperty("fondsen");

        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("bankPublisher", remotePublisher);
            registry.rebind("bank", this);
        } catch (RemoteException e) {

        }
    }

    @Override
    public BankAccount createBankAccount(Customer owner) {
        return bankController.createBankAccount(owner);
    }

    @Override
    public boolean executeTransaction(Transaction transaction) {
        if (bankController == null) {
            return false;
        }

        return bankController.executeTransaction(transaction);
    }
}
