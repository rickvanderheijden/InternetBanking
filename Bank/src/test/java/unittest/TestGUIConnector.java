package unittest;

import com.ark.bank.BankController;
import com.ark.bank.GUIConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestGUIConnector {

    private final String bankIdInternal = "TEST";
    private final String name = "Name";
    private final String residence = "Residence";
    private final String password = "Password";

    private BankController bankController;
    private GUIConnector guiConnector;

    @Before
    public void setUp() throws RemoteException {
        bankController = new BankController(bankIdInternal);
        guiConnector = new GUIConnector(bankController);
    }

    @After
    public void tearDown() {
        guiConnector = null;
        bankController = null;
    }

    @Test
    public void testLoginWithoutRegisteredCustomer() {
        String result = guiConnector.login("Name", "Residence", "password");
        assertNull(result);
    }

    @Test
    public void testLoginWithRegisteredCustomer() {
        createCustomer();
        String result = guiConnector.login(name, residence, password);
        assertNotNull(result);
    }

    @Test
    public void testLogoutNull() {
        boolean result = guiConnector.logout(null);
        assertFalse(result);
    }

    @Test
    public void testLogoutWithRegisteredCustomer() {
        createCustomer();
        String sessionKey = guiConnector.login(name, residence, password);
        boolean result = guiConnector.logout(sessionKey);
        assertTrue(result);
    }

    private void createCustomer() {
        guiConnector.createCustomer(name, residence, password);
    }
}
