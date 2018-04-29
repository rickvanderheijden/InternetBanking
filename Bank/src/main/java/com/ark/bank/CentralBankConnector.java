package com.ark.bank;

import com.ark.centralbank.BankConnectionInfo;
import com.ark.centralbank.IBankForCentralBank;
import com.ark.centralbank.ICentralBankRegister;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import java.net.MalformedURLException;
import java.net.URL;

@WebService(serviceName = "BankService", portName = "BankPort")
public class CentralBankConnector implements IBankForCentralBank {
    private ICentralBankRegister centralBank = null;
    private String bankId;
    private String URLBase;
    private boolean registered = false;

    public CentralBankConnector() {
    }

    public CentralBankConnector(String bankId, String URLBase) {
        this.bankId = bankId;
        this.URLBase = URLBase;

        centralBank = getCentralBankConnection();
        createBankConnection();
        registerBank();
    }

    private boolean registerBank() {
        if (centralBank == null) {
            return false;
        }

        boolean result = false;
        BankConnectionInfo bankConnectionInfo = new BankConnectionInfo(bankId, URLBase + bankId);
        return centralBank.registerBank(bankConnectionInfo);
    }

    @Override
    public void doSomething() {
        registered = true;
    }

    private void createBankConnection() {
        Endpoint.publish(URLBase + bankId, this);
    }

    private ICentralBankRegister getCentralBankConnection() {
        URL wsdlURL;
        try {
            wsdlURL = new URL("http://localhost:8080/CentralBank?wsdl");
            QName qname = new QName("http://centralbank.ark.com/", "CentralBankService");
            Service service = Service.create(wsdlURL, qname);
            QName qnamePort = new QName("http://centralbank.ark.com/", "CentralBankPort");

            return service.getPort(qnamePort, ICentralBankRegister.class);
        } catch (MalformedURLException | WebServiceException e) {
            return null;
        }
    }
}
