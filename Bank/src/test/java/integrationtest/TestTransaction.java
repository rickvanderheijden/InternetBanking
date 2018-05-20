package integrationtest;

import com.ark.bank.IBankForClientSession;
import com.ark.centralbank.ICentralBankRegister;
import com.ark.centralbank.ICentralBankTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import testutilities.BankUtilities;

import java.io.IOException;
import java.rmi.NotBoundException;

//TODO: MOVE TO SEPARATE MODULE?

/**
 * @author Rick van der Heijden
 */
public class TestTransaction {
    private static final String BankIdRABO = "RABO";
    private static final String URLBaseRABO = "http://localhost:1200/";
    private static final String BankIdABNA = "ABNA";
    private static final String URLBaseABNA = "http://localhost:1205/";
    private static IBankForClientSession bankForClientSessionOne;
    private static IBankForClientSession bankForClientSessionTwo;
    private static ICentralBankRegister centralBankRegister;
    private static ICentralBankTransaction centralBankTransaction;
    private static BankUtilities utilities;

    @BeforeClass
    public static void setUpClass() throws IOException, NotBoundException {
        utilities = new BankUtilities();
        utilities.startBank(BankIdABNA, URLBaseABNA);
        utilities.startBank(BankIdRABO, URLBaseRABO);
        bankForClientSessionOne = utilities.getIBankForClient(BankIdRABO);
        bankForClientSessionTwo = utilities.getIBankForClient(BankIdABNA);
    }

    @AfterClass
    public static void tearDownClass() {
        utilities.stopBank(BankIdRABO);
        utilities.stopBank(BankIdABNA);
    }

    @Test
    public void testExecuteTransaction() {

    }
}

