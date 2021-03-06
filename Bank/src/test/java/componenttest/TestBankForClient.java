package componenttest;

import com.ark.bank.IBankForClientSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import testutilities.BankUtilities;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import static org.junit.Assert.assertFalse;

/**
 * @author Rick van der Heijden
 */
public class TestBankForClient {
    private static final String BankId = "RABO";
    private static final String URLBase = "http://localhost:1200/";
    private static final String IPAddressCentralBank = "localhost";
    private static IBankForClientSession bank;
    private static BankUtilities utilities;

    @BeforeClass
    public static void setUpClass() throws IOException, NotBoundException {
        utilities = new BankUtilities();
        utilities.startBank(BankId, URLBase, IPAddressCentralBank);
        bank = utilities.getIBankForClient(BankId);
    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopBank(BankId);
    }

    @Test
    public void testExecuteTransactionNull() throws RemoteException {
        //TODO: Fix test with one bank transaction
        boolean result = bank.executeTransaction(null,null);
        assertFalse(result);
    }
}
