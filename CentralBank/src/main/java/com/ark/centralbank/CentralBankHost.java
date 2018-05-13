package com.ark.centralbank;

import javax.xml.ws.Endpoint;

/**
 * @author Rick van der Heijden
 */
class CentralBankHost {
    
    private static final String URL = "http://localhost:8080/CentralBank";
    
    public static void main(String[] args) {
        
        // Welcome message
        System.out.println("Central bank is running");

        // Create server
        Endpoint.publish(URL, new CentralBank());
    }
}
