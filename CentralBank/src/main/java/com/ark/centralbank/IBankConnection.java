package com.ark.centralbank;

import com.ark.BankConnectionInfo;
import com.ark.Transaction;

public interface IBankConnection {

    boolean executeTransaction(String bankId, Transaction transaction);
    boolean isValidBankAccountNumber(String bankId, String bankAccountNumber);
    boolean registerBank(BankConnectionInfo bankConnectionInfo);
    boolean unregisterBank(String bankId);
}
