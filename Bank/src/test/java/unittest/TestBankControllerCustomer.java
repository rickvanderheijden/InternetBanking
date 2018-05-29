package unittest;

import com.ark.bank.BankController;
import com.ark.bank.Customer;
import com.ark.bank.IBankController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestBankControllerCustomer {

    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private final String BankIdInternal = "TEST";
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
    public void testCreateCustomerAllValuesNull() {
        Customer result = bankController.createCustomer(null, null, null);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerAllValuesEmpty() {
        Customer result = bankController.createCustomer("", "", "");
        assertNull(result);
    }

    @Test
    public void testCreateCustomerNameNull() {
        Customer result = bankController.createCustomer(null, Residence, Password);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerNameEmpty() {
        Customer result = bankController.createCustomer("", Residence, Password);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerPasswordNull() {
        Customer result = bankController.createCustomer(Name, Residence, null);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerPasswordEmpty() {
        Customer result = bankController.createCustomer(Name, Residence, "");
        assertNull(result);
    }

    @Test
    public void testCreateCustomerResidenceNull() {
        Customer result = bankController.createCustomer(Name, null, Password);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerResidenceEmpty() {
        Customer result = bankController.createCustomer(Name, "", Password);
        assertNull(result);
    }

    @Test
    public void testCreateCustomerValidValues() {
        Customer result = bankController.createCustomer(Name, Residence, Password);
        assertNotNull(result);
        assertEquals(Name, result.getName());
        assertEquals(Residence, result.getResidence());
        assertTrue(result.isPasswordValid(Password));
    }

    @Test
    public void testCreateCustomerAlreadyExists() {
        Customer result = bankController.createCustomer(Name, Residence, Password);
        assertNotNull(result);
        result = bankController.createCustomer(Name, Residence, Password);
        assertNull(result);
    }

    @Test
    public void TestGetCustomerInvalidName() {
        bankController.createCustomer(Name, Residence, Password);
        String sessionKey = bankController.login(Name, Residence, Password);
        Customer result = bankController.getCustomer(sessionKey, "WrongName", Residence);
        assertNull(result);
    }

    @Test
    public void TestGetCustomerInvalidResidence() {
        bankController.createCustomer(Name, Residence, Password);
        String sessionKey = bankController.login(Name, Residence, Password);
        Customer result = bankController.getCustomer(sessionKey, Name, "InvalidResidence");
        assertNull(result);
    }

    @Test
    public void TestGetCustomerExpiredSession() {
        createCustomerAndLogin();
        bankController.terminateSession(sessionKey);
        Customer result = bankController.getCustomer(sessionKey, Name, Residence);
        assertNull(result);
    }

    @Test
    public void testGetCustomerValidValues() {
        createCustomerAndLogin();
        Customer result = bankController.getCustomer(sessionKey, Name, Residence);
        assertEquals(Name, result.getName());
        assertEquals(Residence, result.getResidence());
        assertTrue(result.isPasswordValid(Password));
    }

    private void createCustomerAndLogin() {
        bankController.createCustomer(Name, Residence, Password);
        sessionKey = bankController.login(Name, Residence, Password);
    }
}
