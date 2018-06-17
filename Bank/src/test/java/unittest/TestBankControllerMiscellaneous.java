package unittest;

import com.ark.BankConnectionInfo;
import com.ark.bank.BankController;
import unittest.stubs.DatabaseControllerStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unittest.stubs.CentralBankConnectionStub;

import static org.junit.Assert.assertTrue;

/**
 * @author Rick van der Heijden
 */
public class TestBankControllerMiscellaneous {
    private static final String BankIdInternal = "TEST";
    private BankController bankController;

    @Before
    public void setUp() {
        bankController = new BankController(BankIdInternal, new CentralBankConnectionStub(), new DatabaseControllerStub());
    }

    @After
    public void tearDown() {
        bankController = null;
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBankControllerConstructorAllNull() {
        bankController = new BankController(null, null, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBankControllerConstructorBankIdNull() {
        bankController = new BankController(null, new CentralBankConnectionStub(), new DatabaseControllerStub());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBankControllerConstructorBankIdEmpty() {
        bankController = new BankController("", new CentralBankConnectionStub(), new DatabaseControllerStub());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBankControllerConstructorCentralBankConnectionNull() {
        bankController = new BankController(BankIdInternal, null, new DatabaseControllerStub());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testBankControllerConstructorDatabaseControllerNull() {
        bankController = new BankController(BankIdInternal, new CentralBankConnectionStub(), null);
    }

    @Test
    public void testBankControllerRegisterBank() {
        BankConnectionInfo bankConnectionInfo = new BankConnectionInfo(BankIdInternal, "URL");
        boolean result = bankController.registerBank(bankConnectionInfo);
        assertTrue(result);
    }
}
