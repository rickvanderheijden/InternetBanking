package componenttest;

import com.ark.bank.IBankForClientSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utilities.BankUtilities;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import static org.junit.Assert.assertFalse;

public class TestBankForClient {
    private static IBankForClientSession bank;
    private static BankUtilities utilities;

    @BeforeClass
    public static void setUpClass() throws IOException, NotBoundException {
        utilities = new BankUtilities();
        bank = utilities.startBankForClient();
    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopBank();
    }

    @Test
    public void testExecuteTransactionNull() throws RemoteException {
        boolean result = bank.executeTransaction(null);
        assertFalse(result);
    }

}
