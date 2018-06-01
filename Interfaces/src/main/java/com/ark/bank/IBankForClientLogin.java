package com.ark.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Rick van der Heijden
 */
public interface IBankForClientLogin extends Remote {
    /**
     * Logs in the customer with the provided credentials.
     * @param name Name of the customer.
     * @param residence Residence of the customer.
     * @param password Password of the customer.
     * @return SessionKey if the login succeeds, null otherwise.
     * @throws RemoteException Thrown when remote method call fails.
     */
    String login(String name, String residence, String password) throws RemoteException;

    /**
     * Logs out the customer with the procided session key.
     * @param sessionKey The session key of the logged in user. Can not be null or empty and should be valid.
     * @return True if logout succeeds, false otherwise.
     * @throws RemoteException Thrown when remote method call fails.
     */
    boolean logout(String sessionKey) throws RemoteException;
}
