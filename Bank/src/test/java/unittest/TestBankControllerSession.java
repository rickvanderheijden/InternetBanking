package unittest;

import com.ark.bank.BankController;
import com.ark.bank.DatabaseController;
import com.ark.bank.IBankController;
import com.ark.bank.SessionTerminated;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unittest.stubs.CentralBankConnectionStub;

import java.util.Observable;
import java.util.Observer;

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
        bankController = new BankController(BankIdInternal, new CentralBankConnectionStub(), new DatabaseController(BankIdInternal));
        bankController.setSessionTime(100);
        bankController.addObserver(this);

        sessionKey = null;
        sessionKeyTerminated= null;

        createCustomerAndLogin();
    }

    @After
    public void tearDown() {
        bankController = null;
    }

    @Test
    public void testTerminateSessionValidValues() {
        testTerminateSession(sessionKey, true);
        assertEquals(sessionKey, sessionKeyTerminated);
    }

    @Test
    public void testTerminateSessionSessionKeyNull() {
        testTerminateSession(null, false);
    }

    @Test
    public void testTerminateSessionSessionKeyEmpty() {
        testTerminateSession("", false);
    }

    @Test
    public void testTerminateSessionSessionKeyInvalid() {
        testTerminateSession("InvalidKey", false);
    }

    @Test
    public void testRefreshSessionValidValues() {
        testRefreshSession(sessionKey, true);
    }

    @Test
    public void testRefreshSessionSessionKeyNull() {
        testRefreshSession(null, false);
    }

    @Test
    public void testRefreshSessionSessionKeyEmpty() {
        testRefreshSession("", false);
    }

    @Test
    public void testRefreshSessionSessionKeyInvalid() {
        testRefreshSession("InvalidKey", false);
    }

    private void testTerminateSession(String sessionKey, boolean expectedResult) {
        boolean result = bankController.terminateSession(sessionKey);
        assertEquals(expectedResult, result);
    }

    private void testRefreshSession(String sessionKey, boolean expectedResult) {
        boolean result = bankController.refreshSession(sessionKey);
        assertEquals(expectedResult, result);
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
