package unittest;

import com.ark.bank.BankController;
import com.ark.bank.IBankController;
import com.ark.bank.SessionTerminated;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unittest.stubs.CentralBankConnectionStub;

import java.util.Observable;
import java.util.Observer;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestBankControllerSession implements Observer {

    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private static final String BankIdInternal = "TEST";
    private IBankController bankController;
    private String sessionKey;
    private String sessionKeyTerminated;

    @Before
    public void setUp() {
        bankController = new BankController(BankIdInternal, new CentralBankConnectionStub());
        bankController.setSessionTime(100);

        bankController.addObserver(this);

        sessionKey = null;
        sessionKeyTerminated= null;
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
        assertEquals(sessionKey, sessionKeyTerminated);
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

    @Override
    public void update(Observable o, Object arg) {
        if ((o instanceof BankController) && (arg instanceof SessionTerminated)) {
            SessionTerminated sessionTerminated = (SessionTerminated)arg;
            sessionKeyTerminated = sessionTerminated.getSessionKey();
        }
    }
}
