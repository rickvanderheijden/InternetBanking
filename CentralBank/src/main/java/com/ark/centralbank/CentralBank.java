package com.ark.centralbank;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@WebService
public class CentralBank implements ICentralBankRegister {

    private final List<BankConnectionInfo> bankConnectionInfos = new ArrayList<>();

    public CentralBank() {
        super();
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
    public boolean unregisterBank() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
