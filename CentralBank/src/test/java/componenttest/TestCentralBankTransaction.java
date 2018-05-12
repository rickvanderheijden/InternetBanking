package componenttest;

import com.ark.centralbank.ICentralBankTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import utilities.CentralBankUtilities;
import java.io.IOException;

/**
 * @author Rick van der Heijden
 */
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

    //TODO: Add useful tests
}
