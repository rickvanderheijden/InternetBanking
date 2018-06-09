package unittest;

import com.ark.bank.Session;
import com.ark.bank.SessionTerminated;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

/**
 * @author Rick van der Heijden
 */
public class TestSession implements Observer {

    private static final String Name = "Name";
    private static final String Residence = "Residence";
    private final int sessionTime = 400;
    private final int sessionTimeDelta = sessionTime / 2;
    private int sessionTerminatedReceived;
    private Session session;

    @Before
    public void setUp() {
        sessionTerminatedReceived = 0;
        session = new Session(sessionTime, Name, Residence);
        session.addObserver(this);
    }

    @After
    public void tearDown() {
        session = null;
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSessionSessionTimeZero() {
        session = new Session(0, Name, Residence);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSessionSessionTimeNegative() {
        session = new Session(-1, Name, Residence);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSessionCustomerNameNull() {
        session = new Session(sessionTime, null, Residence);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSessionCustomerNameEmpty() {
        session = new Session(sessionTime, "", Residence);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSessionCustomerResidenceNull() {
        session = new Session(sessionTime, Name, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testSessionCustomerResidenceEmpty() {
        session = new Session(sessionTime, Name, "");
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
        assertEquals(0, sessionTerminatedReceived);
    }

    @Test
    public void testSessionActiveAfterRefreshAfterTimeout() throws InterruptedException {
        sleep(sessionTime + sessionTimeDelta);
        assertFalse(session.refresh());
        assertFalse(session.isActive());
        assertEquals(1, sessionTerminatedReceived);
    }

    @Test
    public void testTerminateBeforeTimeOut() throws InterruptedException {
        sleep(sessionTime - sessionTimeDelta);
        assertTrue(session.terminate());
        assertFalse(session.isActive());
        assertEquals(1, sessionTerminatedReceived);
    }

    @Test
    public void testTerminateAfterTimeOut() throws InterruptedException {
        sleep(sessionTime + sessionTimeDelta);
        assertFalse(session.terminate());
        assertEquals(1, sessionTerminatedReceived);
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
        assertEquals(1, sessionTerminatedReceived);
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

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Session) {
            if (arg instanceof SessionTerminated) {
                sessionTerminatedReceived++;
            }
        }
    }
}
