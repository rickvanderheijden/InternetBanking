package integrationtest;

import com.ark.BankAccount;
import com.ark.BankTransaction;
import com.ark.Customer;
import com.ark.bank.DatabaseController;
import com.ark.bank.IBankAccount;
import com.ark.bank.IDatabaseController;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.*;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


/**
 * @author Koen Sengers
 */
public class TestDatabaseController {

    static private final String Name = "John";
    static private final String Residence = "Winterfell";
    static private final String Password = "Ghost";
    static private final String BankAccountNumberRABO = "RABO1234567890";
    static private final String BankAccountNumberSNSB = "SNSB1234567890";

    private IDatabaseController databaseController;
    private Customer customer;

    @Before
    public void setUp() {
        databaseController = new DatabaseController("TEST");
        databaseController.connectToDatabase();

        assertTrue(databaseController.deleteAll());
        customer = new Customer(Name, Residence, Password);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testPersistNull() {
        boolean result = databaseController.persist(null);
        assertFalse(result);
    }

    @Test
    public void testPersistString() {
        boolean result = databaseController.persist("TestString");
        assertFalse(result);
    }

    @Test
    public void testPersistCustomer() {
        boolean result = databaseController.persist(customer);
        assertTrue(result);
    }

    @Test
    public void testPersistBankAccountWithCustomerNotYetPersisted(){
        BankAccount bankAccount = new BankAccount(customer, BankAccountNumberRABO);
        boolean result = databaseController.persist(bankAccount);
        assertTrue(result);
    }

    @Test
    public void testPersistBankAccountWithCustomerAlreadyPersisted(){
        databaseController.persist(customer);
        BankAccount bankAccount = new BankAccount(customer, BankAccountNumberRABO);
        boolean result = databaseController.persist(bankAccount);
        assertTrue(result);
    }


    @Test
    public void testPersistTransaction(){
        long amount = 15;
        String description = "Test transaction";
        String accountTo = "RABO987654321";

        BankTransaction transaction = new BankTransaction(amount, description, BankAccountNumberRABO, accountTo);
        boolean result = databaseController.persist(transaction);
        assertTrue(result);
    }

    @Test
    public void testUpdateBankAccount(){
        BankAccount bankAccount = new BankAccount(customer, BankAccountNumberRABO);
        BankAccount bankAccount2 = new BankAccount(customer, "RABO0987654321");
        long creditLimit = bankAccount.getCreditLimit() + 1000;
        boolean result = databaseController.persist(bankAccount);
        assertTrue(result);

        result = databaseController.persist(bankAccount2);
        assertTrue(result);

        bankAccount.setCreditLimit(creditLimit);
        result = databaseController.persist(bankAccount);
        List<IBankAccount> bankAccounts = databaseController.getBankAccounts(customer);

        assertTrue(result);
        assertEquals(2, bankAccounts.size());
        assertEquals(creditLimit, bankAccounts.get(0).getCreditLimit());
    }

    @Test
    public void testPersistBankAccountFailure(){
        BankAccount bankAccount = new BankAccount(customer, BankAccountNumberRABO);
        BankAccount bankAccount2 = new BankAccount(customer, BankAccountNumberRABO);
        boolean result = databaseController.persist(bankAccount);
        assertTrue(result);

        result = databaseController.persist(bankAccount2);
        assertFalse(result);
    }

    @Test
    public void testDelete() {
        boolean result = databaseController.persist(customer);
        assertTrue(result);

        result = databaseController.delete(customer);
        assertTrue(result);

        customer = databaseController.getCustomer(Name, Residence);
        assertNull(customer);
    }

    @Test
    public void testDeleteCustomer(){
        databaseController.persist(customer);
        boolean result = databaseController.deleteCustomerByNameAndResidence(customer.getName(), customer.getResidence());
        assertTrue(result);
    }

    @Test
    public void testGetCustomer(){
        Customer customer2 = new Customer("Ned", Residence, "Catlyn");
        databaseController.persist(customer);
        databaseController.persist(customer2);

        customer = databaseController.getCustomer(Name, Residence);
        customer2 = databaseController.getCustomer("Ned", Residence);

        assertEquals(Name, customer.getName());
        assertEquals(Residence, customer.getResidence());
        assertTrue(customer.isPasswordValid(Password));

        assertEquals("Ned", customer2.getName());
        assertEquals(Residence, customer2.getResidence());
        assertTrue(customer2.isPasswordValid("Catlyn"));
    }

    @Test
    public void testGetCustomerNull(){
        customer = databaseController.getCustomer(Name, Residence);
        assertNull(customer);
    }

    @Test
    public void testGetBankAccounts(){
        String bankAccountNumber2 = "RABO987654321";
        BankAccount bankAccount1 = new BankAccount(customer, BankAccountNumberRABO);
        BankAccount bankAccount2 = new BankAccount(customer, bankAccountNumber2);

        databaseController.persist(bankAccount1);
        databaseController.persist(bankAccount2);

        List<IBankAccount> bankAccounts = databaseController.getBankAccounts(customer);

        assertEquals(bankAccount1, bankAccounts.get(0));
        assertEquals(bankAccount2, bankAccounts.get(1));
    }

    @Test
    public void testGetBankAccount(){
        String bankAccountNumber2 = "RABO987654321";
        BankAccount bankAccount1 = new BankAccount(customer, BankAccountNumberRABO);
        BankAccount bankAccount2 = new BankAccount(customer, bankAccountNumber2);
        bankAccount1.increaseBalance(100);

        databaseController.persist(bankAccount1);
        databaseController.persist(bankAccount2);

        IBankAccount result = databaseController.getBankAccount(BankAccountNumberRABO);

        assertEquals(bankAccount1, result);
        assertThat(bankAccount2, not(result));
    }

    @Test
    public void testGetBankAccountsNotExistsForCustomer(){
        databaseController.persist(customer);
        List<IBankAccount> bankAccounts = databaseController.getBankAccounts(customer);
        assertEquals(0, bankAccounts.size());
    }

    @Test
    public void testGetBankAccountNotExists(){
        IBankAccount bankAccount = databaseController.getBankAccount(BankAccountNumberRABO);
        assertNull(bankAccount);
    }

    @Test
    public void testGetBankTransaction(){
        long amount = 15;
        String description = "Test transaction";
        String accountTo1 = "RABO987654321";

        String accountFrom2 = "RABO000000000";

        BankTransaction transaction1 = new BankTransaction(amount, description, BankAccountNumberRABO, accountTo1);
        BankTransaction transaction2 = new BankTransaction(amount, description, accountFrom2, BankAccountNumberSNSB);

        databaseController.persist(transaction1);
        databaseController.persist(transaction2);

        List<BankTransaction> transactions1 = databaseController.getBankTransactions(BankAccountNumberRABO);
        assertNotNull(transactions1);
        assertEquals(1, transactions1.size());
        assertEquals(transaction1, transactions1.get(0));


        BankTransaction transaction3 = new BankTransaction(amount, description, BankAccountNumberSNSB, BankAccountNumberRABO);
        databaseController.persist(transaction3);

        List<BankTransaction> transactions2 = databaseController.getBankTransactions(BankAccountNumberRABO);
        assertNotNull(transactions2);
        assertEquals(2, transactions2.size());
        assertEquals(transaction1, transactions2.get(0));
        assertEquals(transaction3, transactions2.get(1));
    }

    @Test
    public void testGetBankTransactionsNotExists(){
        List<BankTransaction> transactions = databaseController.getBankTransactions(BankAccountNumberRABO);
        assertNotNull(transactions);
        assertEquals(0, transactions.size());
    }

    @Test
    public void testTransactionExists(){
        long amount = 15;
        String description = "Test transaction";
        String accountTo1 = "RABO987654321";

        BankTransaction transaction1 = new BankTransaction(amount, description, BankAccountNumberRABO, accountTo1);
        databaseController.persist(transaction1);

        boolean result = databaseController.transactionExists(transaction1);
        assertTrue(result);

        databaseController.delete(transaction1);

        result = databaseController.transactionExists(transaction1);
        assertFalse(result);
    }

    @Test
    public void testGetAllCustomers(){
        Customer customer2 = new Customer("Rick", "Beek en Donk", "Hates Mac");
        Customer customer3 = new Customer("Arthur", "Zaltbommel", "Also hates Mac");
        databaseController.persist(customer);
        databaseController.persist(customer2);
        databaseController.persist(customer3);

        List<Customer> customers = databaseController.getAllCustomers();
        assertNotNull(customers);
        assertEquals(3, customers.size());
        assertEquals(customer, customers.get(0));
        assertEquals(customer2, customers.get(1));
        assertEquals(customer3, customers.get(2));
    }

    @Test
    public void testGetAllBankAccounts(){
        Customer customer2 = new Customer("Rick", "Beek en Donk", "Hates Mac");
        Customer customer3 = new Customer("Arthur", "Zaltbommel", "Also hates Mac");
        databaseController.persist(customer);
        databaseController.persist(customer2);
        databaseController.persist(customer3);

        String bankId2 = "RABO987654321";
        String bankId3 = "RABO5647382910";
        String bankId4 = "RABO1029384756";

        IBankAccount bankAccount1 = new BankAccount(customer, BankAccountNumberRABO);
        IBankAccount bankAccount2 = new BankAccount(customer2, bankId2);
        IBankAccount bankAccount3 = new BankAccount(customer3, bankId3);
        IBankAccount bankAccount4 = new BankAccount(customer, bankId4);

        databaseController.persist(bankAccount1);
        databaseController.persist(bankAccount2);
        databaseController.persist(bankAccount3);
        databaseController.persist(bankAccount4);

        List<IBankAccount> bankAccounts = databaseController.getAllBankAccounts();
        assertNotNull(bankAccounts);
        assertEquals(4, bankAccounts.size());
        assertEquals(bankAccount1, bankAccounts.get(0));
        assertEquals(bankAccount2, bankAccounts.get(1));
        assertEquals(bankAccount3, bankAccounts.get(2));
        assertEquals(bankAccount4, bankAccounts.get(3));
    }

    @Test
    public void testGetAllBankTransactions(){
        String description = "Test1";
        String accountTo = "RABO987654321";
        BankTransaction transaction1 = new BankTransaction(15, description, BankAccountNumberRABO, accountTo);
        BankTransaction transaction2 = new BankTransaction(15, description, BankAccountNumberRABO, accountTo);
        BankTransaction transaction3 = new BankTransaction(15, description, BankAccountNumberRABO, accountTo);
        BankTransaction transaction4 = new BankTransaction(15, description, BankAccountNumberRABO, accountTo);
        BankTransaction transaction5 = new BankTransaction(15, description, BankAccountNumberRABO, accountTo);
        databaseController.persist(transaction1);
        databaseController.persist(transaction2);
        databaseController.persist(transaction3);
        databaseController.persist(transaction4);
        databaseController.persist(transaction5);

        List<BankTransaction> bankTransactions = databaseController.getAllBankTransactions();
        assertNotNull(bankTransactions);
        assertEquals(5, bankTransactions.size());
        assertEquals(transaction1, bankTransactions.get(0));
        assertEquals(transaction2, bankTransactions.get(1));
        assertEquals(transaction3, bankTransactions.get(2));
        assertEquals(transaction4, bankTransactions.get(3));
        assertEquals(transaction5, bankTransactions.get(4));
    }

    @Test
    public void testMultiThreading() throws InterruptedException {
        String description = "Test1";
        String accountTo = "RABO987654321";

        Thread threadOne = new Thread(() -> {
            long amount = 1;
            for (int i = 0; i < 1000; i++) {
                BankTransaction transaction1 = new BankTransaction(amount, description, BankAccountNumberRABO, accountTo);
                amount++;
                databaseController.persist(transaction1);
            }
        });

        Thread threadTwo = new Thread(() -> {
            long amount = 1;
            for (int i = 0; i < 1000; i++) {
                BankTransaction transaction2 = new BankTransaction(amount, description, BankAccountNumberRABO, accountTo);
                amount++;
                databaseController.persist(transaction2);
            }
        });

        Thread threadThree = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                databaseController.getBankTransactions(accountTo);
            }
        });

        Thread threadFour = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                databaseController.getBankTransactions(BankAccountNumberRABO);
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
    }
}