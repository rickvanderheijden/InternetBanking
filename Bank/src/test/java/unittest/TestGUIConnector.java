package unittest;

import com.ark.bank.BankController;
import com.ark.bank.GUIConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.*;

public class TestGUIConnector {

    private final String BankIdInternal = "TEST";
    private BankController bankController;
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
        String name = "Name";
        String residence = "Residence";
        String password = "Password";

        guiConnector.createCustomer(name, residence, password);
        String result = guiConnector.login(name, residence, password);
        assertNotNull(result);
    }
}
