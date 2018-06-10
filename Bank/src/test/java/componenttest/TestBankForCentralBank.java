package componenttest;

import com.ark.BankTransaction;
import com.ark.bank.IBankForCentralBank;
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
        BankTransaction bankTransaction = new BankTransaction();
        boolean result = bank.executeTransaction(bankTransaction);
        assertFalse(result);
    }
}
