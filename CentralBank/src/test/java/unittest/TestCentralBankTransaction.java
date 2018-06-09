package unittest;

import com.ark.BankConnectionInfo;
import com.ark.Transaction;
import com.ark.centralbank.*;
import org.junit.*;
import unittest.stubs.BankConnectionStub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Rick van der Heijden
 */
public class TestCentralBankTransaction {

    private static ICentralBankTransaction centralBank;
    private static final String BankAccountNumberABNA = "ABNA0123456789";
    private static final String BankAccountNumberRABO = "RABO0123456789";

    @Before
    public void setUp() {
        centralBank = new CentralBank(new BankConnectionStub());
    }

    @After
    public void tearDown() {
        centralBank = null;
    }

    @Test
    public void testExecuteTransactionNull() {
        boolean result = centralBank.executeTransaction(null);
        assertFalse(result);
    }


    @Test
    public void testExecuteTransactionValidValuesBothNotRegistered() {
        Transaction transaction = new Transaction(2100, "This is a test transaction", BankAccountNumberABNA, BankAccountNumberRABO);
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValuesAccountFromNotRegistered() {
        Transaction transaction = new Transaction(2100, "This is a test transaction", BankAccountNumberABNA, BankAccountNumberRABO);
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValuesAccountToNotRegistered() {
        ICentralBankRegister centralBankRegister = (ICentralBankRegister)centralBank;
        centralBankRegister.registerBank(new BankConnectionInfo("ABNA", ""));

        Transaction transaction = new Transaction(2100, "This is a test transaction", BankAccountNumberABNA, BankAccountNumberRABO);
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValuesBothRegistered() {
        ICentralBankRegister centralBankRegister = (ICentralBankRegister)centralBank;
        centralBankRegister.registerBank(new BankConnectionInfo("ABNA", "ABNAURL"));
        centralBankRegister.registerBank(new BankConnectionInfo("RABO", "RABOURL"));

        Transaction transaction = new Transaction(2100, "This is a test transaction", BankAccountNumberABNA, BankAccountNumberRABO);
        boolean result = centralBank.executeTransaction(transaction);
        assertTrue(result);
    }

    @Test
    public void testIsValidBankAccountNumber() {
        testIsValidBankAccountNumber(true);
    }

    @Test
    public void testIsValidBankAccountNumberNoBankConnection() {
        centralBank = new CentralBank(null);
        testIsValidBankAccountNumber(false);
    }

    private void testIsValidBankAccountNumber(boolean expectedResult) {
        boolean result = centralBank.isValidBankAccountNumber(BankAccountNumberABNA);
        assertEquals(expectedResult, result);
    }
}
