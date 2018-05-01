package com.ark.bank;

import com.ark.centralbank.Transaction;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBankForClient extends Remote {
    BankAccount createBankAccount(Customer owner) throws RemoteException;
    boolean executeTransaction(Transaction transaction) throws RemoteException;
}
