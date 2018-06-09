import com.ark.BankAccount;
import com.ark.Customer;
import com.ark.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

/**
 * @author Koen Sengers
 */
public class Persistence {

    private EntityManagerFactory emf;
    private EntityManager emp;
    private EntityManager emd;
    private EntityManager emc;
    private EntityManager emb;

    /**
     * Creates a instance of Persistence object
     */
    public Persistence(){
        this.emf = javax.persistence.Persistence.createEntityManagerFactory("bank");
        this.emp = emf.createEntityManager();
        this.emd = emf.createEntityManager();
        this.emc = emf.createEntityManager();
        this.emb = emf.createEntityManager();

    }

    /**
     * Persist a object to the database
     * @param object any object that is a entity. Can not be null.
     * @return boolean true if succesfull else false
     */
    public synchronized boolean persist(Object object) {
        if (object.equals(null)) { return false; }
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
            return result;
        }
    }

    /**
     * Retreives the customer from the database.
     * @param name The name of the customer. Can not be null.
     * @param residence The residence of the customer. Can not be null.
     * @return The customer if found, else null
     */
    public Customer getPersistCustomer(String name, String residence) {
        if (name.equals(null) || residence.equals(null)){ return null; }

        emc.getTransaction().begin();
        try {
            return (Customer) emc.createQuery("SELECT c FROM Customer c WHERE c.name = :value1 AND c.residence = :value2").setParameter("value1", name).setParameter("value2", residence).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            emc.getTransaction().rollback();
            return null;
        } finally {
        }
    }

    /**
     * Retreives the transactions of a customer.
     * @param c The customer from whom the bankaccounts will be retreived. Can not be null.
     * @return A list of bankaccounts if present, else null.
     */
    public List<BankAccount> getPersistTransaction(Customer c) {
        if (c.equals(null)) { return null; }
        emb.getTransaction().begin();
        try {
            return (List<BankAccount>) emb.createQuery("SELECT b FROM BankAccount b WHERE b.owner = :value").setParameter("value", c).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            emc.getTransaction().rollback();
            return null;
        } finally {
        }
    }

    /**
     * Deletes an object from the database
     * @param object Any object that is an entity. Can not be null.
     * @return Boolean true if successfull, else false.
     */
    public synchronized boolean delete(Object object) {
        if (object.equals(null)) { return false; }
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
            return result;
        }
    }

}
