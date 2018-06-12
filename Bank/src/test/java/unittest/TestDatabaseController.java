package unittest;

import com.ark.BankAccount;
import com.ark.BankTransaction;
import com.ark.Customer;
import com.ark.bank.DatabaseController;
import org.junit.Before;
import org.junit.Test;

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
        customer1 = new Customer("John", "Winterfell", "Ghost");
    }

    @Test
    public void testPersistCustomer() {
        boolean result = databaseController.persist(customer1);

        assertTrue(result);

        Customer result_get = databaseController.getPersistCustomer("John", "Winterfell");
        assertEquals("John", result_get.getName());
        assertEquals("Winterfell", result_get.getResidence());

        databaseController.delete(result_get);
    }

    @Test
    public void testPersistBankAccount(){
        databaseController.persist(customer1);
        String bankId = "RABO123456789";
        Customer customer2 = databaseController.getPersistCustomer("John", "Winterfell");
        BankAccount transaction1 = new BankAccount(customer2, bankId);

        boolean result = databaseController.persist(transaction1);

        assertTrue(result);

        databaseController.delete(transaction1);
        databaseController.delete(customer2);
    }

    @Test
    public void testPersistTransaction(){
        long amount = 15;
        String description = "Test transaction";
        String accountFrom = "RABO123456789";
        String accountTo = "RABO987654321";

        BankTransaction transaction1 = new BankTransaction(amount, description, accountFrom, accountTo);
        boolean result = databaseController.persist(transaction1);
        assertTrue(result);

        databaseController.delete(transaction1);
    }

    @Test
    public void testPersistNULL(){
        assertEquals(false, databaseController.persist(null));
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

        databaseController.delete(transaction1);
        databaseController.delete(customer2);
    }

    @Test
    public void testDelete() {
        boolean result = databaseController.persist(customer1);

        assertTrue(result);

        Customer customer2 = databaseController.getPersistCustomer("John", "Winterfell");
        result = databaseController.delete(customer2);

        assertTrue(result);

        Customer customer3 = databaseController.getPersistCustomer("John", "Winterfell");
        assertNull(customer3);
    }

    @Test
    public void testGetPersistCustomer(){
        Customer customer2 = new Customer("Ned", "Winterfell", "Catlyn");
        boolean result1 = databaseController.persist(customer1);
        boolean result2 = databaseController.persist(customer2);

        assertTrue(result1);
        assertTrue(result2);

        Customer customer_get1 = databaseController.getPersistCustomer("John", "Winterfell");
        Customer customer_get2 = databaseController.getPersistCustomer("Ned", "Winterfell");

        assertEquals("John", customer_get1.getName());
        assertEquals("Winterfell", customer_get1.getResidence());

        databaseController.delete(customer_get1);
        databaseController.delete(customer_get2);
    }

    @Test
    public void testGetPersistCustomerNULL(){
        Customer customer2 = databaseController.getPersistCustomer("John", "Winterfell");
        assertNull(customer2);
    }

    @Test
    public void testGetPersistBankaccounts(){
        databaseController.persist(customer1);
        String bankId1 = "RABO123456789";
        String bankId2 = "RABO987654321";
        Customer customer2 = databaseController.getPersistCustomer("John", "Winterfell");
        BankAccount transaction1 = new BankAccount(customer2, bankId1);
        BankAccount transaction2 = new BankAccount(customer2, bankId2);

        databaseController.persist(transaction1);
        databaseController.persist(transaction2);

        List<BankAccount> bankAccounts = databaseController.getPersistBankaccounts(customer1);

        assertEquals(BankAccount.class, bankAccounts.get(0).getClass());
        assertEquals("RABO123456789", bankAccounts.get(0).getNumber());
        assertEquals("RABO987654321", bankAccounts.get(1).getNumber());

        databaseController.delete(transaction1);
        databaseController.delete(transaction2);
        databaseController.delete(customer1);
    }

    @Test
    public void testGetPersistBankaccount(){
        databaseController.persist(customer1);
        String bankId1 = "RABO123456789";
        String bankId2 = "RABO987654321";
        Customer cus = databaseController.getPersistCustomer("John", "Winterfell");
        BankAccount transaction1 = new BankAccount(cus, bankId1);
        BankAccount transaction2 = new BankAccount(cus, bankId2);
        transaction1.increaseBalance(100);

        databaseController.persist(transaction1);
        databaseController.persist(transaction2);

        BankAccount result1 = databaseController.getPersistBankaccount("RABO123456789");

        assertEquals(100, result1.getBalance());

        databaseController.delete(transaction1);
        databaseController.delete(transaction2);
        databaseController.delete(customer1);
    }

    @Test
    public void testGetPersistBankaccountsNULL(){
        databaseController.persist(customer1);

        List<BankAccount> bankAccounts = databaseController.getPersistBankaccounts(customer1);
        assertEquals(0, bankAccounts.size());

        databaseController.delete(customer1);
    }

    @Test
    public void testGetPersistBankaccountNULL(){
        databaseController.persist(customer1);

        BankAccount bankAccount1 = databaseController.getPersistBankaccount("RABO123456789");
        assertNull(bankAccount1);

        databaseController.delete(customer1);
    }

    @Test
    public void testGetPersistTransaction(){
        long amount = 15;
        String description = "Test transaction";
        String accountFrom1 = "RABO123456789";
        String accountTo1 = "RABO987654321";

        String accountFrom2 = "RABO000000000";
        String accountTo2 = "SNSB123456789";

        String accountFrom3 = "SNSB123456789";
        String accountTo3 = "RABO123456789";

        BankTransaction transaction1 = new BankTransaction(amount, description, accountFrom1, accountTo1);
        BankTransaction transaction2 = new BankTransaction(amount, description, accountFrom2, accountTo2);

        databaseController.persist(transaction1);
        databaseController.persist(transaction2);

        List<BankTransaction> transactions1 = databaseController.getPersistTransaction("RABO123456789");
        assertEquals(BankTransaction.class, transactions1.get(0).getClass());
        assertEquals(1, transactions1.size());

        BankTransaction transaction3 = new BankTransaction(amount, description, accountFrom3, accountTo3);
        databaseController.persist(transaction3);

        List<BankTransaction> transactions2 = databaseController.getPersistTransaction("RABO123456789");
        assertEquals(BankTransaction.class, transactions2.get(1).getClass());
        assertEquals(2, transactions2.size());

        databaseController.delete(transaction1);
        databaseController.delete(transaction2);
        databaseController.delete(transaction3);
    }

    @Test
    public void testGetPersistTransactionNULL(){

        List<BankTransaction> transaction1 = databaseController.getPersistTransaction("RABO123456789");
        assertEquals(0, transaction1.size());
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

        databaseController.delete(customer1);
        databaseController.delete(customer2);
        databaseController.delete(customer3);
    }

    @Test
    public void testGetAllBankAccounts(){
        Customer customer2 = new Customer("Rick", "Beek en Donk", "Hates Mac");
        Customer customer3 = new Customer("Arthur", "Zaltbommel", "Also hates Mac");
        databaseController.persist(customer1);
        databaseController.persist(customer2);
        databaseController.persist(customer3);

        String bankId1 = "RABO123456789";
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

        databaseController.delete(bankAccount1);
        databaseController.delete(bankAccount2);
        databaseController.delete(bankAccount3);
        databaseController.delete(bankAccount4);
        databaseController.delete(customer1);
        databaseController.delete(customer2);
        databaseController.delete(customer3);
    }

    @Test
    public void testGetAllBankTransactions(){
        BankTransaction transaction1 = new BankTransaction(15, "Test1", "RABO123456789", "RABO987654321");
        BankTransaction transaction2 = new BankTransaction(15, "Test1", "RABO123456789", "RABO987654321");
        BankTransaction transaction3 = new BankTransaction(15, "Test1", "RABO123456789", "RABO987654321");
        BankTransaction transaction4 = new BankTransaction(15, "Test1", "RABO123456789", "RABO987654321");
        BankTransaction transaction5 = new BankTransaction(15, "Test1", "RABO123456789", "RABO987654321");
        databaseController.persist(transaction1);
        databaseController.persist(transaction2);
        databaseController.persist(transaction3);
        databaseController.persist(transaction4);
        databaseController.persist(transaction5);

        List<BankTransaction> bankTransactions = databaseController.getAllBankTransactions();
        assertEquals(5, bankTransactions.size());
        assertEquals(BankTransaction.class, bankTransactions.get(4).getClass());

        databaseController.delete(transaction1);
        databaseController.delete(transaction2);
        databaseController.delete(transaction3);
        databaseController.delete(transaction4);
        databaseController.delete(transaction5);
    }
}