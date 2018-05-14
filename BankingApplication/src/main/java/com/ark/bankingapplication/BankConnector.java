package com.ark.bankingapplication;

import com.ark.bank.IBankForClientSession;
import fontyspublisher.IRemotePublisherForListener;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BankConnector {

    private String sessionKey;

    public IBankForClientSession getBankConnection(String bankId) throws IOException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IRemotePublisherForListener remotePublisher = (IRemotePublisherForListener) registry.lookup("bankPublisher" + bankId);
        //remotePublisher.subscribeRemoteListener(this, "");
        return (IBankForClientSession) registry.lookup("bank" + bankId);
    }
}
