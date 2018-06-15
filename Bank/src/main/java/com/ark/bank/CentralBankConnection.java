package com.ark.bank;

import com.ark.BankConnectionInfo;
import com.ark.BankTransaction;
import com.ark.centralbank.ICentralBankRegister;
import com.ark.centralbank.ICentralBankTransaction;

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
    private final String centralBankIpAddress;

    public CentralBankConnection(String centralBankIpAddress)  {
        this.centralBankIpAddress = centralBankIpAddress;
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
    public boolean executeTransaction(BankTransaction bankTransaction) {
        if (centralBankTransaction == null) {
            return false;
        }

        return centralBankTransaction.executeTransaction(bankTransaction);
    }

    @Override
    public boolean registerBank(BankConnectionInfo bankConnectionInfo) {
        System.out.println("registerBank");
        if (centralBankRegister == null) {
            System.out.println("registerBank failed");
            return false;
        }

        return centralBankRegister.registerBank(bankConnectionInfo);
    }

    private boolean createCentralBankConnection() {
        System.out.println("createCentralBankConnection");
        URL wsdlURL;
        try {
            wsdlURL = new URL("http://" + centralBankIpAddress + ":8080/CentralBank?wsdl");
//            wsdlURL = new URL("http://localhost:8080/CentralBank?wsdl");
            QName qname = new QName("http://centralbank.ark.com/", "CentralBankService");
            service = Service.create(wsdlURL, qname);
            qnamePort = new QName("http://centralbank.ark.com/", "CentralBankPort");
            return true;
        } catch (MalformedURLException | WebServiceException e) {
            System.out.println("Error creating Central Bank Connection");
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
