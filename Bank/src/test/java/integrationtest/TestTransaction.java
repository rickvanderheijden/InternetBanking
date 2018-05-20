package integrationtest;

import com.ark.bank.BankAccount;
import com.ark.bank.Customer;
import com.ark.bank.IBankForClientLogin;
import com.ark.bank.IBankForClientSession;
import com.ark.centralbank.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import testutilities.BankUtilities;
import testutilities.CentralBankUtilities;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

//TODO: MOVE TO SEPARATE MODULE?

/**
 * @author Rick van der Heijden
 */
public class TestTransaction {
    private static final String BankIdRABO = "RABO";
    private static final String URLBaseRABO = "http://localhost:1200/";
    private static final String BankIdABNA = "ABNA";
    private static final String URLBaseABNA = "http://localhost:1205/";
    private static IBankForClientSession bankForClientSessionABNA;
    private static IBankForClientSession bankForClientSessionRABO;
    private static IBankForClientLogin bankForClientLoginABNA;
    private static IBankForClientLogin bankForClientLoginRABO;
    private static BankUtilities bankUtilities;

    @BeforeClass
    public static void setUpClass() throws IOException, NotBoundException {
        bankUtilities = new BankUtilities();
        CentralBankUtilities centralBankUtilities = new CentralBankUtilities();

        centralBankUtilities.startCentralBank();
        bankUtilities.startBank(BankIdABNA, URLBaseABNA);
        bankUtilities.startBank(BankIdRABO, URLBaseRABO);

        bankForClientSessionABNA = bankUtilities.getIBankForClient(BankIdABNA);
        bankForClientSessionRABO = bankUtilities.getIBankForClient(BankIdRABO);

        bankForClientLoginABNA = (IBankForClientLogin) bankForClientSessionABNA;
        bankForClientLoginRABO = (IBankForClientLogin) bankForClientSessionRABO;
    }

    @AfterClass
    public static void tearDownClass() {
        bankUtilities.stopBank(BankIdRABO);
        bankUtilities.stopBank(BankIdABNA);
    }

    @Test
    public void testExecuteTransaction() throws RemoteException {
        Customer customerABNA = bankForClientSessionABNA.createCustomer("CustomerNameABNA", "CustomerResidenceABNA", "CustomerPasswordABNA");
        String sessionIdABNA = bankForClientLoginABNA.login("CustomerNameABNA", "CustomerResidenceABNA", "CustomerPasswordABNA");
        BankAccount bankAccountABNA = bankForClientSessionABNA.createBankAccount(sessionIdABNA, customerABNA);

        Customer customerRABO = bankForClientSessionRABO.createCustomer("CustomerNameRABO", "CustomerResidenceRABO", "CustomerPasswordRABO");
        String sessionIdRABO = bankForClientLoginRABO.login("CustomerNameRABO", "CustomerResidenceRABO", "CustomerPasswordRABO");
        BankAccount bankAccountRABO = bankForClientSessionRABO.createBankAccount(sessionIdRABO, customerRABO);

        Transaction transaction = new Transaction(23.15, "This is a description", bankAccountABNA.getNumber(), bankAccountRABO.getNumber());
        boolean result = bankForClientSessionABNA.executeTransaction(sessionIdABNA, transaction);

        assertTrue(result);

        bankAccountABNA = bankForClientSessionABNA.getBankAccount(sessionIdABNA, bankAccountABNA.getNumber());
        bankAccountRABO = bankForClientSessionRABO.getBankAccount(sessionIdRABO, bankAccountRABO.getNumber());
        assertEquals(-23.15, bankAccountABNA.getBalance());
        assertEquals( 23.15, bankAccountRABO.getBalance());
    }
}

