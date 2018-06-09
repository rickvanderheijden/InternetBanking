package unittest;

import com.ark.Customer;
import com.ark.bank.BankController;
import com.ark.bank.IBankAccount;
import com.ark.bank.IBankController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unittest.stubs.CentralBankConnectionStub;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestBankControllerBankAccount {

    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private final String BankIdInternal = "TEST";
    private IBankController bankController;
    private String sessionKey;

    @Before
    public void setUp() {
        bankController = new BankController(BankIdInternal, new CentralBankConnectionStub());
    }

    @After
    public void tearDown() {
        bankController = null;
    }

    @Test
    public void testCreateBankAccountSessionKeyNullOwnerNull() {
        IBankAccount result = bankController.createBankAccount(null, null);
        assertNull(result);
    }

    @Test
    public void testCreateBankAccountValidSessionKeyOwnerNull() {
        createCustomerAndLogin();
        IBankAccount result = bankController.createBankAccount(sessionKey, null);
        assertNull(result);
    }

    @Test
    public void testCreateBankAccountInvalidSessionKeyOwnerNull() {
        String sessionKey = "Invalid";
        IBankAccount result = bankController.createBankAccount(sessionKey, null);
        assertNull(result);
    }

    @Test
    public void testCreateBankAccount() {
        Customer owner = createCustomerAndLogin();
        IBankAccount result = bankController.createBankAccount(sessionKey, owner);
        assertThat(result.getNumber(), startsWith(BankIdInternal));
        assertEquals(   14, result.getNumber().length());
        assertEquals(10000, result.getCreditLimit());
        assertEquals(owner, result.getOwner());
    }

    @Test
    public void testCreateBankAccountInvalidSession() {
        Customer owner = createCustomerAndLogin();
        IBankAccount result = bankController.createBankAccount("InvalidSession", owner);
        assertNull(result);
    }

    @Test
    public void testGetBankAccountValidValues() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        IBankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        IBankAccount result = bankController.getBankAccount(sessionKey, bankAccount.getNumber());
        assertNotNull(result);
        assertEquals(result.getNumber(), bankAccount.getNumber());
    }

    @Test
    public void testGetBankAccountBankAccountNumberNull() {
        Customer owner = createCustomerAndLogin();
        bankController.createBankAccount(sessionKey, owner);
        IBankAccount result = bankController.getBankAccount(sessionKey, null);
        assertNull(result);
    }

    @Test
    public void testGetBankAccountBankAccountNumberEmpty() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        bankController.createBankAccount(sessionKey, owner);
        IBankAccount result = bankController.getBankAccount(sessionKey, "");
        assertNull(result);
    }

    @Test
    public void testGetBankAccountBankAccountNumberInvalid() {
        Customer owner = createCustomerAndLogin();
        bankController.createBankAccount(sessionKey, owner);
        IBankAccount result = bankController.getBankAccount(sessionKey, "InvalidNumber");
        assertNull(result);
    }

    @Test
    public void testGetBankAccountSessionExpired() {
        Customer owner = createCustomerAndLogin();
        IBankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        bankController.terminateSession(sessionKey);
        IBankAccount result = bankController.getBankAccount(sessionKey, bankAccount.getNumber());
        assertNull(result);
    }

    @Test
    public void testGetBankAccountSessionInvalid() {
        Customer owner = createCustomerAndLogin();
        IBankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        IBankAccount result = bankController.getBankAccount("InvalidSession", bankAccount.getNumber());
        assertNull(result);
    }

    @Test
    public void testGetBankAccountSessionEmpty() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        IBankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        IBankAccount result = bankController.getBankAccount("", bankAccount.getNumber());
        assertNull(result);
    }

    @Test
    public void testGetBankAccountSessionNull() {
        Customer owner = createCustomerAndLogin();
        IBankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        IBankAccount result = bankController.getBankAccount(null, bankAccount.getNumber());
        assertNull(result);
    }

    @Test
    public void testGetBankAccountOtherCustomer() {
        createCustomerAndLogin();
        Customer owner = new Customer(Name, Residence, Password);
        IBankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);

        bankController.createCustomer("OtherName", "OtherResidence", "OtherPassword");
        String otherSessionKey = bankController.login("OtherName", "OtherResidence", "OtherPassword");
        IBankAccount result = bankController.getBankAccount(otherSessionKey, bankAccount.getNumber());

        assertNull(result);
    }

    @Test
    public void testGetBankAccountNumbersValidValuesOneAccount() {
        Customer owner = createCustomerAndLogin();
        IBankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        List<String> bankAccountNumbers = bankController.getBankAccountNumbers(sessionKey);
        assertEquals(1, bankAccountNumbers.size());
        assertTrue(bankAccountNumbers.contains(bankAccount.getNumber()));
    }

    @Test
    public void testGetBankAccountNumbersValidValuesFiveAccounts() {
        Customer owner = createCustomerAndLogin();
        IBankAccount bankAccount1 = bankController.createBankAccount(sessionKey, owner);
        IBankAccount bankAccount2 = bankController.createBankAccount(sessionKey, owner);
        IBankAccount bankAccount3 = bankController.createBankAccount(sessionKey, owner);
        IBankAccount bankAccount4 = bankController.createBankAccount(sessionKey, owner);
        IBankAccount bankAccount5 = bankController.createBankAccount(sessionKey, owner);
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
        Customer owner = createCustomerAndLogin();
        bankController.createBankAccount(sessionKey, owner);

        bankController.createCustomer("OtherName", "OtherResidence", "OtherPassword");
        String otherSessionKey = bankController.login("OtherName", "OtherResidence", "OtherPassword");

        List<String> bankAccountNumbers = bankController.getBankAccountNumbers(otherSessionKey);
        assertEquals(0, bankAccountNumbers.size());
    }

    @Test
    public void testGetBankAccountNumbersExpiredSession() {
        Customer owner = createCustomerAndLogin();
        bankController.createBankAccount(sessionKey, owner);

        bankController.terminateSession(sessionKey);
        List<String> bankAccountNumbers = bankController.getBankAccountNumbers(sessionKey);
        assertEquals(0, bankAccountNumbers.size());
    }

    @Test
    public void testIsValidBankAccountNumberValidNumber() {
        Customer owner = createCustomerAndLogin();
        IBankAccount bankAccount = bankController.createBankAccount(sessionKey, owner);
        boolean result = bankController.isValidBankAccountNumber(bankAccount.getNumber());
        assertTrue(result);
    }

    @Test
    public void testIsValidBankAccountNumber() {
        createCustomerAndLogin();
        boolean result = bankController.isValidBankAccountNumber("InvalidNumber");
        assertFalse(result);
    }

    private Customer createCustomerAndLogin() {
        Customer customer = bankController.createCustomer(Name, Residence, Password);
        sessionKey = bankController.login(Name, Residence, Password);

        return customer;
    }
}
