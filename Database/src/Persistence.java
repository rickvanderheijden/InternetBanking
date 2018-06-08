import com.ark.Customer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Koen Sengers
 */
public class Persistence {

    private EntityManagerFactory emf;
    private EntityManager emp;
    private EntityManager emd;
    private EntityManager emc;

    public Persistence(){
        this.emf = javax.persistence.Persistence.createEntityManagerFactory("bank");
        this.emp = emf.createEntityManager();
        this.emd = emf.createEntityManager();
        this.emc = emf.createEntityManager();
    }



    public synchronized boolean persist(Object object) {
        boolean result = false;
        emp.getTransaction().begin();
        try {
            emp.persist(object);
            emp.getTransaction().commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            emp.getTransaction().rollback();
        } finally {
            emp.close();
            return result;
        }
    }

    public Customer getPersistCustomer(String name, String residence) {
        emc.getTransaction().begin();
        try {

            return (Customer) emc.createQuery("SELECT c FROM Customer c WHERE c.name = :value1 AND c.residence = :value2").setParameter("value1", name).setParameter("value2", residence).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            emc.getTransaction().rollback();
            return null;
        } finally {
            emc.close();
        }
    }

    public synchronized boolean delete(Object object) {
        if (object == null) { return false; }
        boolean result = false;
        emd.getTransaction().begin();
        try {
            emd.remove(emd.contains(object) ? object : emd.merge(object));
            emd.getTransaction().commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            emd.getTransaction().rollback();
        } finally {
            emd.close();
            return result;
        }
    }

}
