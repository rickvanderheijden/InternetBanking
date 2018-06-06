package com.ark.bank;

import com.ark.*;
import fontyspublisher.RemotePublisher;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Rick van der Heijden
 */
public class GUIConnector extends UnicastRemoteObject implements IBankForClientSession, IBankForClientLogin, Observer {

    private IBankController bankController;
    private RemotePublisher remotePublisher;

    public GUIConnector(IBankController bankController) throws RemoteException {
        super();

        this.bankController = bankController;
        remotePublisher = new RemotePublisher();
        remotePublisher.registerProperty("transactionExecuted");

        String bankId = "";
        if (bankController != null) {

            //TODO: Do this is a better way
            ((Observable)bankController).addObserver(this);
            bankId = bankController.getBankId();
        }

        Registry registry;

        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(1099);
        }

        if (registry != null) {
            registry.rebind("bankPublisher" + bankId, remotePublisher);
            registry.rebind("bank" + bankId, this);
        }
    }

    @Override
    public boolean isSessionActive(String sessionKey) {
        if (bankController == null) {
            return false;
        }

        return bankController.isSessionActive(sessionKey);
    }

    @Override
    public boolean refreshSession(String sessionKey) {
        if (bankController == null) {
            return false;
        }

        return bankController.refreshSession(sessionKey);
    }

    @Override
    public boolean terminateSession(String sessionKey) {
        if (bankController == null) {
            return false;
        }

        return bankController.terminateSession(sessionKey);
    }

    @Override
    public IBankAccount createBankAccount(String sessionKey, Customer owner) {
        return bankController.createBankAccount(sessionKey, owner);
    }

    @Override
    public List<String> getBankAccountNumbers(String sessionKey) {
        if (bankController == null) {
            return new ArrayList<>();
        }

        return bankController.getBankAccountNumbers(sessionKey);
    }

    @Override
    public Customer createCustomer(String name, String residence, String password) {
        if (bankController == null) {
            return null;
        }

        return bankController.createCustomer(name, residence, password);
    }


    //TODO: ONLY FOR CURRENT CUSTOMER? IN THAT CASE, NAME AND RESIDENCE CAN BE REMOVED
    @Override
    public Customer getCustomer(String sessionKey, String name, String residence) {
        if (bankController == null) {
            return null;
        }

        return bankController.getCustomer(sessionKey, name, residence);
    }

    @Override
    public boolean removeCustomer(String sessionKey, String name, String residence) {
        if (bankController == null) {
            return false;
        }

        return bankController.removeCustomer(sessionKey, name, residence);
    }

    @Override
    public IBankAccount getBankAccount(String sessionKey, String bankAccountNumber) {
        if (bankController == null) {
            return null;
        }

        return bankController.getBankAccount(sessionKey, bankAccountNumber);
    }

    @Override
    public List<Transaction> getTransactions(String sessionKey, String bankAccountNumber) {
        if (bankController == null) {
            return new ArrayList<>();
        }

        return bankController.getTransactions(sessionKey, bankAccountNumber);
    }

    @Override
    public boolean executeTransaction(String sessionKey, Transaction transaction) {
        if (bankController == null) {
            return false;
        }

        return bankController.executeTransaction(sessionKey, transaction);
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

    @Override
    public void update(Observable o, Object arg) {
        try {
            remotePublisher.inform("transactionExecuted", null, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
