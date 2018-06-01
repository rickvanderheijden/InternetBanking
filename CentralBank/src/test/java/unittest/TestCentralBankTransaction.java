package unittest;

import com.ark.BankConnectionInfo;
import com.ark.Transaction;
import com.ark.centralbank.*;
import com.ark.centralbank.ICentralBankRegister;
import com.ark.centralbank.ICentralBankTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        Transaction transaction = new Transaction(0, null, null, null);
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAccountToNull() {
        Transaction transaction = new Transaction(2295, "Description", "AccountFrom", null);
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDescriptionNull() {
        Transaction transaction = new Transaction(2295, null, "AccountFrom", "AccountTo");
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAmountNull() {
        Transaction transaction = new Transaction(0, "Description", "AccountFrom", "AccountTo");
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }




    //TODO: DO THESE IN INTEGRATION TEST
    @Test
    public void testExecuteTransactionValidValuesBothNotRegistered() {
        Transaction transaction = new Transaction(2100, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValuesAccountFromNotRegistered() {
        Transaction transaction = new Transaction(2100, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValuesAccountToNotRegistered() {
        ICentralBankRegister centralBankRegister = (ICentralBankRegister)centralBank;
        centralBankRegister.registerBank(new BankConnectionInfo("ABNA", ""));

        Transaction transaction = new Transaction(2100, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = centralBank.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValuesBothRegistered() {
        //TODO: CREATE STUBS? OR DO IN INTEGRATION
        ICentralBankRegister centralBankRegister = (ICentralBankRegister)centralBank;
        centralBankRegister.registerBank(new BankConnectionInfo("ABNA", ""));
        centralBankRegister.registerBank(new BankConnectionInfo("RABO", ""));

        Transaction transaction = new Transaction(2100, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = centralBank.executeTransaction(transaction);
        assertTrue(result);
    }
}
