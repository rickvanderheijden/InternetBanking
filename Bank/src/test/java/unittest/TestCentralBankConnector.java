package unittest;

import com.ark.BankTransaction;
import com.ark.bank.CentralBankConnector;
import org.junit.Before;
import org.junit.Test;
import unittest.stubs.BankControllerStub;

import static org.junit.Assert.assertEquals;

/**
 * @author Rick van der Heijden
 */
public class TestCentralBankConnector {

    private static final String BankId  = "BANK";
    private static final String BankAccountNumber = "BANK0123456789";
    private static final String URLBase = "http://localhost:1200/";
    private CentralBankConnector centralBankConnector;

    @Before
    public void setUp() {
        centralBankConnector = new CentralBankConnector(new BankControllerStub(), BankId, URLBase);
    }

    @Test
    public void testCentralBankConnectorExecuteTransaction() {
        testCentralBankConnectorExecuteTransaction(true);
    }

    @Test
    public void testCentralBankConnectorExecuteTransactionWithoutBankController() {
        centralBankConnector = new CentralBankConnector(null, BankId, URLBase);
        testCentralBankConnectorExecuteTransaction(false);
    }

    @Test
    public void testCentralBankConnectorIsValidBankAccountNumber() {
        testCentralBankConnectorIsValidBankAccountNumber(true);
    }

    @Test
    public void testCentralBankConnectorIsValidBankAccountNumberWithoutBankController() {
        centralBankConnector = new CentralBankConnector(null, BankId, URLBase);
        testCentralBankConnectorIsValidBankAccountNumber(false);
    }

    private void testCentralBankConnectorExecuteTransaction(boolean expectedResult) {
        BankTransaction bankTransaction = new BankTransaction(1000, "Description", BankAccountNumber, BankAccountNumber);
        boolean result = centralBankConnector.executeTransaction(bankTransaction);
        assertEquals(expectedResult, result);
    }
    private void testCentralBankConnectorIsValidBankAccountNumber(boolean expectedResult) {
        boolean result = centralBankConnector.isValidBankAccountNumber(BankAccountNumber);
        assertEquals(expectedResult, result);
    }
}
