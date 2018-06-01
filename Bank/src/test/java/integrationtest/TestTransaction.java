package integrationtest;

import com.ark.*;
import com.ark.bank.IBankAccount;
import com.ark.bank.IBankForClientLogin;
import com.ark.bank.IBankForClientSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import testutilities.BankUtilities;
import testutilities.CentralBankUtilities;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;

//TODO: MOVE TO SEPARATE MODULE?

/**
 * @author Rick van der Heijden
 */
public class TestTransaction {
    private enum BankId { RABO, ABNA }
    private static final String BankIdRABO = "RABO";
    private static final String URLBaseRABO = "http://localhost:1200/";
    private static final String BankIdABNA = "ABNA";
    private static final String URLBaseABNA = "http://localhost:1205/";
    private static IBankForClientSession bankForClientSessionABNA;
    private static IBankForClientSession bankForClientSessionRABO;
    private static IBankForClientLogin bankForClientLoginABNA;
    private static IBankForClientLogin bankForClientLoginRABO;
    private static BankUtilities bankUtilities;
    private static CentralBankUtilities centralBankUtilities;
    private static List<Customer> customers;
    private static List<String> sessionKeys;
    private static List<IBankAccount> bankAccounts;

    @BeforeClass
    public static void setUpClass() throws IOException, NotBoundException {
        bankUtilities = new BankUtilities();
        centralBankUtilities = new CentralBankUtilities();

        centralBankUtilities.startCentralBank();
        bankUtilities.startBank(BankIdABNA, URLBaseABNA);
        bankUtilities.startBank(BankIdRABO, URLBaseRABO);

        bankForClientSessionABNA = bankUtilities.getIBankForClient(BankIdABNA);
        bankForClientSessionRABO = bankUtilities.getIBankForClient(BankIdRABO);

        bankForClientLoginABNA = (IBankForClientLogin) bankForClientSessionABNA;
        bankForClientLoginRABO = (IBankForClientLogin) bankForClientSessionRABO;

        customers = new ArrayList<>();
        sessionKeys = new ArrayList<>();
        bankAccounts = new ArrayList<>();
    }

    @AfterClass
    public static void tearDownClass() {
        bankUtilities.stopBank(BankIdRABO);
        bankUtilities.stopBank(BankIdABNA);
        centralBankUtilities.stopCentralBank();
    }

    @Test
    public void testExecuteTransactionDifferentBanks() throws RemoteException {
        int indexABNA = createCustomerAndBankAccount(BankId.ABNA);
        int indexRABO = createCustomerAndBankAccount(BankId.RABO);

        Transaction transaction = new Transaction(2315, "This is a description", bankAccounts.get(indexABNA).getNumber(), bankAccounts.get(indexRABO).getNumber());
        boolean result = bankForClientSessionABNA.executeTransaction(sessionKeys.get(indexABNA), transaction);

        assertTrue(result);

        bankAccounts.set(indexABNA, bankForClientSessionABNA.getBankAccount(sessionKeys.get(indexABNA), bankAccounts.get(indexABNA).getNumber()));
        bankAccounts.set(indexRABO, bankForClientSessionRABO.getBankAccount(sessionKeys.get(indexRABO), bankAccounts.get(indexRABO).getNumber()));
        assertEquals(-2315, bankAccounts.get(indexABNA).getBalance());
        assertEquals(2315, bankAccounts.get(indexRABO).getBalance());
    }

