package componenttest;

import com.ark.centralbank.ICentralBankTransaction;
import com.ark.centralbank.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utilities.CentralBankUtilities;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertFalse;

public class TestCentralBankTransaction {
    private static ICentralBankTransaction centralBank;
    private static CentralBankUtilities utilities;

    @BeforeClass
    public static void setUpClass() throws IOException {
        utilities = new CentralBankUtilities();
        centralBank = utilities.startCentralBankTransaction();
    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopCentralBank();
    }

    @Test
    public void testExecuteTransactionNull() {
        boolean result = centralBank.executeTransaction(null);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAllValuesNull() {
        Transaction transaction = new Transaction(.00, null, null, null, null);
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDateNull() {
        Transaction transaction = new Transaction(22.95, "Description", "AccountFrom", "AccountTo", null);
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAccountToNull() {
        Transaction transaction = new Transaction(22.95, "Description", "AccountFrom", null, new Date());
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDescriptionNull() {
        Transaction transaction = new Transaction(22.95, null, "AccountFrom", "AccountTo", new Date());
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAmountNull() {
        Transaction transaction = new Transaction(0.0, "Description", "AccountFrom", "AccountTo", new Date());
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValues() {
        Transaction transaction = new Transaction(21.0, "This is a test transaction", "ABNA0123456789", "RABO0123456789", new Date());
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);

        //TODO: Make stub etc
    }

}
