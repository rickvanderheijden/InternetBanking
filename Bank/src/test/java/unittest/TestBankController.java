package unittest;

import com.ark.bank.BankAccount;
import com.ark.bank.BankController;
import com.ark.bank.Customer;
import com.ark.centralbank.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class TestBankController {

    private final String BankIdInternal = "TEST";
    private final String BankIdExternal = "NONE";
    private final String accountFromExternal = BankIdExternal + "287493762";
    private final String accountToExternal   = BankIdExternal + "239475623";
    private String accountFromInternal;
    private String accountToInternal;
    private BankController bankController;

    @Before
    public void setUp() throws RemoteException {
        bankController = new BankController(BankIdInternal, null);
    }

    @After
    public void tearDown() {
        bankController = null;
    }


    //TODO: Test BankAccount variations. but where?

    @Test
    public void testCreateBankAccountOwnerNull() {
        BankAccount result = bankController.createBankAccount(null);
        assertNull(result);
    }

    @Test
    public void testCreateBankAccount() {
        Customer owner = new Customer("TestUser", "TestPassword", "TestResidence");
        BankAccount result = bankController.createBankAccount(owner);
        assertThat(result.getNumber(), startsWith(BankIdInternal));
        assertEquals(14, result.getNumber().length());
        assertEquals(100.0, result.getCreditLimit(), 0.0);
        assertEquals(owner, result.getOwner());
    }

    @Test
    public void testExecuteTransactionNull() {
        boolean result = bankController.executeTransaction(null);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAllValuesNull() {
        Transaction transaction = new Transaction(.00, null, null, null, null);
        boolean result = bankController.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDateNull() {
        Transaction transaction = new Transaction(22.95, "Description", "AccountFrom", "AccountTo", null);
        boolean result = bankController.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAccountToNull() {
        Transaction transaction = new Transaction(22.95, "Description", "AccountFrom", null, new Date());
        boolean result = bankController.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDescriptionNull() {
        Transaction transaction = new Transaction(22.95, null, "AccountFrom", "AccountTo", new Date());
        boolean result = bankController.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAmountNull() {
        Transaction transaction = new Transaction(0.0, "Description", "AccountFrom", "AccountTo", new Date());
        boolean result = bankController.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValues() {
        Transaction transaction = new Transaction(21.0, "This is a test transaction", "ABNA0123456789", "RABO0123456789", new Date());
        boolean result = bankController.executeTransaction(transaction);
        assertTrue(result);
    }
}
