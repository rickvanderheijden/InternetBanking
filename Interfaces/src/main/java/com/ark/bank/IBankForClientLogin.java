package com.ark.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBankForClientLogin extends Remote {

    //TODO: Change class diagram or change code to be like class diagram
    String login(String name, String residence, String password) throws RemoteException;
    boolean logout(String sessionKey) throws RemoteException;
}
