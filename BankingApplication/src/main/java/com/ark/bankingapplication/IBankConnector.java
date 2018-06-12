package com.ark.bankingapplication;

import com.ark.BankTransaction;
import com.ark.Customer;
import com.ark.bank.IBankAccount;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public interface IBankConnector {
    boolean connect(String bankId) throws RemoteException, NotBoundException;
    String login(String name, String residence, String password) throws RemoteException;
    Customer createCustomer(String name, String residence, String password) throws RemoteException;
    IBankAccount createBankAccount(String sessionKey, Customer customer) throws RemoteException;
    List<BankTransaction> getTransactions(String sessionKey, String bankNumber) throws RemoteException;
    List<String> getBankAccountNumbers(String sessionKey) throws RemoteException;
    boolean executeTransaction(String sessionKey, BankTransaction bankTransaction) throws RemoteException;
    Customer getCustomer(String sessionKey, String name, String residence) throws RemoteException;
    IBankAccount getBankAccount(String sessionKey, String bankAccountNumber) throws RemoteException;
    boolean logout(String sessionKey) throws RemoteException;
    boolean setCreditLimit(String sessionKey, String bankAccountNumber, long limit) throws RemoteException;
    void subscribeToTransaction(String bankAccountNumber) throws RemoteException;
    void unsubscribeToTransaction(String bankAccountNumber) throws RemoteException;
}
