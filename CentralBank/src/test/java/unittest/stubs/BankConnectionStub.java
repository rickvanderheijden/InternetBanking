package unittest.stubs;

import com.ark.BankConnectionInfo;
import com.ark.Transaction;
import com.ark.centralbank.IBankConnection;

import java.util.HashSet;
import java.util.Set;

public class BankConnectionStub implements IBankConnection {

    private final Set<String> bankIds = new HashSet<>();

    @Override
    public boolean executeTransaction(String bankId, Transaction transaction) {
        return (bankIds.contains(getBankId(transaction.getAccountFrom()))
            && bankIds.contains(getBankId(transaction.getAccountTo())));
    }

    @Override
    public boolean isValidBankAccountNumber(String bankId, String bankAccountNumber) {
        return true;
    }

    @Override
    public boolean registerBank(BankConnectionInfo bankConnectionInfo) {
        bankIds.add(bankConnectionInfo.getBankId());
        return true;
    }

    @Override
    public boolean unregisterBank(String bankId) {
        if (bankIds.contains(bankId)) {
            bankIds.remove(bankId);
            return true;
        }

        return false;
    }

    private String getBankId(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }

        return accountNumber.length() < 4 ? null : accountNumber.substring(0, 4);
    }

}
