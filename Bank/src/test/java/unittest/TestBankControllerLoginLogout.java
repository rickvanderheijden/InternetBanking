package unittest;

import com.ark.bank.BankController;
import unittest.stubs.DatabaseControllerStub;
import com.ark.bank.IBankController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unittest.stubs.CentralBankConnectionStub;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * @author Rick van der Heijden
 */
public class TestBankControllerLoginLogout {

    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private static final String BankIdInternal = "TEST";
    private IBankController bankController;
    private String sessionKey;

    @Before
    public void setUp() {
        bankController = new BankController(BankIdInternal, new CentralBankConnectionStub(), new DatabaseControllerStub());
    }

    @After
    public void tearDown() {
        bankController = null;
    }

    @Test
    public void testLogoutValidValues() {
        createCustomerAndLogin();
        boolean result = bankController.logout(sessionKey);
        assertTrue(result);
    }

    @Test
    public void testLoginUnknownCustomer() {
        String result = bankController.login(Name, Residence, Password);
        assertNull(result);
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

    private void createCustomerAndLogin() {
        bankController.createCustomer(Name, Residence, Password);
        sessionKey = bankController.login(Name, Residence, Password);
    }
}
