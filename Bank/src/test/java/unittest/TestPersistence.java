package unittest;

import com.ark.BankAccount;
import com.ark.BankTransaction;
import com.ark.Customer;
import com.ark.bank.Persistence;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.*;


/**
 * @author Koen Sengers
 */
public class TestPersistence {

    Persistence p;
    Customer c1;

    @Before
    public void setUp() {
        p = new Persistence("Test");
        c1 = new Customer("John", "Winterfell", "Ghost");
    }

    @Test
    public void testPersistCustomer() {
        boolean result = p.persist(c1);

        assertTrue(result);

        Customer cus = p.getPersistCustomer("John", "Winterfell");
        assertEquals("John", cus.getName());
        assertEquals("Winterfell", cus.getResidence());

        p.delete(cus);
    }

    @Test
    public void testPersistBankAccount(){
        p.persist(c1);
        String idNum = "RABO123456789";
        Customer cus = p.getPersistCustomer("John", "Winterfell");
        BankAccount t1 = new BankAccount(cus, idNum);

        boolean result = p.persist(t1);

        assertTrue(result);

        p.delete(t1);
        p.delete(cus);
    }

    @Test
    public void testPersistTransaction(){
        long amount = 15;
        String description = "Test transaction";
        String accountFrom = "RABO123456789";
        String accountTo = "RABO987654321";

        BankTransaction t = new BankTransaction(amount, description, accountFrom, accountTo);
        boolean result = p.persist(t);
        assertTrue(result);

        p.delete(t);
    }

    @Test
    public void testPersistNULL(){
        assertEquals(false, p.persist(null));
    }

    @Test
    public void testUpdateBankAccount(){
        p.persist(c1);
        String idNum = "RABO123456789";
        Customer cus = p.getPersistCustomer("John", "Winterfell");

        BankAccount t1 = new BankAccount(cus, idNum);

        assertEquals(10000, t1.getCreditLimit());

        boolean result1 = p.persist(t1);
        assertTrue(result1);

        t1.setCreditLimit(9000);
        boolean result2 = p.persist(t1);

        List<BankAccount> b = p.getPersistBankaccounts(cus);
        assertTrue(result2);
        assertEquals(1, b.size());
        assertEquals(9000, b.get(0).getCreditLimit());

        p.delete(t1);
        p.delete(cus);
    }

    @Test
    public void testDelete() {
        boolean result = p.persist(c1);

        assertTrue(result);

        Customer cus = p.getPersistCustomer("John", "Winterfell");
        result = p.delete(cus);

        assertTrue(result);

        Customer cust = p.getPersistCustomer("John", "Winterfell");
        assertNull(cust);
    }

    @Test
    public void testGetPersistCustomer(){
        Customer c2 = new Customer("Ned", "Winterfell", "Catlyn");
        boolean result1 = p.persist(c1);
        boolean result2 = p.persist(c2);

        assertTrue(result1);
        assertTrue(result2);

        Customer cus1 = p.getPersistCustomer("John", "Winterfell");
        Customer cus2 = p.getPersistCustomer("Ned", "Winterfell");

        assertEquals("John", cus1.getName());
        assertEquals("Winterfell", cus1.getResidence());

        p.delete(cus1);
        p.delete(cus2);
    }

    @Test
    public void testGetPersistCustomerNULL(){
        Customer cus1 = p.getPersistCustomer("John", "Winterfell");
        assertNull(cus1);
    }

    @Test
    public void testGetPersistBankaccounts(){
        p.persist(c1);
        String idNum1 = "RABO123456789";
        String idNum2 = "RABO987654321";
        Customer cus = p.getPersistCustomer("John", "Winterfell");
        BankAccount t1 = new BankAccount(cus, idNum1);
        BankAccount t2 = new BankAccount(cus, idNum2);

        p.persist(t1);
        p.persist(t2);

        List<BankAccount> lb = p.getPersistBankaccounts(c1);

        assertEquals("RABO123456789", lb.get(0).getNumber());
        assertEquals("RABO987654321", lb.get(1).getNumber());

        p.delete(t1);
        p.delete(t2);
        p.delete(c1);
    }

    @Test
    public void testGetPersistBankaccount(){
        p.persist(c1);
        String idNum1 = "RABO123456789";
        String idNum2 = "RABO987654321";
        Customer cus = p.getPersistCustomer("John", "Winterfell");
        BankAccount t1 = new BankAccount(cus, idNum1);
        BankAccount t2 = new BankAccount(cus, idNum2);
        t1.increaseBalance(100);

        p.persist(t1);
        p.persist(t2);

        BankAccount result1 = p.getPersistBankaccount("RABO123456789");

        assertEquals(100, result1.getBalance());

        p.delete(t1);
        p.delete(t2);
        p.delete(c1);
    }

    @Test
    public void testGetPersistBankaccountsNULL(){
        p.persist(c1);

        List<BankAccount> lb = p.getPersistBankaccounts(c1);
        assertEquals(0, lb.size());

        p.delete(c1);
    }

    @Test
    public void testGetPersistBankaccountNULL(){
        p.persist(c1);

        BankAccount b1 = p.getPersistBankaccount("RABO123456789");
        assertNull(b1);

        p.delete(c1);
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

        BankTransaction tr1 = new BankTransaction(amount, description, accountFrom1, accountTo1);
        BankTransaction tr2 = new BankTransaction(amount, description, accountFrom2, accountTo2);

        p.persist(tr1);
        p.persist(tr2);

        List<BankTransaction> trl1 = p.getPersistTransaction("RABO123456789");
        assertEquals(1, trl1.size());

        BankTransaction tr3 = new BankTransaction(amount, description, accountFrom3, accountTo3);
        p.persist(tr3);

        List<BankTransaction> trl2 = p.getPersistTransaction("RABO123456789");
        assertEquals(2, trl2.size());

        p.delete(tr1);
        p.delete(tr2);
        p.delete(tr3);
    }

    @Test
    public void testGetPersistTransactionNULL(){

        List<BankTransaction> tr = p.getPersistTransaction("RABO123456789");
        assertEquals(0, tr.size());
    }
}