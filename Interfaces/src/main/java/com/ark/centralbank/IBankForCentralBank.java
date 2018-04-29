package com.ark.centralbank;

import javax.jws.WebService;

@WebService
public interface IBankForCentralBank {
    boolean executeTransaction(Transaction transaction);
    void doSomething();
}
