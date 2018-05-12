package unittest;

import com.ark.bank.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Thread.sleep;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Rick van der Heijden
 */
public class TestSession {

    private final int sessionTime = 400;
    private final int sessionTimeDelta = sessionTime / 2;
    private Session session;

    @Before
    public void setUp() {
        session = new Session(sessionTime);
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

}
