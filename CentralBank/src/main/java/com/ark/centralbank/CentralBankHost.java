package com.ark.centralbank;

import javax.xml.ws.Endpoint;

/**
 * @author Rick van der Heijden
 */
@SuppressWarnings("FieldCanBeLocal")
class CentralBankHost {
    
    private static String URL = "http://localhost:8080/CentralBank";
    
    public static void main(String[] args) {

        if ((args.length >= 1) && (args[0] != null) && (!args[0].isEmpty()))
        {
            String IPAddress = args[0];
            URL = "http://" + IPAddress + ":8080/CentralBank";
        } else {
            System.out.println("Not enough parameters used.\n");
            System.out.println("Usage:    CentralBank.jar \"IP address\"");
            System.out.println("Example:  CentralBank.jar \"192.168.0.10\"");
            return;
        }


        // Welcome message
        System.out.println("Central bank is running on URL: " + URL);

        // Create server
        IBankConnection bankConnection = new BankConnection();
        Endpoint.publish(URL, new CentralBank(bankConnection));
    }
}
