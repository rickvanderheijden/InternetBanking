package unittest.stubs;

import com.ark.BankConnectionInfo;
import com.ark.BankTransaction;
import com.ark.bank.ICentralBankConnection;

/**
 * @author Rick van der Heijden
 */
public class CentralBankConnectionStub implements ICentralBankConnection {

    @Override
    public boolean registerBank(BankConnectionInfo bankConnectionInfo) {
        return true;
    }

    @Override
    public boolean executeTransaction(BankTransaction bankTransaction) {
        return true;
    }

    @Override
    public boolean isValidBankAccountNumber(String accountNumber) {
        return true;
    }
}