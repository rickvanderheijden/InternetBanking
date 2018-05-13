package com.ark.centralbank;

import javax.jws.WebService;

/**
 * @author Rick van der Heijden
 */
@WebService
public interface ICentralBankRegister {
    boolean registerBank(BankConnectionInfo bankConnectionInfo);
    boolean unregisterBank(String bankId);
}
