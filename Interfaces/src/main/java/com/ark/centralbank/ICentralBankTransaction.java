package com.ark.centralbank;

import javax.jws.WebService;

@WebService
public interface ICentralBankTransaction {
    boolean executeTransaction();
}