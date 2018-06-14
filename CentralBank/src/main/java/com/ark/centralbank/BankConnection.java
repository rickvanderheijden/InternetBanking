package com.ark.centralbank;

import com.ark.BankConnectionInfo;
import com.ark.BankTransaction;
import com.ark.bank.IBankForCentralBank;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BankConnection implements IBankConnection {

    private final Map<String, IBankForCentralBank> bankForCentralBanks = new HashMap<>();

    public BankConnection() {
    }

    @Override
    public boolean executeTransaction(String bankId, BankTransaction bankTransaction) {
        if (bankForCentralBanks.containsKey(bankId)) {
            return bankForCentralBanks.get(bankId).executeTransaction(bankTransaction);
        }

        return false;
    }

    @Override
    public boolean isValidBankAccountNumber(String bankId, String bankAccountNumber) {
        if (bankForCentralBanks.containsKey(bankId)) {
            return bankForCentralBanks.get(bankId).isValidBankAccountNumber(bankAccountNumber);
        }

        return false;
    }

    @Override
    public boolean registerBank(BankConnectionInfo bankConnectionInfo) {
        System.out.println("registerBank");
        IBankForCentralBank bankForCentralBank = getBankConnection(bankConnectionInfo.getURL());
        if (bankForCentralBank != null) {
            bankForCentralBanks.put(bankConnectionInfo.getBankId(), bankForCentralBank);
            System.out.println("Bank registered: " + bankConnectionInfo.getBankId());
            return true;
        }

        return false;
    }

    @Override
    public boolean unregisterBank(String bankId) {
        if (bankForCentralBanks.containsKey(bankId)) {
            bankForCentralBanks.remove(bankId);
            return true;
        }

        return false;
    }

    private IBankForCentralBank getBankConnection(String bankURL) {
        URL wsdlURL;
        try {
            wsdlURL = new URL(bankURL + "?wsdl");
        } catch (MalformedURLException e) {
            return null;
        }

        QName qname = new QName("http://bank.ark.com/", "BankService");
        Service service = Service.create(wsdlURL, qname);
        QName qnamePort = new QName("http://bank.ark.com/","BankPort");

        return service.getPort(qnamePort, IBankForCentralBank.class);
    }
}
