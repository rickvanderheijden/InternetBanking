package unittest;

import com.ark.bank.BankController;
import com.ark.bank.IBankController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestBankControllerSession {

    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private static final String BankIdInternal = "TEST";
    private IBankController bankController;
    private String sessionKey;

    @Before
    public void setUp() {
        bankController = new BankController(BankIdInternal, null);
    }

    @After
    public void tearDown() {
        bankController = null;
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



    private void createCustomerAndLogin() {
        bankController.createCustomer(Name, Residence, Password);
        sessionKey = bankController.login(Name, Residence, Password);
    }
}
