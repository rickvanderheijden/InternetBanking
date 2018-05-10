package componenttest;

import java.io.IOException;

import com.ark.bank.IBankForCentralBank;
import com.ark.centralbank.Transaction;
import org.junit.AfterClass;
import utilities.BankUtilities;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestBankForCentralBank {
    private static IBankForCentralBank bank;
    private static BankUtilities utilities;

    @BeforeClass
    public static void setUpClass() throws IOException {
        utilities = new BankUtilities();
        bank = utilities.startBankForCentralBank();
    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopBank();
    }

    @Test
    public void testExecuteTransactionNull() {
        boolean result = bank.executeTransaction(null);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAllValuesNull() {
        Transaction transaction = new Transaction(.00, null, null, null);
        boolean result = bank.executeTransaction(transaction);
        assertFalse(result);
    }

        @Test
        public void testExecuteTransactionAccountToNull() {
        Transaction transaction = new Transaction(22.95, "Description", "AccountFrom", null);
        boolean result = bank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDescriptionNull() {
        Transaction transaction = new Transaction(22.95, null, "AccountFrom", "AccountTo");
        boolean result = bank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAmountNull() {
        Transaction transaction = new Transaction(0.0, "Description", "AccountFrom", "AccountTo");
        boolean result = bank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValues() {
        Transaction transaction = new Transaction(21.0, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = bank.executeTransaction(transaction);
        assertTrue(result);
    }

    //TODO: Only test interface, not internals.
}
