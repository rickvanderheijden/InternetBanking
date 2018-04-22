package componenttest;

import com.ark.centralbank.ICentralBankTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utilities.CentralBankUtilities;

import java.io.IOException;

public class TestCentralBankTransaction {
    private static ICentralBankTransaction centralBank;
    private static CentralBankUtilities utilities;

    @BeforeClass
    public static void setUpClass() throws IOException {
        utilities = new CentralBankUtilities();
        centralBank = utilities.startCentralBankTransaction();
    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopCentralBank();
    }

    @Test
    public void testExecuteTransaction() {
        centralBank.executeTransaction();
    }
}