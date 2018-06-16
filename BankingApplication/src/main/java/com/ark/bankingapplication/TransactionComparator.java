package com.ark.bankingapplication;

import com.ark.BankTransaction;

import java.util.Comparator;

class TransactionComparator implements Comparator<BankTransaction> {
    @Override
    public int compare(BankTransaction bankTransaction1, BankTransaction bankTransaction2) {
        return bankTransaction1.getDate().compareTo(bankTransaction2.getDate());
    }
}
