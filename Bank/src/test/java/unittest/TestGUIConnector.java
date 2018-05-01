package unittest;

import com.ark.bank.BankController;
import com.ark.bank.GUIConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        int expectedResult = -1;
        int result = guiConnector.login("Name", "Residence", "password");
        assertEquals(expectedResult, result);
    }

    @Test
    public void testLoginWithRegisteredCustomer() {
        String name = "Name";
        String residence = "Residence";
        String password = "Password";

        guiConnector.createCustomer(name, residence, password);

        int result = guiConnector.login(name, residence, password);

        System.out.println(result);


        assertTrue(result > -1);
    }
}
