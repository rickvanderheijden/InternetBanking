package com.ark.bank;

import java.rmi.RemoteException;

/**
 * @author Rick van der Heijden
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
class BankHost {

    private static String BankId;
    private static String URLBase;
    private static String CentralBankIpAddress;

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
        else
        {
            System.out.println("Not enough parameters used.\n");
            System.out.println("Usage:    Bank.jar \"BankID\" \"IP address and port\" \"Central Bank IP Address\"");
            System.out.println("Example:  Bank.jar \"ABNA\" \"http://192.168.0.10:1200/\" \"192.168.0.190\"");
            return;
        }

        // Welcome message
        System.out.println("Bank is running: " + BankId);
        System.out.println("URL Base: " + URLBase);
        System.out.println("CentralBankIpAddress: " + CentralBankIpAddress);

        ICentralBankConnection centralBankConnection = new CentralBankConnection(CentralBankIpAddress);
        IDatabaseController databaseController = new DatabaseController(BankId);

        IBankController bankController = new BankController(BankId, centralBankConnection, databaseController);
        CentralBankConnector centralBankConnector = new CentralBankConnector(bankController, BankId, URLBase);
        GUIConnector guiConnector = new GUIConnector(bankController);
    }
}
