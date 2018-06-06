package unittest.stubs;

import com.ark.BankConnectionInfo;
import com.ark.Transaction;
import com.ark.bank.ICentralBankConnection;

public class CentralBankConnectionStub implements ICentralBankConnection {

    @Override
    public boolean registerBank(BankConnectionInfo bankConnectionInfo) {
        return false;
    }

    @Override
    public boolean executeTransaction(Transaction transaction) {
        return false;
    }

    @Override
    public boolean isValidBankAccountNumber(String accountNumber) {
        return false;
    }
}