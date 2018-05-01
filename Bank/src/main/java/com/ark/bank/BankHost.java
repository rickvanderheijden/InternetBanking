package com.ark.bank;

import java.rmi.RemoteException;

@SuppressWarnings("SpellCheckingInspection")
class BankHost {

    static private String BankId = "RABO";
    private static String URLBase = "http://localhost:1200/";
    private static BankController bankController;
    private static CentralBankConnector centralBankConnector;
    private static GUIConnector guiConnector;

    public static void main(String[] args) throws RemoteException {

        // Welcome message
        System.out.println("Bank is running");

        if ((args.length >= 2)
            && (args[0] != null) && (!args[0].isEmpty())
            && (args[1] != null) && (!args[1].isEmpty()))
        {
            BankId = args[0];
            URLBase = args[1];
        }

        bankController = new BankController(BankId);
        centralBankConnector = new CentralBankConnector(bankController, BankId, URLBase);
        guiConnector = new GUIConnector(bankController);
    }
}
