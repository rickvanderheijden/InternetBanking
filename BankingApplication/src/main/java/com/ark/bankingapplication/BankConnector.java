package com.ark.bankingapplication;

import com.ark.bank.IBankForClientLogin;
import com.ark.bank.IBankForClientSession;
import fontyspublisher.IRemotePublisherForListener;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BankConnector {

    private String sessionKey;
    protected IBankForClientLogin bankLogin = null;

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public IBankForClientLogin getBankLogin() {
        return bankLogin;
    }

    public IBankForClientSession getBankConnection(String bankId) throws IOException, NotBoundException, RemoteException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IRemotePublisherForListener remotePublisher = (IRemotePublisherForListener) registry.lookup("bankPublisher" + bankId);
        //remotePublisher.subscribeRemoteListener(this, "");
        this.bankLogin = (IBankForClientLogin) registry.lookup("bank" + bankId);
        return (IBankForClientSession) registry.lookup("bank" + bankId);
    }

    public String Login(String name, String residence, String password) throws RemoteException {
        try {
            this.sessionKey = this.bankLogin.login(name, residence, password);
            return this.sessionKey;
        } catch (RemoteException ex) {
            System.out.println("er is iets fout gegaan");
            return null;
        }

    }
}
