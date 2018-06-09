package com.ark.centralbank;

import com.ark.*;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rick van der Heijden
 */
@WebService
public class CentralBank implements ICentralBankRegister, ICentralBankTransaction {

    private final List<BankConnectionInfo> bankConnectionInfos = new ArrayList<>();
    private IBankConnection bankConnection;

    public CentralBank() {
    }

    public CentralBank(IBankConnection bankConnection) {
        this.bankConnection = bankConnection;
    }
    
    @Override
    public boolean registerBank(BankConnectionInfo bankConnectionInfo) {
        if ((bankConnection == null)
                || (bankConnectionInfo == null)
                || (bankConnectionInfo.getBankId() == null)
                || (bankConnectionInfo.getURL() == null)) {
            return false;
        }

        for (BankConnectionInfo info : bankConnectionInfos) {
            if (info.getBankId().equals(bankConnectionInfo.getBankId())
                    || info.getURL().equals(bankConnectionInfo.getURL())) {
                return false;
            }
        }

        if (bankConnection.registerBank(bankConnectionInfo)) {
            bankConnectionInfos.add(bankConnectionInfo);
            return true;
        }

        return false;
    }

    @Override
    public boolean unregisterBank(String bankId) {
        if (bankConnection == null) {
            return false;
        }

        return bankConnection.unregisterBank(bankId);
    }

    @Override
    public boolean executeTransaction(Transaction transaction) {
        if ((bankConnection == null) || (transaction == null)) {
            return false;
        }

        String bankIdFrom = getBankId(transaction.getAccountFrom());
        String bankIdTo = getBankId(transaction.getAccountTo());

        if (!isBankRegistered(bankIdFrom) && !isBankRegistered(bankIdTo)) {
            return false;
        }

        return bankConnection.executeTransaction(bankIdTo, transaction);
    }

    @Override
    public boolean isValidBankAccountNumber(String bankAccountNumber) {
        if (bankConnection == null) {
            return false;
        }

        return bankConnection.isValidBankAccountNumber(getBankId(bankAccountNumber), bankAccountNumber);
    }

    private String getBankId(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }

        return accountNumber.length() < 4 ? null : accountNumber.substring(0, 4);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isBankRegistered(String bankId) {
        if ((bankId == null) || bankId.isEmpty()) {
            return false;
        }

        for (BankConnectionInfo bankConnectionInfo : bankConnectionInfos) {
            if (bankConnectionInfo.getBankId().equals(bankId)) {
                return true;
            }
        }

        return false;
    }
}
