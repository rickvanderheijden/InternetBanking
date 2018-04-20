package com.ark.centralbank;

import javax.jws.WebService;

@WebService
public interface ICentralBankRegister {
    boolean registerBank(BankConnectionInfo bankConnectionInfo);
    boolean unregisterBank(); 
}
