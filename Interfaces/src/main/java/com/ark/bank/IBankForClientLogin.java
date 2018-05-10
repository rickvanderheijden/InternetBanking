package com.ark.bank;

import java.rmi.RemoteException;

public interface IBankForClientLogin {
    String login(String name, String residence, String password) throws RemoteException;
}
