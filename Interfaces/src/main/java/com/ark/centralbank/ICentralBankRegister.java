package com.ark.centralbank;

import com.ark.BankConnectionInfo;

import javax.jws.WebService;

/**
 * @author Rick van der Heijden
 */
@WebService
public interface ICentralBankRegister {
    /**
     * Registers a bank to the central bank, if not already registered.
     * @param bankConnectionInfo Information needed to create the connection. Can not be null.
     * @return True if bank is registered correctly, false otherwise.
     */
    boolean registerBank(BankConnectionInfo bankConnectionInfo);

    /**
     * Unregisters a bank with the central bank, if registered.
     * @param bankId The bank id of the bank that needs to be inregistered. Should be valid and not be null or empty
     * @return True if bank is unregistered correctly, false otherwise.
     */
    boolean unregisterBank(String bankId);
}
