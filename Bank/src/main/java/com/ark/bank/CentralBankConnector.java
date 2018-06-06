package com.ark.bank;

import com.ark.BankConnectionInfo;
import com.ark.Transaction;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * @author Rick van der Heijden
 */
@SuppressWarnings("WeakerAccess")
@WebService(serviceName = "BankService", portName = "BankPort")
public class CentralBankConnector implements IBankForCentralBank {
    private IBankController bankController;
    private String bankId;
    private String URLBase;

    public CentralBankConnector() {
    }

    public CentralBankConnector(IBankController bankController, String bankId, String URLBase) {
        this.bankController = bankController;
        this.bankId = bankId;
        this.URLBase = URLBase;

        if ((URLBase != null) && (!URLBase.isEmpty())
                && (bankId) != null && (!bankId.isEmpty())) {

            createBankConnection();
            registerBank();
        }
    }

    private void registerBank() {
        if (bankController != null) {

            BankConnectionInfo bankConnectionInfo = new BankConnectionInfo(bankId, URLBase + bankId);
            bankController.registerBank(bankConnectionInfo);
        }
    }

    @Override
    public boolean executeTransaction(Transaction transaction) {
        if (bankController == null) {
            return false;
        }

        return bankController.executeTransaction(transaction);
    }

    @Override
    public boolean isValidBankAccountNumber(String bankAccountNumber) {
        if (bankController == null) {
            return false;
        }

        return bankController.isValidBankAccountNumber(bankAccountNumber);
    }

    private void createBankConnection() {
        Endpoint.publish(URLBase + bankId, this);
    }
}
