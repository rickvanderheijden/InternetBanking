package com.ark.bank;

import java.rmi.RemoteException;

public interface IBankForClientLogin {
    int login(String name, String residence, String password) throws RemoteException;
}
