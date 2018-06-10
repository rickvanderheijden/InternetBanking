package com.ark.centralbank;

import com.ark.BankConnectionInfo;
import com.ark.BankTransaction;

public interface IBankConnection {

    boolean executeTransaction(String bankId, BankTransaction bankTransaction);
    boolean isValidBankAccountNumber(String bankId, String bankAccountNumber);
    boolean registerBank(BankConnectionInfo bankConnectionInfo);
    boolean unregisterBank(String bankId);
}
