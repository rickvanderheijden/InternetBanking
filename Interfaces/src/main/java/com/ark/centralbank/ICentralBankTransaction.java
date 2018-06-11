package com.ark.centralbank;

import com.ark.BankTransaction;

import javax.jws.WebService;

/**
 * @author Rick van der Heijden
 */
@WebService
public interface ICentralBankTransaction {
    /**
     * Executes a bankTransaction from one bank to another.
     * @param bankTransaction This is the bankTransaction which holds the amount and banknumbers. Should have valid entries and can not be null.
     * @return True is the bankTransaction can be executed succesfully (on both receiving and sending ends), false otherwise.
     */
    boolean executeTransaction(BankTransaction bankTransaction);

    /**
     * Checks if a bank account number is valid; it will check the existence of the bank account on the bank.
     * @param bankAccountNumber This is the bank account number. Can not be null or empty.
     * @return True if the bank account exists, false otherwise.
     */
    boolean isValidBankAccountNumber(String bankAccountNumber);
}
