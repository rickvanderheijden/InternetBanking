package unittest;

import com.ark.bank.BankController;
import com.ark.bank.IBankController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

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
        bankController = new BankController(BankIdInternal, null);
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
