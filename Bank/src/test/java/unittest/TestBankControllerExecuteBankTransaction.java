package unittest;

import com.ark.BankTransaction;
import com.ark.Customer;
import com.ark.bank.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import unittest.stubs.CentralBankConnectionStub;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestBankControllerExecuteBankTransaction {

    private static final String Name = "Name";
    private static final String Password = "Password";
    private static final String Residence = "Residence";
    private static final String BankIdInternal = "TEST";
    private final CentralBankConnectionStub centralBankConnectionStub = new CentralBankConnectionStub();
    private IBankController bankController;
    private String sessionKeyOne;
    private String sessionKeyTwo;
    private String sessionKeyThree;
    private Customer customerOne;
    private Customer customerTwo;
    private Customer customerThree;

    @Before
    public void setUp() {
        bankController = new BankController(BankIdInternal, centralBankConnectionStub, new DatabaseController(BankIdInternal));
        sessionKeyOne = "";
        sessionKeyTwo = "";
        sessionKeyThree = "";
        customerOne = null;
        customerTwo = null;
        customerThree = null;
    }

    @After
    public void tearDown() {
        bankController = null;
    }

    @Test
    public void testExecuteTransactionNull() {
        createCustomerAndLogin(1);
        boolean result = bankController.executeTransaction(sessionKeyOne, null);
        assertFalse(result);
    }

    @Test
    public void testExecuteTransactionAllValuesNull() {
        createCustomerAndLogin(1);
        BankTransaction bankTransaction = new BankTransaction();
        boolean result = bankController.executeTransaction(sessionKeyOne, bankTransaction);
        assertFalse(result);
    }


    @Test
    public void testExecuteTransactionAccountToNull() {
        createCustomerAndLogin(1);
        BankTransaction bankTransaction = new BankTransaction();
        setTransactionValues(bankTransaction, 2295, "Description", "AccountFrom", null);
        boolean result = bankController.executeTransaction(sessionKeyOne, bankTransaction);
        List<BankTransaction> transactionsFrom = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountFrom());
        assertFalse(result);
        assertEquals(0, transactionsFrom.size());
    }

    @Test
    public void testExecuteTransactionDescriptionNull() {
        createCustomerAndLogin(1);
        BankTransaction bankTransaction = new BankTransaction();
        setTransactionValues(bankTransaction, 2295, null, "AccountFrom", "AccountTo");
        boolean result = bankController.executeTransaction(sessionKeyOne, bankTransaction);
        List<BankTransaction> transactionsFrom = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountFrom());
        List<BankTransaction> transactionsTo = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountTo());
        assertFalse(result);
        assertEquals(0, transactionsFrom.size());
        assertEquals(0, transactionsTo.size());
    }

    @Test
    public void testExecuteTransactionAmountNull() {
        BankTransaction bankTransaction = new BankTransaction();
        setTransactionValues(bankTransaction, 0, "Description", "AccountFrom", "AccountTo");
        boolean result = bankController.executeTransaction(sessionKeyOne, bankTransaction);
        List<BankTransaction> transactionsFrom = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountFrom());
        List<BankTransaction> transactionsTo = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountTo());
        assertFalse(result);
        assertEquals(0, transactionsFrom.size());
        assertEquals(0, transactionsTo.size());
    }

    @Test
    public void testExecuteTransactionValidValuesButWrongSessionKey() {
        createCustomerAndLogin(2);

        IBankAccount bankAccountOne   = bankController.createBankAccount(sessionKeyOne,   customerOne);
        IBankAccount bankAccountTwo   = bankController.createBankAccount(sessionKeyTwo,   customerTwo);

        BankTransaction bankTransaction = new BankTransaction(2100, "This is a test bankTransaction", bankAccountOne.getNumber(), bankAccountTwo.getNumber());
        boolean result = bankController.executeTransaction(sessionKeyTwo, bankTransaction);
        List<BankTransaction> transactionsFrom = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountFrom());
        List<BankTransaction> transactionsTo = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountTo());
        assertFalse(result);
        assertEquals(0, bankAccountOne.getBalance());
        assertEquals(0, bankAccountTwo.getBalance());
        assertEquals(0, transactionsFrom.size());
        assertEquals(0, transactionsTo.size());
    }

    @Test
    public void testExecuteTransactionSameCustomerDifferentBankAccount() {
        createCustomerAndLogin(1);

        IBankAccount bankAccountOne   = bankController.createBankAccount(sessionKeyOne, customerOne);
        IBankAccount bankAccountTwo   = bankController.createBankAccount(sessionKeyOne, customerOne);

        BankTransaction bankTransaction = new BankTransaction(2100, "This is a test bankTransaction", bankAccountOne.getNumber(), bankAccountTwo.getNumber());
        boolean result = bankController.executeTransaction(sessionKeyOne, bankTransaction);
        List<BankTransaction> transactionsFrom = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountFrom());
        List<BankTransaction> transactionsTo = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountTo());
        assertTrue(result);
        assertEquals(-2100, bankAccountOne.getBalance());
        assertEquals( 2100, bankAccountTwo.getBalance());
        assertEquals(1, transactionsFrom.size());
        assertEquals(1, transactionsTo.size());
    }

    @Test
    public void testExecuteTransactionSameCustomerSameBankAccount() {
        createCustomerAndLogin(1);
        IBankAccount bankAccountOne = bankController.createBankAccount(sessionKeyOne, customerOne);

        BankTransaction bankTransaction = new BankTransaction(2100, "This is a test bankTransaction", bankAccountOne.getNumber(), bankAccountOne.getNumber());
        boolean result = bankController.executeTransaction(sessionKeyOne, bankTransaction);
        List<BankTransaction> transactionsFrom = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountFrom());
        List<BankTransaction> transactionsTo = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountTo());
        assertFalse(result);
        assertEquals( 0, bankAccountOne.getBalance());
        assertEquals(0, transactionsFrom.size());
        assertEquals(0, transactionsTo.size());
    }

    @Test
    public void testExecuteTransactionLocalFromOtherTo() {
        createCustomerAndLogin(1);
        IBankAccount bankAccountOne = bankController.createBankAccount(sessionKeyOne, customerOne);

        BankTransaction bankTransaction = new BankTransaction(2100, "This is a test bankTransaction", bankAccountOne.getNumber(), "OtherBank");
        boolean result = bankController.executeTransaction(sessionKeyOne, bankTransaction);
        List<BankTransaction> transactionsFrom = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountFrom());
        assertTrue(result);
        assertEquals(-2100, bankAccountOne.getBalance());
        assertEquals(    1, transactionsFrom.size());
    }

    @Test
    public void testExecuteTransactionLocalToOtherFrom() {
        createCustomerAndLogin(1);
        IBankAccount bankAccountOne = bankController.createBankAccount(sessionKeyOne, customerOne);

        BankTransaction bankTransaction = new BankTransaction(2100, "This is a test bankTransaction", "OtherBank", bankAccountOne.getNumber());
        boolean result = bankController.executeTransaction(bankTransaction);
        List<BankTransaction> transactionsTo = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountTo());
        assertTrue(result);
        assertEquals(2100, bankAccountOne.getBalance());
        assertEquals(   1, transactionsTo.size());
    }

    @Test
    public void testExecuteTransactionValidValues() {
        createCustomerAndLogin(2);

        IBankAccount bankAccountOne   = bankController.createBankAccount(sessionKeyOne,   customerOne);
        IBankAccount bankAccountTwo   = bankController.createBankAccount(sessionKeyTwo,   customerTwo);

        BankTransaction bankTransaction = new BankTransaction(2100, "This is a test bankTransaction", bankAccountOne.getNumber(), bankAccountTwo.getNumber());
        boolean result = bankController.executeTransaction(sessionKeyOne, bankTransaction);
        List<BankTransaction> transactionsFrom = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountFrom());
        List<BankTransaction> transactionsTo = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountTo());
        assertTrue(result);
        assertEquals(-2100, bankAccountOne.getBalance());
        assertEquals( 2100, bankAccountTwo.getBalance());
        assertEquals(1, transactionsFrom.size());
        assertEquals(1, transactionsTo.size());
    }

    @Test
    public void testExecuteTransactionTwice() {
        createCustomerAndLogin(2);

        IBankAccount bankAccountOne   = bankController.createBankAccount(sessionKeyOne,   customerOne);
        IBankAccount bankAccountTwo   = bankController.createBankAccount(sessionKeyTwo,   customerTwo);

        BankTransaction bankTransaction = new BankTransaction(2100, "This is a test bankTransaction", bankAccountOne.getNumber(), bankAccountTwo.getNumber());
        boolean result = bankController.executeTransaction(sessionKeyOne, bankTransaction);
        List<BankTransaction> transactionsFrom = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountFrom());
        List<BankTransaction> transactionsTo = bankController.getTransactions(sessionKeyOne, bankTransaction.getAccountTo());
        assertTrue(result);
        assertEquals(-2100, bankAccountOne.getBalance());
        assertEquals( 2100, bankAccountTwo.getBalance());
        assertEquals(1, transactionsFrom.size());
        assertEquals(1, transactionsTo.size());

        result = bankController.executeTransaction(sessionKeyOne, bankTransaction);
        assertFalse(result);
        assertEquals(-2100, bankAccountOne.getBalance());
        assertEquals( 2100, bankAccountTwo.getBalance());
        assertEquals(1, transactionsFrom.size());
        assertEquals(1, transactionsTo.size());
    }

    @Test
    public void testExecuteTransactionValidValuesMultiThreading() throws InterruptedException {
        createCustomerAndLogin(3);

        IBankAccount bankAccountOne   = bankController.createBankAccount(sessionKeyOne,   customerOne);
        IBankAccount bankAccountTwo   = bankController.createBankAccount(sessionKeyTwo,   customerTwo);
        IBankAccount bankAccountThree = bankController.createBankAccount(sessionKeyThree, customerThree);

        Thread threadOne   = new Thread(() -> executeAndCheckTransaction(sessionKeyOne,   bankAccountOne.getNumber(),   bankAccountThree.getNumber(), 1000));
        Thread threadTwo   = new Thread(() -> executeAndCheckTransaction(sessionKeyTwo,   bankAccountTwo.getNumber(),   bankAccountThree.getNumber(), 1000));
        Thread threadThree = new Thread(() -> executeAndCheckTransaction(sessionKeyThree, bankAccountThree.getNumber(), bankAccountOne.getNumber()  , 1000));
        Thread threadFour  = new Thread(() -> executeAndCheckTransaction(sessionKeyThree, bankAccountThree.getNumber(), bankAccountTwo.getNumber()  , 1000));

        threadOne.start();
        threadTwo.start();
        threadThree.start();
        threadFour.start();

        threadOne.join();
        threadTwo.join();
        threadThree.join();
        threadFour.join();

        List<BankTransaction> transactionsOne   = bankController.getTransactions(sessionKeyOne,   bankAccountOne.getNumber());
        List<BankTransaction> transactionsTwo   = bankController.getTransactions(sessionKeyTwo,   bankAccountTwo.getNumber());
        List<BankTransaction> transactionsThree = bankController.getTransactions(sessionKeyThree, bankAccountThree.getNumber());

        assertEquals( 0, bankAccountOne.getBalance());
        assertEquals( 0, bankAccountTwo.getBalance());
        assertEquals( 0, bankAccountThree.getBalance());

        assertEquals(2000, transactionsOne.size());
        assertEquals(2000, transactionsTwo.size());
        assertEquals(4000, transactionsThree.size());
    }

    @Test
    public void testGetTransactionsSessionKeyValid() {
        createCustomerAndLogin(2);
        IBankAccount bankAccountOne = bankController.createBankAccount(sessionKeyOne,   customerOne);
        IBankAccount bankAccountTwo = bankController.createBankAccount(sessionKeyTwo,   customerTwo);
        executeAndCheckTransaction(sessionKeyOne, bankAccountOne.getNumber(), bankAccountTwo.getNumber(), 1);

        List<BankTransaction> transactionsOne = bankController.getTransactions(sessionKeyOne,   bankAccountOne.getNumber());
        List<BankTransaction> transactionsTwo = bankController.getTransactions(sessionKeyTwo,   bankAccountTwo.getNumber());

        assertEquals(1, transactionsOne.size());
        assertEquals(1, transactionsTwo.size());
    }

    @Test
    public void testGetTransactionsSessionKeyInvalid() {
        createCustomerAndLogin(2);
        IBankAccount bankAccountOne = bankController.createBankAccount(sessionKeyOne,   customerOne);
        IBankAccount bankAccountTwo = bankController.createBankAccount(sessionKeyTwo,   customerTwo);
        executeAndCheckTransaction(sessionKeyOne, bankAccountOne.getNumber(), bankAccountTwo.getNumber(), 1);

        List<BankTransaction> transactionsOne = bankController.getTransactions("Invalid",   bankAccountOne.getNumber());
        assertEquals(0, transactionsOne.size());
    }

    @Test
    public void testGetTransactionsSessionKeyNull() {
        createCustomerAndLogin(2);
        IBankAccount bankAccountOne = bankController.createBankAccount(sessionKeyOne,   customerOne);
        IBankAccount bankAccountTwo = bankController.createBankAccount(sessionKeyTwo,   customerTwo);
        executeAndCheckTransaction(sessionKeyOne, bankAccountOne.getNumber(), bankAccountTwo.getNumber(), 1);

        List<BankTransaction> transactionsOne = bankController.getTransactions(null,   bankAccountOne.getNumber());
        assertEquals(0, transactionsOne.size());
    }

    private void executeAndCheckTransaction(String sessionKey, String bankAccountFrom, String bankAccountTo, int numberOfTransactions) {
        for (int i = 0; i < numberOfTransactions; i++) {
            BankTransaction bankTransaction = new BankTransaction(10, "This is a test bankTransaction", bankAccountFrom, bankAccountTo);
            boolean result = bankController.executeTransaction(sessionKey, bankTransaction);
            assertTrue(result);
        }
    }

    private void createCustomerAndLogin(int numberOfCustomers) {
        if (numberOfCustomers < 0) numberOfCustomers = 0;
        if (numberOfCustomers > 3) numberOfCustomers = 3;

        for (int index = 0; index < numberOfCustomers; index++) {
            String number = Integer.toString(index);
            String name = Name.concat(number);
            String residence = Residence.concat(number);
            String password = Password.concat(number);
            switch (index) {
                case 0:
                    customerOne = bankController.createCustomer(name, residence, password);
                    sessionKeyOne = bankController.login(name, residence, password);
                    break;
                case 1:
                    customerTwo = bankController.createCustomer(name, residence, password);
                    sessionKeyTwo = bankController.login(name, residence, password);
                    break;
                case 2:
                    customerThree = bankController.createCustomer(name, residence, password);
                    sessionKeyThree = bankController.login(name, residence, password);
                    break;
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void setTransactionValues(BankTransaction bankTransaction, long amount, String description, String accountFrom, String accountTo) {
        if (amount > 0)          { bankTransaction.setAmount(amount);           }
        if (description != null) { bankTransaction.setDescription(description); }
        if (accountFrom != null) { bankTransaction.setAccountFrom(accountFrom); }
        if (accountTo   != null) { bankTransaction.setAccountTo(accountTo);     }
    }
}
