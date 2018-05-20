package unittest;

import com.ark.bank.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * @author Rick van der Heijden
 */
public class TestSession {

    private static final String Name = "Name";
    private static final String Residence = "Residence";
    private final int sessionTime = 400;
    private final int sessionTimeDelta = sessionTime / 2;
    private Session session;

    @Before
    public void setUp() {
        session = new Session(sessionTime, Name, Residence);
    }

    @After
    public void tearDown() {
        session = null;
    }

    @Test
    public void testSessionActiveBeforeTimeOut() throws InterruptedException {
        sleep(sessionTime - sessionTimeDelta);
        assertTrue(session.isActive());
    }

    @Test
    public void testSessionActiveAfterTimeOut() throws InterruptedException {
        sleep(sessionTime + sessionTimeDelta);
        assertFalse(session.isActive());
    }

    @Test
    public void testSessionActiveAfterRefreshBeforeTimeout() throws InterruptedException {
        sleep(sessionTime - sessionTimeDelta);
        assertTrue(session.refresh());
        sleep(sessionTime - sessionTimeDelta);
        assertTrue(session.isActive());
    }

    @Test
    public void testSessionActiveAfterRefreshAfterTimeout() throws InterruptedException {
        sleep(sessionTime + sessionTimeDelta);
        assertFalse(session.refresh());
        assertFalse(session.isActive());
    }

    @Test
    public void testTerminateBeforeTimeOut() throws InterruptedException {
        sleep(sessionTime - sessionTimeDelta);
        assertTrue(session.terminate());
        assertFalse(session.isActive());
    }

    @Test
    public void testTerminateAfterTimeOut() throws InterruptedException {
        sleep(sessionTime + sessionTimeDelta);
        assertFalse(session.terminate());
    }

    @Test
    public void testGetSessionKeyBeforeTimeOut() throws InterruptedException {
        sleep(sessionTime - sessionTimeDelta);
        assertEquals(36, session.getSessionKey().length());
    }

    @Test
    public void testGetSessionKeyAfterTimeOut() throws InterruptedException {
        sleep(sessionTime + sessionTimeDelta);
        assertNull(session.getSessionKey());
    }

    @Test
    public void testGetCustomerNameBeforeTimeOut() throws InterruptedException {
        sleep(sessionTime - sessionTimeDelta);
        assertEquals(Name, session.getCustomerName());
    }

    @Test
    public void testGetCustomerNameAfterTimeOut() throws InterruptedException {
        sleep(sessionTime + sessionTimeDelta);
        assertEquals(Name, session.getCustomerName());
    }

    @Test
    public void testGetCustomerResidenceBeforeTimeOut() throws InterruptedException {
        sleep(sessionTime - sessionTimeDelta);
        assertEquals(Residence, session.getCustomerResidence());
    }

    @Test
    public void testGetCustomerResidenceAfterTimeOut() throws InterruptedException {
        sleep(sessionTime + sessionTimeDelta);
        assertEquals(Residence, session.getCustomerResidence());
    }
}
