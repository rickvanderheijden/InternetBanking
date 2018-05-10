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
    public void setUp() {
        bankController = new BankController(BankIdInternal);
    }

    @After
    public void tearDown() {
        bankController = null;
    }


    //TODO: Test BankAccount variations. but where?
    //TODO: Need stubs to check for scenario's (other banks, etc)

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
        Transaction transaction = new Transaction(.00, null, null, null);
        boolean result = bankController.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAccountToNull() {
        Transaction transaction = new Transaction(22.95, "Description", "AccountFrom", null);
        boolean result = bankController.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDescriptionNull() {
        Transaction transaction = new Transaction(22.95, null, "AccountFrom", "AccountTo");
        boolean result = bankController.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAmountNull() {
        Transaction transaction = new Transaction(0.0, "Description", "AccountFrom", "AccountTo");
        boolean result = bankController.executeTransaction(transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValues() {
        Transaction transaction = new Transaction(21.0, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = bankController.executeTransaction(transaction);
        assertTrue(result);
    }

    @Test
    public void testCreateCustomerAllValuesNull() {
        Customer result = bankController.createCustomer(null, null, null);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerAllValuesEmpty() {
        Customer result = bankController.createCustomer("", "", "");
        assertNull(result);
    }

    @Test
    public void testCreateCustomerNameNull() {
        Customer result = bankController.createCustomer(null, "Password", "Residence");
        assertNull(result);
    }

    @Test
    public void testCreateCustomerNameEmpty() {
        Customer result = bankController.createCustomer("", "Residence", "Password");
        assertNull(result);
    }

    @Test
    public void testCreateCustomerPasswordNull() {
        Customer result = bankController.createCustomer("Name", "Residence", null);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerPasswordEmpty() {
        Customer result = bankController.createCustomer("Name", "Residence", "");
        assertNull(result);
    }

    @Test
    public void testCreateCustomerResidenceNull() {
        Customer result = bankController.createCustomer("Name", null, "Password");
        assertNull(result);
    }

    @Test
    public void testCreateCustomerResidenceEmpty() {
        Customer result = bankController.createCustomer("Name", "", "Password");
        assertNull(result);
    }

    @Test
    public void testCreateCustomerValidValues() {
        String name = "Name";
        String password = "Password";
        String residence = "Residence";
        Customer result = bankController.createCustomer(name, residence, password);
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(residence, result.getResidence());
        assertTrue(result.isPasswordValid(password));
    }

    @Test
    public void testCreateCustomerAlreadyExists() {
        String name = "Name";
        String password = "Password";
        String residence = "Residence";
        Customer result = bankController.createCustomer(name, residence, password);
        assertNotNull(result);
        result = bankController.createCustomer(name, residence, password);
        assertNull(result);
    }
}
