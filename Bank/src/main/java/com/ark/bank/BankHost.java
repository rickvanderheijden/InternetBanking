package com.ark.bank;

import org.hibernate.service.spi.ServiceException;

import java.rmi.RemoteException;

/**
 * @author Rick van der Heijden
 */
@SuppressWarnings("unused")
class BankHost {

    private static String BankId = "RABO";
    private static String URLBase = "http://localhost:1200/";

    public static void main(String[] args) throws RemoteException, ServiceException {

        if ((args.length >= 2)
            && (args[0] != null) && (!args[0].isEmpty())
            && (args[1] != null) && (!args[1].isEmpty()))
        {
            BankId = args[0];
            URLBase = args[1];
        }

        // Welcome message
        System.out.println("Bank is running: " + BankId);

        ICentralBankConnection centralBankConnection = new CentralBankConnection();
        IBankController bankController = new BankController(BankId, centralBankConnection);
        CentralBankConnector centralBankConnector = new CentralBankConnector(bankController, BankId, URLBase);
        GUIConnector guiConnector = new GUIConnector(bankController);
        bankController.connectToBankDatabase(BankId);
    }
}
