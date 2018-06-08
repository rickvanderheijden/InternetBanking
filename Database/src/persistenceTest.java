import com.ark.Customer;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;


/**
 * @author Koen Sengers
 */
public class persistenceTest {

    Persistence p;

    @Before
    public void setUp(){ p = new Persistence(); }

    @Test
    public void testPersist() {
        Customer c = new Customer("John", "Winterfell", "Ghost");
        boolean result = p.persist(c);

        assertTrue(result);

        Customer cus = p.getPersistCustomer("John", "Winterfell");
        assertEquals("John", cus.getName());
        assertEquals("Winterfell", cus.getResidence());

        p.delete(cus);

    }

    @Test
    public void testDelete() {
        Customer c = new Customer("John", "Winterfell", "Ghost");
        boolean result = p.persist(c);

        assertTrue(result);

        Customer cus = p.getPersistCustomer("John", "Winterfell");
        result = p.delete(cus);

        assertTrue(result);

        Customer cust = p.getPersistCustomer("John", "Winterfell");
        assertNull(cust);
    }

    @Test
    public void testGetPersistCustomer(){
        Customer c1 = new Customer("John", "Winterfell", "Ghost");
        Customer c2 = new Customer("Ned", "Winterfell", "Catlyn");
        boolean result1 = p.persist(c1);
        boolean result2 = p.persist(c2);

        assertTrue(result1);
        assertTrue(result2);

        Customer cus1 = p.getPersistCustomer("John", "Winterfell");
        Customer cus2 = p.getPersistCustomer("John", "Winterfell");

        p.delete(cus1);
        p.delete(cus2);
    }
}