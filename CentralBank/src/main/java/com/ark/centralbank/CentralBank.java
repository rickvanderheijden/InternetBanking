package com.ark.centralbank;

import com.ark.bank.IBankForCentralBank;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@WebService
public class CentralBank implements ICentralBankRegister, ICentralBankTransaction {

    private final List<BankConnectionInfo> bankConnectionInfos = new ArrayList<>();

    public CentralBank() {
    }
    
    @Override
    public boolean registerBank(BankConnectionInfo bankConnectionInfo) {

        if (bankConnectionInfo == null
                || bankConnectionInfo.getBankId() == null
                || bankConnectionInfo.getURL() == null) {
            return false;
        }

        for (BankConnectionInfo info : bankConnectionInfos) {
            if (info.getBankId().equals(bankConnectionInfo.getBankId())
                    || info.getURL().equals(bankConnectionInfo.getURL())) {
                return false;
            }
        }

        bankConnectionInfos.add(bankConnectionInfo);
        return true;
    }

    @Override
    public boolean unregisterBank(String bankId) {
        for (BankConnectionInfo info : bankConnectionInfos) {
            if (info.getBankId().equals(bankId)) {
                bankConnectionInfos.remove(info);
                return true;
            }
        }

        return false;

    }

    @Override
    public boolean executeTransaction(Transaction transaction) {

        //TODO: Do we want to do the check here, or leave it to the bank itself?
        if ((transaction == null)
            || (transaction.getDate() == null)
            || (transaction.getAccountFrom() == null)
            || (transaction.getAccountTo() == null)
            || (!(transaction.getAmount() <= 0.0))
            || (transaction.getDate() == null)
            || (transaction.getDescription() == null)) {
            return false;
        }

        String bankIdFrom = getBankId(transaction.getAccountFrom());
        String bankIdTo = getBankId(transaction.getAccountTo());

        if (!isBankRegistered(bankIdFrom) || !isBankRegistered(bankIdTo)) {
            return false;
        }

        IBankForCentralBank bankForCentralBankTo = getBankConnection(getBankURL(bankIdTo));

        if (bankForCentralBankTo == null) {
            return false;
        }

        return bankForCentralBankTo.executeTransaction(transaction);
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

    private String getBankId(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }

        return accountNumber.length() < 4 ? null : accountNumber.substring(0, 4);
    }

    private String getBankURL(String bankId) {
        if ((bankId == null) || bankId.isEmpty()) {
            return null;
        }

        for (BankConnectionInfo bankConnectionInfo : bankConnectionInfos) {
            if (bankConnectionInfo.getBankId().equals(bankId)) {
                return bankConnectionInfo.getURL();
            }
        }

        return null;
    }

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
