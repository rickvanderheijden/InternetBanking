package com.ark.bank;

import com.ark.centralbank.BankConnectionInfo;
import com.ark.centralbank.ICentralBankRegister;
import com.ark.centralbank.Transaction;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Rick van der Heijden
 */
@WebService(serviceName = "BankService", portName = "BankPort")
public class CentralBankConnector implements IBankForCentralBank {
    private ICentralBankRegister centralBank = null;
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

            centralBank = getCentralBankConnection();
            createBankConnection();
            registerBank();
        }
    }

    private boolean registerBank() {
        if (centralBank == null) {
            return false;
        }

        BankConnectionInfo bankConnectionInfo = new BankConnectionInfo(bankId, URLBase + bankId);
        return centralBank.registerBank(bankConnectionInfo);
    }

    @Override
    public boolean executeTransaction(Transaction transaction) {
        if (bankController == null) {
            return false;
        }

        //TODO: HOW TO DO THIS FOR BANK??
        return bankController.executeTransaction(transaction);
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
