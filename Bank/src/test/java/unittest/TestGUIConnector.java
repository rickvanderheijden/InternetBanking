package unittest;

import com.ark.bank.BankController;
import com.ark.bank.Customer;
import com.ark.bank.GUIConnector;
import com.ark.bank.IBankController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestGUIConnector {

    private static final String BankIdInternal = "TEST";
    private static final String Name = "Name";
    private static final String Residence = "Residence";
    private static final String Password = "Password";

    private IBankController bankController;
    private GUIConnector guiConnector;
    private String sessionKey;

    @Before
    public void setUp() throws RemoteException {
        bankController = new BankController(BankIdInternal, null);
        guiConnector = new GUIConnector(bankController);
        guiConnector.createCustomer(Name, Residence, Password);
        sessionKey = guiConnector.login(Name, Residence, Password);
    }

    @After
    public void tearDown() {
        guiConnector = null;
        bankController = null;
    }


    @Test
    public void testLoginWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        String result = guiConnector.login(Name, Residence, Password);
        assertNull(result);
    }

    @Test
    public void testLoginWithoutRegisteredCustomer() {
        String result = guiConnector.login("OtherName", "OtherResidence", "OtherPassword");
        assertNull(result);
    }

    @Test
    public void testLoginWithRegisteredCustomer() {
        String result = guiConnector.login(Name, Residence, Password);
        assertNotNull(result);
    }

    @Test
    public void testLogoutWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        String result = guiConnector.login(Name, Residence, Password);
        assertNull(result);
    }

    @Test
    public void testLogoutWithSessionKeyNull() {
        boolean result = guiConnector.logout(null);
        assertFalse(result);
    }

    @Test
    public void testLogoutWithRegisteredCustomer() {
        boolean result = guiConnector.logout(sessionKey);
        assertTrue(result);
    }

    @Test
    public void testIsSessionActive() {
        boolean result = guiConnector.isSessionActive(sessionKey);
        assertTrue(result);
    }

    @Test
    public void testIsSessionActiveWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        boolean result = guiConnector.isSessionActive(sessionKey);
        assertFalse(result);
    }

    @Test
    public void testRefreshSession() {
        boolean result = guiConnector.refreshSession(sessionKey);
        assertTrue(result);
    }

    @Test
    public void testRefreshSessionWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        boolean result = guiConnector.refreshSession(sessionKey);
        assertFalse(result);
    }

    @Test
    public void testTerminateSession() {
        boolean result = guiConnector.terminateSession(sessionKey);
        assertTrue(result);
    }

    @Test
    public void testTerminateSessionWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        boolean result = guiConnector.terminateSession(sessionKey);
        assertFalse(result);
    }

    @Test
    public void testGetCustomer() {
        Customer result = guiConnector.getCustomer(sessionKey, Name, Residence);
        assertEquals(Name, result.getName());
        assertEquals(Residence, result.getResidence());
    }

    @Test
    public void testGetCustomerWithNoBankController() throws RemoteException {
        guiConnector = new GUIConnector(null);
        Customer result = guiConnector.getCustomer(sessionKey, Name, Residence);
        assertNull(result);
    }


    //TODO: What if user is already logged in?
}
