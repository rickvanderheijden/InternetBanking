package com.ark.bank;

import com.ark.centralbank.Transaction;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBankForClient extends Remote {
    BankAccount createBankAccount(Customer owner) throws RemoteException;
    Customer createCustomer(String name, String password, String residence) throws RemoteException;
    boolean executeTransaction(Transaction transaction) throws RemoteException;

    //TODO: Move to ILogin or ISession or something
    int login(String name, String residence, String password) throws RemoteException;
}
