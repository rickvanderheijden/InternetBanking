package com.ark.bank;

/**
 * @author Rick van der Heijden
 */
class TransactionExecuted {
    private final String bankAccount;
    public TransactionExecuted(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount() {
        return bankAccount;
    }
}
