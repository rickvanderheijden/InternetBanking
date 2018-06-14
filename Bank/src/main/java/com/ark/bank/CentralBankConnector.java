package com.ark.bank;

import com.ark.BankConnectionInfo;
import com.ark.BankTransaction;
import com.sun.xml.internal.ws.server.ServerRtException;

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

            if (createBankConnection()) {
                registerBank();
            }
        }
    }

    private void registerBank() {
        if (bankController != null) {

            BankConnectionInfo bankConnectionInfo = new BankConnectionInfo(bankId, URLBase + bankId);
            boolean result = bankController.registerBank(bankConnectionInfo);
            System.out.println("Register bank " + (result ? "passed" : "failed"));
        }
    }

    @Override
    public boolean executeTransaction(BankTransaction bankTransaction) {
        if (bankController == null) {
            return false;
        }

        return bankController.executeTransaction(bankTransaction);
    }

    @Override
    public boolean isValidBankAccountNumber(String bankAccountNumber) {
        if (bankController == null) {
            return false;
        }

        return bankController.isValidBankAccountNumber(bankAccountNumber);
    }

    private boolean createBankConnection() {
        System.out.println("createBankConnection");
        try {
            Endpoint.publish(URLBase + bankId, this);
        } catch (IllegalArgumentException | ServerRtException e) {
            System.out.println("createBankConnection failed. URLBase: " + URLBase + ", bankId: " + bankId);
            return false;
        }

        return true;

    }
}
