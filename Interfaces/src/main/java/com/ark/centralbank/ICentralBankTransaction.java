package com.ark.centralbank;

import javax.jws.WebService;

/**
 * @author Rick van der Heijden
 */
@WebService
public interface ICentralBankTransaction {
    boolean executeTransaction(Transaction transaction);
    boolean isValidBankAccountNumber(String bankAccountNumber);
}
