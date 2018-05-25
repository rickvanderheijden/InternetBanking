package unittest;

import com.ark.bank.BankAccount;
import com.ark.bank.BankController;
import com.ark.bank.Customer;
import com.ark.bank.IBankController;
import com.ark.centralbank.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestBankController {

    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private final String BankIdInternal = "TEST";
    private final String BankIdExternal = "NONE";
    private final String accountFromExternal = BankIdExternal + "287493762";
    private final String accountToExternal   = BankIdExternal + "239475623";
    private String accountFromInternal;
    private String accountToInternal;
    private IBankController bankController;
    private String sessionKey;

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
    public void testCreateBankAccountSessionKeyNullOwnerNull() {
        BankAccount result = bankController.createBankAccount(null, null);
        assertNull(result);
    }

    @Test
    public void testCreateBankAccountValidSessionKeyOwnerNull() {
        createCustomerAndLogin();
        BankAccount result = bankController.createBankAccount(sessionKey, null);
        assertNull(result);
    }

    @Test
    public void testCreateBankAccountInvalidSessionKeyOwnerNull() {
        String sessionKey = "Invalid";
        BankAccount result = bankController.createBankAccount(sessionKey, null);
        assertNull(result);
    }

    @Test
    public void testCreateBankAccount() {
        Customer owner = bankController.createCustomer(Name, Residence, Password);
        sessionKey = bankController.login(Name, Residence, Password);
        BankAccount result = bankController.createBankAccount(sessionKey, owner);
        assertThat(result.getNumber(), startsWith(BankIdInternal));
        assertEquals(14, result.getNumber().length());
        assertEquals(100.0, result.getCreditLimit(), 0.0);
        assertEquals(owner, result.getOwner());
    }

    @Test
    public void testCreateBankAccountInvalidSession() {
        Customer owner = bankController.createCustomer(Name, Residence, Password);
        sessionKey = bankController.login(Name, Residence, Password);
        BankAccount result = bankController.createBankAccount("InvalidSession", owner);
        assertNull(result);
    }

    @Test
    public void testExecuteTransactionNull() {
        createCustomerAndLogin();
        boolean result = bankController.executeTransaction(sessionKey, null);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAllValuesNull() {
        createCustomerAndLogin();
        Transaction transaction = new Transaction(.00, null, null, null);
        boolean result = bankController.executeTransaction(sessionKey, transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAccountToNull() {
        createCustomerAndLogin();
        Transaction transaction = new Transaction(22.95, "Description", "AccountFrom", null);
        boolean result = bankController.executeTransaction(sessionKey, transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDescriptionNull() {
        createCustomerAndLogin();
        Transaction transaction = new Transaction(22.95, null, "AccountFrom", "AccountTo");
        boolean result = bankController.executeTransaction(sessionKey, transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAmountNull() {
        Transaction transaction = new Transaction(0.0, "Description", "AccountFrom", "AccountTo");
        boolean result = bankController.executeTransaction(sessionKey, transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValues() {

        //TODO: IS THIS CORRECT? MISSES A LOT OF STUFF
        //TODO: Create stubs and all?
        createCustomerAndLogin();
        Transaction transaction = new Transaction(21.0, "This is a test transaction", "ABNA0123456789", "RABO0123456789");
        boolean result = bankController.executeTransaction(sessionKey, transaction);
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
        Customer result = bankController.createCustomer(null, Residence, Password);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerNameEmpty() {
        Customer result = bankController.createCustomer("", Residence, Password);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerPasswordNull() {
        Customer result = bankController.createCustomer(Name, Residence, null);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerPasswordEmpty() {
        Customer result = bankController.createCustomer(Name, Residence, "");
        assertNull(result);
    }

    @Test
    public void testCreateCustomerResidenceNull() {
        Customer result = bankController.createCustomer(Name, null, Password);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerResidenceEmpty() {
        Customer result = bankController.createCustomer(Name, "", Password);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerValidValues() {
        Customer result = bankController.createCustomer(Name, Residence, Password);
        assertNotNull(result);
        assertEquals(Name, result.getName());
        assertEquals(Residence, result.getResidence());
        assertTrue(result.isPasswordValid(Password));
    }

    @Test
    public void testCreateCustomerAlreadyExists() {
        Customer result = bankController.createCustomer(Name, Residence, Password);
        assertNotNull(result);
        result = bankController.createCustomer(Name, Residence, Password);
        assertNull(result);
    }

    @Test
    public void TestGetCustomerInvalidName() {
        bankController.createCustomer(Name, Residence, Password);
        String sessionKey = bankController.login(Name, Residence, Password);
        Customer result = bankController.getCustomer(sessionKey, "WrongName", Residence);
        assertNull(result);
    }

    @Test
    public void TestGetCustomerInvalidResidence() {
        bankController.createCustomer(Name, Residence, Password);
        String sessionKey = bankController.login(Name, Residence, Password);
        Customer result = bankController.getCustomer(sessionKey, Name, "InvalidResidence");
        assertNull(result);
    }

    @Test
    public void TestGetCustomerExpiredSession() {
        createCustomerAndLogin();
        bankController.terminateSession(sessionKey);
        Customer result = bankController.getCustomer(sessionKey, Name, Residence);
        assertNull(result);
    }

    @Test
    public void testGetCustomerValidValues() {
        createCustomerAndLogin();
        Customer result = bankController.getCustomer(sessionKey, Name, Residence);
        assertEquals(Name, result.getName());
        assertEquals(Residence, result.getResidence());
        assertTrue(result.isPasswordValid(Password));
    }

    @Test
    public void testTerminateSessionValidValues() {
        createCustomerAndLogin();
        boolean result = bankController.terminateSession(sessionKey);
        assertTrue(result);
    }

    @Test
    public void testTerminateSessionSessionKeyNull() {
        createCustomerAndLogin();
        boolean result = bankController.terminateSession(null);
        assertFalse(result);
    }

    @Test
    public void testTerminateSessionSessionKeyEmpty() {
        createCustomerAndLogin();
        boolean result = bankController.terminateSession("");
        assertFalse(result);
    }

    @Test
    public void testTerminateSessionSessionKeyInvalid() {
        createCustomerAndLogin();
        boolean result = bankController.terminateSession("InvalidKey");
        assertFalse(result);
    }

    @Test
    public void testRefreshSessionValidValues() {
        createCustomerAndLogin();
        boolean result = bankController.refreshSession(sessionKey);
        assertTrue(result);
    }

    @Test
    public void testRefreshSessionSessionKeyNull() {
        createCustomerAndLogin();
        boolean result = bankController.refreshSession(null);
        assertFalse(result);
    }

    @Test
    public void testRefreshSessionSessionKeyEmpty() {
        createCustomerAndLogin();
        boolean result = bankController.refreshSession("");
        assertFalse(result);
    }

    @Test
    public void testRefreshSessionSessionKeyInvalid() {
        createCustomerAndLogin();
        boolean result = bankController.refreshSession("InvalidKey");
        assertFalse(result);
    }

    @Test
    public void testLogoutValidValues() {
        createCustomerAndLogin();
        boolean result = bankController.logout(sessionKey);
        assertTrue(result);
    }

    @Test
    public void testLogoutSessionExpired() {
        createCustomerAndLogin();
        bankController.terminateSession(sessionKey);
        boolean result = bankController.logout(sessionKey);
        assertFalse(result);
    }

    @Test
    public void testLogoutSessionInvalid() {
        createCustomerAndLogin();
        boolean result = bankController.logout("InvalidSession");
        assertFalse(result);
    }

    @Test
    public void testLogoutSessionNull() {
        createCustomerAndLogin();
        boolean result = bankController.logout(null);
        assertFalse(result);
    }

    @Test
    public void testLogoutSessionEmpty() {
        createCustomerAndLogin();
        boolean result = bankController.logout("");
        assertFalse(result);
    }

    @Test
    public void testGetBankAccountValidValues() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        BankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        BankAccount result = bankController.getBankAccount(sessionKey, bankAccount.getNumber());
        assertNotNull(result);
        assertEquals(result.getNumber(), bankAccount.getNumber());
    }

    @Test
    public void testGetBankAccountBankAccountNumberNull() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        bankController.createBankAccount(sessionKey, owner);
        BankAccount result = bankController.getBankAccount(sessionKey, null);
        assertNull(result);
    }

    @Test
    public void testGetBankAccountBankAccountNumberEmpty() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        bankController.createBankAccount(sessionKey, owner);
        BankAccount result = bankController.getBankAccount(sessionKey, "");
        assertNull(result);
    }

    @Test
    public void testGetBankAccountBankAccountNumberInvalid() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        bankController.createBankAccount(sessionKey, owner);
        BankAccount result = bankController.getBankAccount(sessionKey, "InvalidNumber");
        assertNull(result);
    }

    @Test
    public void testGetBankAccountSessionExpired() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        BankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        bankController.terminateSession(sessionKey);
        BankAccount result = bankController.getBankAccount(sessionKey, bankAccount.getNumber());
        assertNull(result);
    }

    @Test
    public void testGetBankAccountSessionInvalid() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        BankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        BankAccount result = bankController.getBankAccount("InvalidSession", bankAccount.getNumber());
        assertNull(result);
    }

    @Test
    public void testGetBankAccountSessionEmpty() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        BankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        BankAccount result = bankController.getBankAccount("", bankAccount.getNumber());
        assertNull(result);
    }

    @Test
    public void testGetBankAccountSessionNull() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        BankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        BankAccount result = bankController.getBankAccount(null, bankAccount.getNumber());
        assertNull(result);
    }

    @Test
    public void testGetBankAccountOtherCustomer() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        BankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);

        bankController.createCustomer("OtherName", "OtherResidence", "OtherPassword");
        String otherSessionKey = bankController.login("OtherName", "OtherResidence", "OtherPassword");
        BankAccount result = bankController.getBankAccount(otherSessionKey, bankAccount.getNumber());

        assertNull(result);
    }

    @Test
    public void testGetBankAccountNumbersValidValuesOneAccount() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        BankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        List<String> bankAccountNumbers = bankController.getBankAccountNumbers(sessionKey);
        assertEquals(1, bankAccountNumbers.size());
        assertTrue(bankAccountNumbers.contains(bankAccount.getNumber()));
    }

    @Test
    public void testGetBankAccountNumbersValidValuesFiveAccounts() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        BankAccount bankAccount1 = bankController.createBankAccount(sessionKey, owner);
        BankAccount bankAccount2 = bankController.createBankAccount(sessionKey, owner);
        BankAccount bankAccount3 = bankController.createBankAccount(sessionKey, owner);
        BankAccount bankAccount4 = bankController.createBankAccount(sessionKey, owner);
        BankAccount bankAccount5 = bankController.createBankAccount(sessionKey, owner);
        List<String> bankAccountNumbers = bankController.getBankAccountNumbers(sessionKey);
        assertEquals(5, bankAccountNumbers.size());
        assertTrue(bankAccountNumbers.contains(bankAccount1.getNumber()));
        assertTrue(bankAccountNumbers.contains(bankAccount2.getNumber()));
        assertTrue(bankAccountNumbers.contains(bankAccount3.getNumber()));
        assertTrue(bankAccountNumbers.contains(bankAccount4.getNumber()));
        assertTrue(bankAccountNumbers.contains(bankAccount5.getNumber()));
    }

    @Test
    public void testGetBankAccountNumbersOtherOwner() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        bankController.createBankAccount(sessionKey, owner);

        bankController.createCustomer("OtherName", "OtherResidence", "OtherPassword");
        String otherSessionKey = bankController.login("OtherName", "OtherResidence", "OtherPassword");

        List<String> bankAccountNumbers = bankController.getBankAccountNumbers(otherSessionKey);
        assertEquals(0, bankAccountNumbers.size());
    }

    @Test
    public void testGetBankAccountNumbersExpiredSession() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        bankController.createBankAccount(sessionKey, owner);

        bankController.terminateSession(sessionKey);
        List<String> bankAccountNumbers = bankController.getBankAccountNumbers(sessionKey);
        assertEquals(0, bankAccountNumbers.size());
    }

    private void createCustomerAndLogin() {
        bankController.createCustomer(Name, Residence, Password);
        sessionKey = bankController.login(Name, Residence, Password);
    }
}
