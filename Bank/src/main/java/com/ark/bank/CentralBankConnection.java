package com.ark.bank;

import com.ark.BankConnectionInfo;
import com.ark.centralbank.ICentralBankRegister;
import com.ark.centralbank.ICentralBankTransaction;
import com.ark.Transaction;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import java.net.MalformedURLException;
import java.net.URL;

public class CentralBankConnection implements ICentralBankConnection {

    private final ICentralBankTransaction centralBankTransaction;
    private final ICentralBankRegister centralBankRegister;
    private Service service;
    private QName qnamePort;

    public CentralBankConnection()  {
        this.centralBankTransaction = getCentralBankTransaction();
        this.centralBankRegister = getCentralBankRegister();
    }

    @Override
    public boolean isValidBankAccountNumber(String accountNumber) {
        if (centralBankTransaction == null) {
            return false;
        }

        return centralBankTransaction.isValidBankAccountNumber(accountNumber);
    }

    @Override
    public boolean executeTransaction(Transaction transaction) {
        if (centralBankTransaction == null) {
            return false;
        }

        return centralBankTransaction.executeTransaction(transaction);
    }

    @Override
    public boolean registerBank(BankConnectionInfo bankConnectionInfo) {
        if (centralBankRegister == null) {
            return false;
        }

        return centralBankRegister.registerBank(bankConnectionInfo);
    }

    private boolean createCentralBankConnection() {
        URL wsdlURL;
        try {
            wsdlURL = new URL("http://localhost:8080/CentralBank?wsdl");
            QName qname = new QName("http://centralbank.ark.com/", "CentralBankService");
            service = Service.create(wsdlURL, qname);
            qnamePort = new QName("http://centralbank.ark.com/", "CentralBankPort");
            return true;
        } catch (MalformedURLException | WebServiceException e) {
            return false;
        }
    }

    private ICentralBankTransaction getCentralBankTransaction() {
        if (createCentralBankConnection()) {
            return service.getPort(qnamePort, ICentralBankTransaction.class);
        }

        return null;
    }

    private ICentralBankRegister getCentralBankRegister() {
        if (createCentralBankConnection()) {
            return service.getPort(qnamePort, ICentralBankRegister.class);
        }

        return null;
    }
}
