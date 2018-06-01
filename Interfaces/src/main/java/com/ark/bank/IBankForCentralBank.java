package com.ark.bank;

import com.ark.centralbank.Transaction;

import javax.jws.WebService;

/**
 * @author Rick van der Heijden
 */
@WebService
public interface IBankForCentralBank {
    /**
     * Executes a transaction from one bank to another.
     * @param transaction This is the transaction which holds the amount and banknumbers. Should have valid entries and can not be null.
     * @return True is the transaction can be executed succesfully (on both receiving and sending ends), false otherwise.
     */
    boolean executeTransaction(Transaction transaction);

    /**
     * Checks if a bank account number is valid; it will check the existence of the bank account on the bank.
     * @param bankAccountNumber This is the bank account number. Can not be null or empty.
     * @return True if the bank account exists, false otherwise.
     */
    boolean isValidBankAccountNumber(String bankAccountNumber);
}
