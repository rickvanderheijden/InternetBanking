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
    private EntityManager em;

    /**
     * Creates a instance of Persistence object
     */
    public Persistence(){
        this.emf = javax.persistence.Persistence.createEntityManagerFactory("bank");
        this.em = emf.createEntityManager();

    }

    /**
     * opens transaction if not already open
     */
    private void beginTransaction(){
        if(!em.getTransaction().isActive()){
            em.getTransaction().begin();
        }
    }

    /**
     * Persist a object to the database
     * @param object any object that is a entity. Can not be null.
     * @return boolean true if succesfull else false
     */
    public boolean persist(Object object) {
        if (object == null) { return false; }
        boolean result = false;
        beginTransaction();
        try {
            em.persist(object);
            em.getTransaction().commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
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
        if (name == null || residence == null){ return null; }
        beginTransaction();
        try {
            return (Customer) em.createQuery("SELECT c FROM Customer c WHERE c.name = :value1 AND c.residence = :value2").setParameter("value1", name).setParameter("value2", residence).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return null;
        }
    }

    /**
     * Retreives the transactions of a customer.
     * @param c The customer from whom the bankaccounts will be retreived. Can not be null.
     * @return A list of bankaccounts if present, else null.
     */
    public List<BankAccount> getPersistBankaccounts(Customer c) {
        if (c == null) { return null; }
        beginTransaction();
        try {
            return (List<BankAccount>) em.createQuery("SELECT b FROM BankAccount b WHERE b.owner = :value").setParameter("value", c).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return null;
        }
    }

    /**
     * Retreives a transactions
     * @param nr The account number of the bankaccount. Can not be null.
     * @return The bankaccount if present, else null.
     */
    public BankAccount getPersistBankaccount(String nr) {
        if (nr.isEmpty()) { return null; }
        beginTransaction();
        try {
            return (BankAccount) em.createQuery("SELECT b FROM BankAccount b WHERE b.number = :value").setParameter("value", nr).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return null;
        }
    }

    public List<Transaction> getPersistTransaction(String nr){
        if (nr.isEmpty()) { return null; }
        beginTransaction();
        try {
            return (List<Transaction>) em.createQuery("SELECT t FROM Transaction t WHERE t.accountFrom = :value or t.accountTo = :value").setParameter("value", nr).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return null;
        }
    }

    /**
     * Deletes an object from the database
     * @param object Any object that is an entity. Can not be null.
     * @return Boolean true if successfull, else false.
     */
    public boolean delete(Object object) {
        if (object == null) { return false; }
        boolean result = false;
        beginTransaction();
        try {
            em.remove(em.contains(object) ? object : em.merge(object));
            em.getTransaction().commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            return result;
        }
    }

}
