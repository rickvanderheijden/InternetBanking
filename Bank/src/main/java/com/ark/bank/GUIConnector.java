package com.ark.bank;

import com.ark.BankTransaction;
import com.ark.Customer;
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


        String bankId = "";
        if (bankController != null) {

            bankController.addObserver(this);
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
        if (bankController == null) {
            return null;
        }

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
    public List<BankTransaction> getTransactions(String sessionKey, String bankAccountNumber) {
        if (bankController == null) {
            return new ArrayList<>();
        }

        return bankController.getTransactions(sessionKey, bankAccountNumber);
    }

    @Override
    public boolean executeTransaction(String sessionKey, BankTransaction bankTransaction) throws RemoteException {
        if (bankController == null) {
            return false;
        }
        registerRemoteProperty("transactionExecuted" + bankTransaction.getAccountTo());
        return bankController.executeTransaction(sessionKey, bankTransaction);
    }

    @Override
    public boolean setCreditLimit(String sessionKey, String bankAccountNr, long limit) throws RemoteException {
        if (bankController == null) {
            return false;
        }

        return bankController.setCreditLimit(sessionKey, bankAccountNr, limit);
    }

    @Override
    public String login(String name, String residence, String password) throws RemoteException {
        if (bankController == null) {
            return null;
        }
        String sessionKey = bankController.login(name, residence, password);
        this.registerRemoteProperty("sessionTerminated" + sessionKey);
        return sessionKey;
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
        if (o instanceof BankController) {
            if (arg instanceof SessionTerminated) {
                SessionTerminated sessionTerminated = (SessionTerminated) arg;
                try {
                    remotePublisher.inform("sessionTerminated", sessionTerminated.getSessionKey(), null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            if (arg instanceof TransactionExecuted){

                try {
                    remotePublisher.inform("transactionExecuted", null, null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void registerRemoteProperty(String propertyName) throws RemoteException {
        if (remotePublisher != null) {
            remotePublisher.registerProperty(propertyName);
        }
    }
}
