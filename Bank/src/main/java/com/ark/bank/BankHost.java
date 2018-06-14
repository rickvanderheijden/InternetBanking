package com.ark.bank;

import java.rmi.RemoteException;

/**
 * @author Rick van der Heijden
 */
@SuppressWarnings("unused")
class BankHost {

    private static String BankId = "RABO";
    private static String URLBase = "http://localhost:1200/";
    private static String CentralBankIpAddress = "localhost";

    public static void main(String[] args) throws RemoteException {

        if ((args.length >= 3)
            && (args[0] != null) && (!args[0].isEmpty())
            && (args[1] != null) && (!args[1].isEmpty())
            && (args[2] != null) && (!args[2].isEmpty()))
        {
            BankId = args[0];
            URLBase = args[1];
            CentralBankIpAddress = args[2];
        }

        // Welcome message
        System.out.println("Bank is running: " + BankId);
        System.out.println("URL Base: " + URLBase);
        System.out.println("CentralBankIpAddress: " + CentralBankIpAddress);

        ICentralBankConnection centralBankConnection = new CentralBankConnection(CentralBankIpAddress);
        //IDatabaseController databaseController = new DatabaseController(BankId);
        IDatabaseController databaseController = new DatabaseControllerStub();

        IBankController bankController = new BankController(BankId, centralBankConnection, databaseController);
        CentralBankConnector centralBankConnector = new CentralBankConnector(bankController, BankId, URLBase);
        GUIConnector guiConnector = new GUIConnector(bankController);
    }
}
