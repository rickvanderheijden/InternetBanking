package com.ark.centralbank;

import javax.xml.ws.Endpoint;

/**
 * @author Rick van der Heijden
 */
class CentralBankHost {
    
    private static String URL = "http://localhost:8080/CentralBank";
    
    public static void main(String[] args) {

        if ((args.length >= 1) && (args[0] != null) && (!args[0].isEmpty()))
        {
            String IPAddress = args[0];
            URL = "http://" + IPAddress + ":8080/CentralBank";

        }

        // Welcome message
        System.out.println("Central bank is running on URL: " + URL);

        // Create server
        IBankConnection bankConnection = new BankConnection();
        Endpoint.publish(URL, new CentralBank(bankConnection));
    }
}
