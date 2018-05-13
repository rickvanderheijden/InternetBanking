package unittest;

import com.ark.centralbank.BankConnectionInfo;
import com.ark.centralbank.CentralBank;
import com.ark.centralbank.ICentralBankRegister;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Rick van der Heijden
 */
public class TestCentralBankRegister {

    private static ICentralBankRegister centralBank;

    @BeforeClass
    public static void setUpClass() {
        centralBank = new CentralBank();
    }

    @AfterClass
    public static void tearDownClass() {
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
