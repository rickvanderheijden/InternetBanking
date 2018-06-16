package componenttest;

import com.ark.centralbank.ICentralBankTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import testutilities.CentralBankUtilities;

import java.io.IOException;

/**
 * @author Rick van der Heijden
 */
class TestCentralBankTransaction {
    private static ICentralBankTransaction centralBank;
    private static CentralBankUtilities utilities;

    @BeforeClass
    public static void setUpClass() throws IOException {
        utilities = new CentralBankUtilities();
        utilities.startCentralBank("localhost");
        centralBank = utilities.getCentralBankTransaction();
    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopCentralBank();
    }

    //TODO: Add useful tests
}
