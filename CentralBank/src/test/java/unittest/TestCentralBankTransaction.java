package unittest;

import com.ark.centralbank.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Rick van der Heijden
 */
public class TestCentralBankTransaction {

    private static ICentralBankTransaction centralBank;

    @BeforeClass
    public static void setUpClass() {
        centralBank = new CentralBank();
    }

    @AfterClass
    public static void tearDownClass() {
        centralBank = null;
    }

    @Test
    public void testExecuteTransactionNull() {
        boolean result = centralBank.executeTransaction(null);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAllValuesNull() {
        Transaction transaction = new Transaction(.00, null, null, null);
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAccountToNull() {
        Transaction transaction = new Transaction(22.95, "Description", "AccountFrom", null);
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDescriptionNull() {
        Transaction transaction = new Transaction(22.95, null, "AccountFrom", "AccountTo");
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAmountNull() {
        Transaction transaction = new Transaction(0.0, "Description", "AccountFrom", "AccountTo");
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }




    //TODO: DO THESE IN INTEGRATION TEST
    @Test
    public void testExecuteTransactionValidValuesBothNotRegistered() {
        Transaction transaction = new Transaction(21.0, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValuesAccountFromNotRegistered() {
        Transaction transaction = new Transaction(21.0, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValuesAccountToNotRegistered() {
        ICentralBankRegister centralBankRegister = (ICentralBankRegister)centralBank;
        centralBankRegister.registerBank(new BankConnectionInfo("ABNA", ""));

        Transaction transaction = new Transaction(21.0, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValuesBothRegistered() {
        ICentralBankRegister centralBankRegister = (ICentralBankRegister)centralBank;
        centralBankRegister.registerBank(new BankConnectionInfo("ABNA", ""));
        centralBankRegister.registerBank(new BankConnectionInfo("RABO", ""));

        Transaction transaction = new Transaction(21.0, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = centralBank.executeTransaction(transaction);
        assertTrue(result);
    }
}
