import com.ark.BankAccount;
import com.ark.Customer;
import com.ark.Transaction;
import com.ark.bank.BankController;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;


/**
 * @author Koen Sengers
 */
public class persistenceTest {

    Persistence p;
    Customer c1;

    @Before
    public void setUp() {
        p = new Persistence();
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
        p.delete(c1);
    }

    @Test
    public void testDelete() {
        boolean result = p.persist(c1);

        assertTrue(result);

        Customer cus = p.getPersistCustomer("John", "Winterfell");
        result = p.delete(cus);

        assertTrue(result);

//        Customer cust = p.getPersistCustomer("John", "Winterfell");
//        assertNull(cust);
    }

    @Test
    public void testGetPersistCustomer(){
//        Customer c2 = new Customer("Ned", "Winterfell", "Catlyn");
        boolean result1 = p.persist(c1);
//        boolean result2 = p.persist(c2);

        assertTrue(result1);
//        assertTrue(result2);

        Customer cus1 = p.getPersistCustomer("John", "Winterfell");
//        Customer cus2 = p.getPersistCustomer("Ned", "Winterfell");

        assertEquals("John", cus1.getName());
        assertEquals("Winterfell", cus1.getResidence());

        p.delete(cus1);
//        p.delete(cus2);
    }

    @Test
    public void testGetPersistBankaccount(){
        p.persist(c1);
        String idNum1 = "RABO123456789";
        String idNum2 = "RABO987654321";
        Customer cus = p.getPersistCustomer("John", "Winterfell");
        BankAccount t1 = new BankAccount(cus, idNum1);
        BankAccount t2 = new BankAccount(cus, idNum2);

        p.persist(t1);
        p.persist(t2);

        List<BankAccount> lb = p.getPersistTransaction(c1);

        assertEquals("RABO123456789", lb.get(0).getNumber());
        assertEquals("RABO987654321", lb.get(1).getNumber());

        p.delete(t1);
        p.delete(t2);
        p.delete(c1);
    }
}