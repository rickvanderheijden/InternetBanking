package unittest;

import com.ark.bank.BankController;
import com.ark.Customer;
import com.ark.bank.DatabaseController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unittest.stubs.CentralBankConnectionStub;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestBankControllerCustomer {

    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private static final String BankIdInternal = "TEST";
    private BankController bankController;
    private String sessionKey;

    @Before
    public void setUp() {
        bankController = new BankController(BankIdInternal, new CentralBankConnectionStub(), new DatabaseController(BankIdInternal));
    }

    @After
    public void tearDown() {
        bankController = null;
    }

    @Test
    public void testCreateCustomerAllValuesNull() {
        testCreateCustomer(null, null, null, false);
    }

    @Test
    public void testCreateCustomerAllValuesEmpty() {
        testCreateCustomer("", "", "", false);
    }

    @Test
    public void testCreateCustomerNameNull() {
        testCreateCustomer(null, Residence, Password, false);
    }

    @Test
    public void testCreateCustomerNameEmpty() {
        testCreateCustomer("", Residence, Password, false);
    }

    @Test
    public void testCreateCustomerPasswordNull() {
        testCreateCustomer(Name, Residence, null, false);
    }

    @Test
    public void testCreateCustomerPasswordEmpty() {
        testCreateCustomer(Name, Residence, "", false);
    }

    @Test
    public void testCreateCustomerResidenceNull() {
        testCreateCustomer(Name, null, Password, false);
    }

    @Test
    public void testCreateCustomerResidenceEmpty() {
        testCreateCustomer(Name, "", Password, false);
    }

    @Test
    public void testCreateCustomerValidValues() {
        Customer result = testCreateCustomer(Name, Residence, Password, true);
        assertEquals(Name, result.getName());
        assertEquals(Residence, result.getResidence());
        assertTrue(result.isPasswordValid(Password));
    }

    @Test
    public void testCreateCustomerAlreadyExists() {
        testCreateCustomer(Name, Residence, Password, true);
        testCreateCustomer(Name, Residence, Password, false);
    }

    @Test
    public void TestGetCustomerInvalidName() {
        createCustomerAndLogin();
        checkGetCustomer(sessionKey, "WrongName", Residence, false);
    }

    @Test
    public void TestGetCustomerInvalidResidence() {
        createCustomerAndLogin();
        checkGetCustomer(sessionKey, Name, "InvalidResidence", false);
    }

    @Test
    public void TestGetCustomerExpiredSession() {
        createCustomerAndLogin();
        bankController.terminateSession(sessionKey);
        checkGetCustomer(sessionKey, Name, Residence, false);
    }

    @Test
    public void testGetCustomerValidValues() {
        createCustomerAndLogin();
        Customer result = checkGetCustomer(sessionKey, Name, Residence, true);
        assertEquals(Name, result.getName());
        assertEquals(Residence, result.getResidence());
        assertTrue(result.isPasswordValid(Password));
    }

    @Test
    public void testRemoveCustomerValidValues() {
        createCustomerAndLogin();
        boolean result = bankController.removeCustomer(sessionKey, Name, Residence);
        Customer customer = bankController.getCustomer(sessionKey, Name, Residence);
        assertTrue(result);
        assertNull(customer);
    }

    @Test
    public void testRemoveCustomerSessionKeyNull() {
        testRemoveCustomer(null, Name, Residence);
    }

    @Test
    public void testRemoveCustomerSessionKeyEmpty() {
        testRemoveCustomer("", Name, Residence);
    }

    @Test
    public void testRemoveCustomerSessionKeyInvalid() {
        testRemoveCustomer("InvalidSessionKey", Name, Residence);
    }

    @Test
    public void testRemoveCustomerSessionExpired() {
        createCustomerAndLogin();
        bankController.terminateSession(sessionKey);
        boolean result = bankController.removeCustomer(sessionKey, Name, Residence);
        sessionKey = bankController.login(Name, Residence, Password);
        Customer customer = bankController.getCustomer(sessionKey, Name, Residence);
        assertFalse(result);
        assertNotNull(customer);
    }

    @Test
    public void testRemoveCustomerNameNull() {
        testRemoveCustomer(sessionKey, null, Residence);
    }

    @Test
    public void testRemoveCustomerNameEmpty() {
        testRemoveCustomer(sessionKey, "", Residence);
    }

    @Test
    public void testRemoveCustomerNameInvalid() {
        testRemoveCustomer(sessionKey, "InvalidName", Residence);
    }

    @Test
    public void testRemoveCustomerResidenceNull() {
        testRemoveCustomer(sessionKey, Name, null);
    }

    @Test
    public void testRemoveCustomerResidenceEmpty() {
        testRemoveCustomer(sessionKey, Name, "");
    }

    @Test
    public void testRemoveCustomerResidenceInvalid() {
        testRemoveCustomer(sessionKey, Name, "InvalidResidence");
    }

    @Test
    public void testRemoveCustomerSessionKeyOfOtherCustomer() {
        createCustomerAndLogin();
        bankController.createCustomer("Name2", "Residence2", "Password2");
        String sessionKey2 = bankController.login("Name2", "Residence2", "Password2");

        boolean result = bankController.removeCustomer(sessionKey2, Name, Residence);
        Customer customer = bankController.getCustomer(sessionKey, Name, Residence);
        assertFalse(result);
        assertNotNull(customer);
    }

    private void createCustomerAndLogin() {
        bankController.createCustomer(Name, Residence, Password);
        sessionKey = bankController.login(Name, Residence, Password);
    }

    private Customer testCreateCustomer(String name, String residence, String password, boolean expectedResult) {
        Customer customer = bankController.createCustomer(name, residence, password);
        checkIfCustomerIsNull(customer, !expectedResult);
        return customer;
    }

    private Customer checkGetCustomer(String sessionKey, String name, String residence, boolean expectedResult) {
        Customer customer = bankController.getCustomer(sessionKey, name, residence);
        checkIfCustomerIsNull(customer, !expectedResult);
        return customer;
    }

    private void testRemoveCustomer(String sessionKey, String name, String residence) {
        createCustomerAndLogin();
        boolean result = bankController.removeCustomer(sessionKey, name, residence);
        Customer customer = bankController.getCustomer(this.sessionKey, Name, Residence);
        assertFalse(result);
        checkIfCustomerIsNull(customer, false);
    }

    private void checkIfCustomerIsNull(Customer customer, boolean expectedNull) {
        if (expectedNull) {
            assertNull(customer);
        } else {
            assertNotNull(customer);
        }
    }
}
