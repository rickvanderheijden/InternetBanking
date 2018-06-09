package unittest;

import com.ark.BankConnectionInfo;
import com.ark.centralbank.CentralBank;
import com.ark.centralbank.ICentralBankRegister;
import org.junit.*;
import unittest.stubs.BankConnectionStub;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Rick van der Heijden
 */
public class TestCentralBankRegister {

    private static ICentralBankRegister centralBank;

    @Before
    public void setUp() {
        centralBank = new CentralBank(new BankConnectionStub());
    }

    @After
    public void tearDown() {
        centralBank = null;
    }

    @Test
    public void testRegisterBankConnectionInfoNull() {
        boolean result = centralBank.registerBank(null);
        assertFalse(result);
    }

    @Test
    public void testRegisterBankConnectionInfoBankIdNull() {
        BankConnectionInfo bankConnectionInfo = new BankConnectionInfo(null, "ThisIsAnUrl");
        boolean result = centralBank.registerBank(bankConnectionInfo);
        assertFalse(result);
    }

    @Test
    public void testRegisterBankConnectionInfoURLNull() {
        BankConnectionInfo bankConnectionInfo = new BankConnectionInfo("BANK", null);
        boolean result = centralBank.registerBank(bankConnectionInfo);
        assertFalse(result);
    }

    @Test
    public void testRegisterBankValid() {
        BankConnectionInfo bankConnectionInfo = new BankConnectionInfo("BANK1", "http://localhost:1234/");
        boolean result = centralBank.registerBank(bankConnectionInfo);
        assertTrue(result);
    }

    @Test
    public void testRegisterBankAlreadyExists() {
        BankConnectionInfo bankConnectionInfo = new BankConnectionInfo("BANK2", "http://localhost:1234/");
        centralBank.registerBank(bankConnectionInfo);

        boolean result = centralBank.registerBank(bankConnectionInfo);
        assertFalse(result);
    }

    @Test
    public void testUnregisterBankNoBankConnection() {
        centralBank = new CentralBank(null);
        boolean result = centralBank.unregisterBank("BANK3");
        assertFalse(result);
    }

    @Test
    public void testUnregisterBankNotRegistered() {
        boolean result = centralBank.unregisterBank("BANK3");
        assertFalse(result);
    }

    @Test
    public void testUnregisterBankRegistered() {
        BankConnectionInfo bankConnectionInfo = new BankConnectionInfo("BANK4", "http://localhost:1200/");
        centralBank.registerBank(bankConnectionInfo);

        boolean result = centralBank.unregisterBank(bankConnectionInfo.getBankId());
        assertTrue(result);
    }
}
