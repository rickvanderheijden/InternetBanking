package componenttest;

import com.ark.centralbank.BankConnectionInfo;
import com.ark.centralbank.ICentralBankRegister;
import java.io.IOException;
import org.junit.AfterClass;
import utilities.CentralBankUtilities;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCentralBankRegister {
    private static ICentralBankRegister centralBank;
    private static CentralBankUtilities utilities;

    @BeforeClass
    public static void setUpClass() throws IOException {
        utilities = new CentralBankUtilities();
        centralBank = utilities.startCentralBankRegister();
    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopCentralBank();
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
}
