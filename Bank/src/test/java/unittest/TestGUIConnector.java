package unittest;

import com.ark.bank.BankController;
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

    @Before
    public void setUp() throws RemoteException {
        bankController = new BankController(BankIdInternal);
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
        String result = guiConnector.login(Name, Residence, Password);
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
        String sessionKey = guiConnector.login(Name, Residence, Password);
        boolean result = guiConnector.logout(sessionKey);
        assertTrue(result);
    }

    private void createCustomer() {
        guiConnector.createCustomer(Name, Residence, Password);
    }
}
