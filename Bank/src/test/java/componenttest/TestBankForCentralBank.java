package componenttest;

import com.ark.bank.IBankForCentralBank;
import com.ark.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import testutilities.BankUtilities;

import java.io.IOException;

import static org.junit.Assert.assertFalse;

/**
 * @author Rick van der Heijden
 */
public class TestBankForCentralBank {
    private static final String BankId = "RABO";
    private static final String URLBase = "http://localhost:1200/";
    private static IBankForCentralBank bank;
    private static BankUtilities utilities;

    @BeforeClass
    public static void setUpClass() throws IOException {
        utilities = new BankUtilities();
        utilities.startBank(BankId, URLBase);
        bank = utilities.getIBankForCentralBank(BankId, URLBase);
    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopBank(BankId);
    }

    @Test
    public void testExecuteTransactionNull() {
        boolean result = bank.executeTransaction(null);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAllValuesNull() {
        Transaction transaction = new Transaction(0, null, null, null);
        boolean result = bank.executeTransaction(transaction);
        assertFalse(result);
    }

        @Test
        public void testExecuteTransactionAccountToNull() {
        Transaction transaction = new Transaction(2295, "Description", "AccountFrom", null);
        boolean result = bank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDescriptionNull() {
        Transaction transaction = new Transaction(2295, null, "AccountFrom", "AccountTo");
        boolean result = bank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAmountNull() {
        Transaction transaction = new Transaction(0, "Description", "AccountFrom", "AccountTo");
        boolean result = bank.executeTransaction(transaction);
        assertFalse(result);
    }

    //TODO: Only test interface, not internals.
}
