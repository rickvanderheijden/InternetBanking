package componenttest;

import java.io.IOException;

import com.ark.centralbank.IBankForCentralBank;
import org.junit.AfterClass;
import utilities.BankUtilities;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCentralBankRegister {
    private static IBankForCentralBank bank;
    private static BankUtilities utilities;

    @BeforeClass
    public static void setUpClass() throws IOException {
        utilities = new BankUtilities();
        bank = utilities.startBankForCentralBank();
    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopBank();
    }

    @Test
    public void test() {
        //assertFalse(result);
    }
}
