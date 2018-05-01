package com.ark.bank;

import com.ark.centralbank.Transaction;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("SpellCheckingInspection")
public class BankController {
    private final long startBankAccountNumber = 1000000000L;
    private final long endBankAccountNumber = 9999999999L;
    private final GUIConnector guiConnector;
    private String bankId;
    private final Random random = new Random();
    private final Set<BankAccount> bankAccounts = new HashSet<>();

    public BankController(String bankId, String URLBase) throws RemoteException {
        this.bankId = bankId;
        CentralBankConnector centralBankConnector = new CentralBankConnector(this, bankId, URLBase);
        guiConnector = new GUIConnector(this);
    }

    public boolean executeTransaction(Transaction transaction) {
        return ((transaction != null)
                && (transaction.getDate() != null)
                && (transaction.getAccountFrom() != null)
                && (transaction.getAccountTo() != null)
                && (!(transaction.getAmount() <= 0.0))
                && (transaction.getDate() != null)
                && (transaction.getDescription() != null));
    }

    public BankAccount createBankAccount(Customer owner) {

        if (owner == null) {
            return null;
        }

        BankAccount bankAccount = new BankAccount(owner, bankId);
        bankAccounts.add(bankAccount);
        return bankAccount;
    }

    private String getRandomBankAccountNumber() {
        long range = endBankAccountNumber - startBankAccountNumber + 1;
        long fraction = (long)(range * random.nextDouble());
        long randomNumber = fraction + startBankAccountNumber;

        return Long.toString(randomNumber);
    }

    //TODO: Use BankAccoutnt object instead of Strings
}