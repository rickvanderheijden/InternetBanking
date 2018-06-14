package com.ark.bank;

import com.ark.BankAccount;
import com.ark.Customer;
import com.ark.BankTransaction;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.service.spi.ServiceException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.net.ConnectException;
import java.sql.SQLNonTransientConnectionException;
import java.util.List;

/**
 * @author Koen Sengers
 */
public final class DatabaseController implements IDatabaseController {
    private EntityManager entityManager;
    private String bankId;

    /**
     * Creates a instance of DatabaseController object
     * @param bankId The bank id of the current bank.
     */
    public DatabaseController(String bankId) throws ServiceException {
        this.bankId = bankId;
    }

    @Override
    public boolean connectToDatabase() {
        boolean result = false;
        try {
            EntityManagerFactory entityManagerFactory = javax.persistence.Persistence.createEntityManagerFactory("bank" + bankId);
            this.entityManager = entityManagerFactory.createEntityManager();
            result = true;
        } catch (Exception exception) {
            System.out.println("Could not connect to database");
            this.entityManager = null;
        }
        return result;
    }

    /**
     * opens transaction if not already open
     */
    private void beginTransaction(){
        if(!entityManager.getTransaction().isActive()){
            entityManager.getTransaction().begin();
        }
    }

    @Override
    public boolean persist(Object object) {
        if (object == null) { return false; }
        boolean result;
        if (object instanceof BankAccount) {
            result = persistBankAccount((BankAccount) object);
        } else {
            result = persistBase(object);
        }
        return result;
    }

    private boolean persistBase(Object object) {
        boolean result = false;
        beginTransaction();
        try {
            entityManager.persist(object);
            entityManager.getTransaction().commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            entityManager.getTransaction().rollback();
        } finally {
            return result;
        }
    }

    private boolean persistBankAccount(BankAccount bankAccount) {
        if (bankAccount.getOwner() == null) { return false; }
        Customer customer = getPersistCustomer(bankAccount.getOwner().getName(), bankAccount.getOwner().getResidence());
        if(customer == null) {
            persistBase(bankAccount.getOwner());
        }
        return persistBase(bankAccount);
    }

    @Override
    public Customer getPersistCustomer(String name, String residence) {
        if (name == null || residence == null){ return null; }
        beginTransaction();
        try {
            return (Customer) entityManager.createQuery("SELECT c FROM Customer c WHERE c.name = :value1 AND c.residence = :value2").setParameter("value1", name).setParameter("value2", residence).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public List<Customer> getAllCustomers(){
        beginTransaction();
        try {
            return (List<Customer>) entityManager.createQuery("SELECT c FROM Customer c").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public List<BankAccount> getAllBankAccounts(){
        beginTransaction();
        try {
            return (List<BankAccount>) entityManager.createQuery("SELECT b FROM BankAccount b").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public List<BankTransaction> getAllBankTransactions(){
        beginTransaction();
        try {
            return (List<BankTransaction>) entityManager.createQuery("SELECT b FROM BankTransaction b").getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public List<BankAccount> getPersistBankaccounts(Customer customer) {
        if (customer == null) { return null; }
        beginTransaction();
        try {
            return (List<BankAccount>) entityManager.createQuery("SELECT b FROM BankAccount b WHERE b.owner = :value").setParameter("value", customer).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public BankAccount getPersistBankaccount(String bankAccountNumber) {
        if (bankAccountNumber.isEmpty()) { return null; }
        beginTransaction();
        try {
            return (BankAccount) entityManager.createQuery("SELECT b FROM BankAccount b WHERE b.number = :value").setParameter("value", bankAccountNumber).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public List<BankTransaction> getPersistTransaction(String bankAccountNumber){
        if (bankAccountNumber.isEmpty()) { return null; }
        beginTransaction();
        try {
            return (List<BankTransaction>) entityManager.createQuery("SELECT t FROM BankTransaction t WHERE t.accountFrom = :value or t.accountTo = :value").setParameter("value", bankAccountNumber).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return null;
        }
    }

    @Override
    public boolean delete(Object object) {
        if (object == null) { return false; }
        if(object instanceof Customer) {
            return deleteCustomer((Customer) object);
        } else if(object instanceof BankAccount) {
            return deleteBankAccount((BankAccount) object);
        } else {
            return deleteBase(object);
        }
    }

    /**
     * Deletes all associated objects of customer and customer.
     * @param object The customer instance.
     * @return Boolean true if successfull, else false.
     */
    private boolean deleteCustomer(Customer object) {
        List<BankAccount> l = this.getPersistBankaccounts(object);
        if(l.size() >= 1){
            for (int i = 0; i < l.size(); i++){
                this.deleteBase(getPersistBankaccount((l.get(i)).getNumber()));
            }
        }
        return deleteBase(getPersistCustomer(object.getName(), object.getResidence()));
    }

    /**
     * Deletes BankAccount
     * @param object The bankaccount instance.
     * @return Boolean true if successfull, else false.
     */
    private boolean deleteBankAccount(BankAccount object) {

        return deleteBase(getPersistBankaccount(object.getNumber()));
    }

    /**
     * Base delete method with duplicated code
     * @param object Any object that is an entity. Can not be null.
     * @return Boolean true if successfull, else false.
     */
    private boolean deleteBase(Object object){
        boolean result = false;
        beginTransaction();
        try {
            entityManager.remove(entityManager.contains(object) ? object : entityManager.merge(object));
            entityManager.getTransaction().commit();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            return result;
        }
    }

    public boolean deleteALL(){
        boolean result = false;
        beginTransaction();
        try {
            deleteALLBankAccounts();
            deleteALLBankTransactions();
            deleteALLCustomers();
            result = true;
        } catch (Exception e){
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            return result;
        }
    }

    private boolean deleteALLBankAccounts(){
        boolean result = false;
        beginTransaction();
        try {
            entityManager.createQuery("DELETE FROM BankAccount b").executeUpdate();
            entityManager.getTransaction().commit();
            result = true;
        } catch (Exception e){
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            return result;
        }
    }

    private boolean deleteALLBankTransactions(){
        boolean result = false;
        beginTransaction();
        try {
            entityManager.createQuery("DELETE FROM BankTransaction b").executeUpdate();
            entityManager.getTransaction().commit();
            result = true;
        } catch (Exception e){
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            return result;
        }
    }

    private boolean deleteALLCustomers(){
        boolean result = false;
        beginTransaction();
        try {
            entityManager.createQuery("DELETE FROM Customer c").executeUpdate();
            entityManager.getTransaction().commit();
            result = true;
        } catch (Exception e){
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            return result;
        }
    }
}
