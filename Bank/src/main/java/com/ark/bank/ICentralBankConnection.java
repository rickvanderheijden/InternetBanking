package com.ark.bank;

import com.ark.BankConnectionInfo;
import com.ark.Transaction;

public interface ICentralBankConnection {
    boolean registerBank(BankConnectionInfo bankConnectionInfo);
    boolean executeTransaction(Transaction transaction);
    boolean isValidBankAccountNumber(String accountNumber);
}
