package unittest;

import com.ark.Customer;
import com.ark.bank.*;
import com.ark.bank.IBankController;
import com.ark.Transaction;
import com.ark.bank.IBankAccount;
import com.ark.centralbank.ICentralBankTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestBankControllerExecuteTransaction {

    private static final String Name = "TestName";
    private static final String Password = "TestPassword";
    private static final String Residence = "TestResidence";
    private static final String BankIdInternal = "TEST";
    private IBankController bankController;
    private String sessionKey;

    @Before
    public void setUp() {
        bankController = new BankController(BankIdInternal, null);
    }

    @After
    public void tearDown() {
        bankController = null;
    }

    @Test
    public void testExecuteTransactionNull() {
        createCustomerAndLogin();
        boolean result = bankController.executeTransaction(sessionKey, null);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAllValuesNull() {
        createCustomerAndLogin();
        Transaction transaction = new Transaction(0, null, null, null);
        boolean result = bankController.executeTransaction(sessionKey, transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAccountToNull() {
        createCustomerAndLogin();
        Transaction transaction = new Transaction(2295, "Description", "AccountFrom", null);
        boolean result = bankController.executeTransaction(sessionKey, transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionDescriptionNull() {
        createCustomerAndLogin();
        Transaction transaction = new Transaction(2295, null, "AccountFrom", "AccountTo");
        boolean result = bankController.executeTransaction(sessionKey, transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAmountNull() {
        Transaction transaction = new Transaction(0, "Description", "AccountFrom", "AccountTo");
        boolean result = bankController.executeTransaction(sessionKey, transaction);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionValidValuesButWrongSessionKey() {
        Customer customerOne   = bankController.createCustomer("Name1", "Residence1", "Password1");
        Customer customerTwo   = bankController.createCustomer("Name2", "Residence2", "Password2");

        String sessionKeyOne   = bankController.login("Name1", "Residence1", "Password1");
        String sessionKeyTwo   = bankController.login("Name2", "Residence2", "Password2");

        IBankAccount bankAccountOne   = bankController.createBankAccount(sessionKeyOne,   customerOne);
        IBankAccount bankAccountTwo   = bankController.createBankAccount(sessionKeyTwo,   customerTwo);

        Transaction transaction = new Transaction(2100, "This is a test transaction", bankAccountOne.getNumber(), bankAccountTwo.getNumber());
        boolean result = bankController.executeTransaction(sessionKeyTwo, transaction);
        assertFalse(result);
        assertEquals(    0, bankAccountOne.getBalance());
        assertEquals(    0, bankAccountTwo.getBalance());
    }

    @Test
    public void testExecuteTransactionValidValues() {
        Customer customerOne   = bankController.createCustomer("Name1", "Residence1", "Password1");
        Customer customerTwo   = bankController.createCustomer("Name2", "Residence2", "Password2");

        String sessionKeyOne   = bankController.login("Name1", "Residence1", "Password1");
        String sessionKeyTwo   = bankController.login("Name2", "Residence2", "Password2");

        IBankAccount bankAccountOne   = bankController.createBankAccount(sessionKeyOne,   customerOne);
        IBankAccount bankAccountTwo   = bankController.createBankAccount(sessionKeyTwo,   customerTwo);

        Transaction transaction = new Transaction(2100, "This is a test transaction", bankAccountOne.getNumber(), bankAccountTwo.getNumber());
        boolean result = bankController.executeTransaction(sessionKeyOne, transaction);
        assertTrue(result);
        assertEquals(-2100, bankAccountOne.getBalance());
        assertEquals( 2100, bankAccountTwo.getBalance());
    }

    @Test
    public void testExecuteTransactionValidValuesMultiThreading() throws InterruptedException {
        Customer customerOne   = bankController.createCustomer("Name1", "Residence1", "Password1");
        Customer customerTwo   = bankController.createCustomer("Name2", "Residence2", "Password2");
        Customer customerThree = bankController.createCustomer("Name3", "Residence3", "Password3");

        String sessionKeyOne   = bankController.login("Name1", "Residence1", "Password1");
        String sessionKeyTwo   = bankController.login("Name2", "Residence2", "Password2");
        String sessionKeyThree = bankController.login("Name3", "Residence3", "Password3");

        IBankAccount bankAccountOne   = bankController.createBankAccount(sessionKeyOne,   customerOne);
        IBankAccount bankAccountTwo   = bankController.createBankAccount(sessionKeyTwo,   customerTwo);
        IBankAccount bankAccountThree = bankController.createBankAccount(sessionKeyThree, customerThree);

        Transaction transactionOne   = new Transaction(10, "This is a test transaction", bankAccountOne.getNumber(), bankAccountThree.getNumber());
        Transaction transactionTwo   = new Transaction(10, "This is a test transaction", bankAccountTwo.getNumber(), bankAccountThree.getNumber());
        Transaction transactionThree = new Transaction(10, "This is a test transaction", bankAccountThree.getNumber(), bankAccountOne.getNumber());
        Transaction transactionFour  = new Transaction(10, "This is a test transaction", bankAccountThree.getNumber(), bankAccountTwo.getNumber());

        Thread threadOne = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                bankController.executeTransaction(sessionKeyOne, transactionOne);
            }
        });

        Thread threadTwo = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                bankController.executeTransaction(sessionKeyTwo, transactionTwo);
            }
        });

        Thread threadThree = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                bankController.executeTransaction(sessionKeyThree, transactionThree);
            }
        });

        Thread threadFour = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                bankController.executeTransaction(sessionKeyThree, transactionFour);
            }
        });

        threadOne.start();
        threadTwo.start();
        threadThree.start();
        threadFour.start();

        threadOne.join();
        threadTwo.join();
        threadThree.join();
        threadFour.join();

        assertEquals( 0, bankAccountOne.getBalance());
        assertEquals( 0, bankAccountTwo.getBalance());
        assertEquals( 0, bankAccountThree.getBalance());
    }

    private void createCustomerAndLogin() {
        bankController.createCustomer(Name, Residence, Password);
        sessionKey = bankController.login(Name, Residence, Password);
    }

    //TODO: CHECK IF TRANSACTION EXIST AFTER SUCCESFUL. NOT WHEN NOT SUCCESFUL!!
}