    @Test
    public void testExecuteTransactionSameBanks() throws RemoteException {
        int indexABNAOne = createCustomerAndBankAccount(BankId.ABNA);
        int indexABNATwo = createCustomerAndBankAccount(BankId.ABNA);

        Transaction transaction = new Transaction(2315, "This is a description", bankAccounts.get(indexABNAOne).getNumber(), bankAccounts.get(indexABNATwo).getNumber());
        boolean result = bankForClientSessionABNA.executeTransaction(sessionKeys.get(indexABNAOne), transaction);

        assertTrue(result);

        bankAccounts.set(indexABNAOne, bankForClientSessionABNA.getBankAccount(sessionKeys.get(indexABNAOne), bankAccounts.get(indexABNAOne).getNumber()));
        bankAccounts.set(indexABNATwo, bankForClientSessionABNA.getBankAccount(sessionKeys.get(indexABNATwo), bankAccounts.get(indexABNATwo).getNumber()));
        assertEquals(-2315, bankAccounts.get(indexABNAOne).getBalance());
        assertEquals( 2315, bankAccounts.get(indexABNATwo).getBalance());
    }

    @Test
    public void testExecuteTransactionFromAccountWithNotEnoughCredit() throws RemoteException {
        int indexABNA = createCustomerAndBankAccount(BankId.ABNA);
        int indexRABO = createCustomerAndBankAccount(BankId.RABO);

        long amount = bankAccounts.get(indexABNA).getCreditLimit() + 1;

        Transaction transaction = new Transaction(amount, "This is a description", bankAccounts.get(indexABNA).getNumber(), bankAccounts.get(indexRABO).getNumber());
        boolean result = bankForClientSessionABNA.executeTransaction(sessionKeys.get(indexABNA), transaction);

        assertFalse(result);

        bankAccounts.set(indexABNA, bankForClientSessionABNA.getBankAccount(sessionKeys.get(indexABNA), bankAccounts.get(indexABNA).getNumber()));
        bankAccounts.set(indexRABO, bankForClientSessionRABO.getBankAccount(sessionKeys.get(indexRABO), bankAccounts.get(indexRABO).getNumber()));
        assertEquals(0, bankAccounts.get(indexABNA).getBalance());
        assertEquals(0, bankAccounts.get(indexRABO).getBalance());
    }

    @Test
    public void testExecuteTransactionToUnavailableAccount() throws RemoteException {
        int indexABNA = createCustomerAndBankAccount(BankId.ABNA);
        int indexRABO = createCustomerAndBankAccount(BankId.RABO);

        Transaction transaction = new Transaction(1500, "This is a description", bankAccounts.get(indexABNA).getNumber(), "INVALIDACCOUNT");
        boolean result = bankForClientSessionABNA.executeTransaction(sessionKeys.get(indexABNA), transaction);

        assertFalse(result);

        bankAccounts.set(indexABNA, bankForClientSessionABNA.getBankAccount(sessionKeys.get(indexABNA), bankAccounts.get(indexABNA).getNumber()));
        bankAccounts.set(indexRABO, bankForClientSessionRABO.getBankAccount(sessionKeys.get(indexRABO), bankAccounts.get(indexRABO).getNumber()));
        assertEquals(0, bankAccounts.get(indexABNA).getBalance());
        assertEquals(0, bankAccounts.get(indexRABO).getBalance());
    }

    private int createCustomerAndBankAccount(BankId bankId) throws RemoteException {

        IBankForClientLogin bankForClientLogin = null;
        IBankForClientSession bankForClientSession = null;

        switch (bankId) {
            case ABNA:
                bankForClientLogin = bankForClientLoginABNA;
                bankForClientSession = bankForClientSessionABNA;
                break;
            case RABO:
                bankForClientLogin = bankForClientLoginRABO;
                bankForClientSession = bankForClientSessionRABO;
                break;
        }

        int index = customers.size();
        Customer customer = bankForClientSession.createCustomer("CustomerName" + index, "CustomerResidence" + index, "CustomerPassword" + index);
        String sessionKey = bankForClientLogin.login("CustomerName" + index, "CustomerResidence" + index, "CustomerPassword" + index);
        IBankAccount bankAccount = bankForClientSession.createBankAccount(sessionKey, customer);

        customers.add(customer);
        sessionKeys.add(sessionKey);
        bankAccounts.add(bankAccount);

        return index;
    }
}

