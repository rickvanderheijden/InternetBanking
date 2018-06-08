import com.ark.Customer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Koen Sengers
 */
public class Persistence {

    EntityManagerFactory emf;
    EntityManager em;

    public Persistence(){
        this.emf = javax.persistence.Persistence.createEntityManagerFactory("bank");
        this.em = emf.createEntityManager();
    }



    public boolean persist(Object object) {
        boolean result = false;
        em.getTransaction().begin();
        try {
            em.persist(object);
            em.getTransaction().commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        } finally {
            em.close();
            return result;
        }
    }
}
