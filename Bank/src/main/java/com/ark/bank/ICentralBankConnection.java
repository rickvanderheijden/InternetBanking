package com.ark.bank;

import com.ark.BankConnectionInfo;
import com.ark.BankTransaction;

public interface ICentralBankConnection {
    boolean registerBank(BankConnectionInfo bankConnectionInfo);
    boolean executeTransaction(BankTransaction bankTransaction);
    boolean isValidBankAccountNumber(String accountNumber);
}
