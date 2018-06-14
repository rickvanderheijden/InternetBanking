package unittest;

import com.ark.BankAccount;
import com.ark.BankTransaction;
import com.ark.Customer;
import com.ark.bank.DatabaseController;
import org.hibernate.service.spi.ServiceException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLDataException;
import java.util.List;

import static junit.framework.TestCase.*;


/**
 * @author Koen Sengers
 */
public class TestDatabaseController {

    DatabaseController databaseController;
    Customer customer1;

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

        BankTransaction transaction1 = new BankTransaction(amount, description, accountFrom, accountTo);
        boolean result = databaseController.persist(transaction1);
        assertTrue(result);
    }

    @Test
    public void testUpdateBankAccount(){
        databaseController.persist(customer1);
        String bankId = "RABO123456789";
        Customer customer2 = databaseController.getPersistCustomer("John", "Winterfell");

        BankAccount transaction1 = new BankAccount(customer2, bankId);

        assertEquals(10000, transaction1.getCreditLimit());

        boolean result1 = databaseController.persist(transaction1);
        assertTrue(result1);

        transaction1.setCreditLimit(9000);
        boolean result2 = databaseController.persist(transaction1);

        List<BankAccount> bankAccounts = databaseController.getPersistBankaccounts(customer2);
        assertTrue(result2);
        assertEquals(BankAccount.class, bankAccounts.get(0).getClass());
        assertEquals(1, bankAccounts.size());
        assertEquals(9000, bankAccounts.get(0).getCreditLimit());
    }

    @Test
    public void testDelete() {
        boolean result = databaseController.persist(customer1);

        assertTrue(result);

        result = databaseController.delete(customer);
        assertTrue(result);

        customer = databaseController.getCustomer(Name, Residence);
        assertNull(customer);
    }

    @Test
    public void testGetPersistCustomer(){
        Customer customer2 = new Customer("Ned", "Winterfell", "Catlyn");
        boolean result1 = databaseController.persist(customer1);
        boolean result2 = databaseController.persist(customer2);

        assertTrue(result1);
        assertTrue(result2);

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

        databaseController.persist(transaction1);
        databaseController.persist(transaction2);

        List<BankAccount> bankAccounts = databaseController.getPersistBankaccounts(customer1);

        assertEquals(BankAccount.class, bankAccounts.get(0).getClass());
        assertEquals("RABO123456789", bankAccounts.get(0).getNumber());
        assertEquals("RABO987654321", bankAccounts.get(1).getNumber());
    }

    @Test
    public void testGetBankAccount(){
        String bankAccountNumber2 = "RABO987654321";
        BankAccount bankAccount1 = new BankAccount(customer, BankAccountNumberRABO);
        BankAccount bankAccount2 = new BankAccount(customer, bankAccountNumber2);
        bankAccount1.increaseBalance(100);

        databaseController.persist(transaction1);
        databaseController.persist(transaction2);

        BankAccount result = databaseController.getBankAccount(BankAccountNumberRABO);

        assertEquals(100, result1.getBalance());
    }

    @Test
    public void testGetBankAccountsNotExistsForCustomer(){
        databaseController.persist(customer);
        List<BankAccount> bankAccounts = databaseController.getBankAccounts(customer);
        assertEquals(0, bankAccounts.size());
    }

    @Test
    public void testGetBankAccountNotExists(){
        BankAccount bankAccount = databaseController.getBankAccount(BankAccountNumberRABO);
        assertNull(bankAccount);
    }

    @Test
    public void testGetPersistTransaction(){
        long amount = 15;
        String description = "Test transaction";
        String accountTo1 = "RABO987654321";

        String accountFrom2 = "RABO000000000";

        BankTransaction transaction1 = new BankTransaction(amount, description, BankAccountNumberRABO, accountTo1);
        BankTransaction transaction2 = new BankTransaction(amount, description, accountFrom2, BankAccountNumberSNSB);

        databaseController.persist(transaction1);
        databaseController.persist(transaction2);

        List<BankTransaction> transactions1 = databaseController.getTransaction(BankAccountNumberRABO);
        assertNotNull(transactions1);
        assertEquals(1, transactions1.size());
        assertEquals(transaction1, transactions1.get(0));


        BankTransaction transaction3 = new BankTransaction(amount, description, accountFrom3, accountTo3);
        databaseController.persist(transaction3);

        List<BankTransaction> transactions2 = databaseController.getPersistTransaction("RABO123456789");
        assertEquals(BankTransaction.class, transactions2.get(1).getClass());
        assertEquals(2, transactions2.size());
    }

    @Test
    public void testGetTransactionsNotExists(){
        List<BankTransaction> transactions = databaseController.getTransaction(BankAccountNumberRABO);
        assertNotNull(transactions);
        assertEquals(0, transactions.size());
    }

    @Test
    public void testGetAllCustomers(){
        Customer customer2 = new Customer("Rick", "Beek en Donk", "Hates Mac");
        Customer customer3 = new Customer("Arthur", "Zaltbommel", "Also hates Mac");
        databaseController.persist(customer1);
        databaseController.persist(customer2);
        databaseController.persist(customer3);

        List<Customer> customers = databaseController.getAllCustomers();
        assertEquals(Customer.class, customers.get(0).getClass());
        assertEquals(3, customers.size());
    }

    @Test
    public void testGetAllBankAccounts(){
        Customer customer2 = new Customer("Rick", "Beek en Donk", "Hates Mac");
        Customer customer3 = new Customer("Arthur", "Zaltbommel", "Also hates Mac");
        databaseController.persist(customer1);
        databaseController.persist(customer2);
        databaseController.persist(customer3);

        String bankId2 = "RABO987654321";
        String bankId3 = "RABO5647382910";
        String bankId4 = "RABO1029384756";

        BankAccount bankAccount1 = new BankAccount(customer1, bankId1);
        BankAccount bankAccount2 = new BankAccount(customer2, bankId2);
        BankAccount bankAccount3 = new BankAccount(customer3, bankId3);
        BankAccount bankAccount4 = new BankAccount(customer1, bankId4);

        databaseController.persist(bankAccount1);
        databaseController.persist(bankAccount2);
        databaseController.persist(bankAccount3);
        databaseController.persist(bankAccount4);

        List<BankAccount> bankAccounts = databaseController.getAllBankAccounts();
        assertEquals(4, bankAccounts.size());
        assertEquals(BankAccount.class, bankAccounts.get(3).getClass());
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
        assertEquals(5, bankTransactions.size());
        assertEquals(BankTransaction.class, bankTransactions.get(4).getClass());
    }
}