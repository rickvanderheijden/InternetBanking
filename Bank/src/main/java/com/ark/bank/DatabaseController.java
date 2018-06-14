package com.ark.bank;

import com.ark.BankAccount;
import com.ark.Customer;
import com.ark.BankTransaction;
import jdk.nashorn.internal.runtime.regexp.joni.Warnings;
import org.hibernate.service.spi.ServiceException;
import org.jboss.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Koen Sengers
 */
@SuppressWarnings("unchecked")
public final class DatabaseController implements IDatabaseController {
    private EntityManager entityManager;
    private final String bankId;

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
            exception.printStackTrace();
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
            System.out.println(e.getMessage());
            entityManager.getTransaction().rollback();
        }

        return result;
    }

    private boolean persistBankAccount(BankAccount bankAccount) {
        if (bankAccount.getOwner() == null) { return false; }
        Customer customer = getCustomer(bankAccount.getOwner().getName(), bankAccount.getOwner().getResidence());
        if(customer == null) {
            persistBase(bankAccount.getOwner());
        }
        return persistBase(bankAccount);
    }

    @Override
    public Customer getCustomer(String name, String residence) {
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
    public List<IBankAccount> getAllBankAccounts(){
        beginTransaction();
        try {
            return (List<IBankAccount>) entityManager.createQuery("SELECT b FROM BankAccount b").getResultList();
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
    public List<IBankAccount> getBankAccounts(Customer customer) {
        List<IBankAccount> bankAccounts = new ArrayList<>();

        if (customer == null) { return bankAccounts; }

        beginTransaction();
        try {
            bankAccounts = entityManager.createQuery("SELECT b FROM BankAccount b WHERE b.owner = :value").setParameter("value", customer).getResultList();
            return bankAccounts;
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return bankAccounts;
        }
    }

    @Override
    public IBankAccount getBankAccount(String bankAccountNumber) {
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
    public List<BankTransaction> getBankTransactions(String bankAccountNumber){
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
    public boolean transactionExists(BankTransaction bankTransaction){
        if (bankTransaction == null ) { return false; }
        BankTransaction transaction;
        beginTransaction();
        try {
            transaction = (BankTransaction) entityManager.createQuery("SELECT t FROM BankTransaction t WHERE t.accountTo = :value1 and t.accountFrom = :value2 and t.transactionDate = :value3 and t.amount = :value4").setParameter("value1", bankTransaction.getAccountTo()).setParameter("value2", bankTransaction.getAccountFrom()).setParameter("value3", bankTransaction.getDate()).setParameter("value4", bankTransaction.getAmount()).getSingleResult();
            if (transaction == null) { return false; }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            return false;
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

    @Override
    public boolean deleteCustomerByNameAndResidence(String name, String residence){
        if(name.isEmpty() || residence.isEmpty()) { return false; }
        Customer customer = getCustomer(name, residence);
        return delete(customer);
    }

    /**
     * Deletes all associated objects of customer and customer.
     * @param customer The customer instance.
     * @return Boolean true if successfull, else false.
     */
    private boolean deleteCustomer(Customer customer) {
        List<IBankAccount> bankAccounts = this.getBankAccounts(customer);
        if(bankAccounts.size() >= 1){
            for (IBankAccount bankAccount : bankAccounts) {
                this.deleteBase(getBankAccount(bankAccount.getNumber()));
            }
        }
        return deleteBase(getCustomer(customer.getName(), customer.getResidence()));
    }

    /**
     * Deletes BankAccount
     * @param bankAccount The bankaccount instance.
     * @return Boolean true if successfull, else false.
     */
    private boolean deleteBankAccount(BankAccount bankAccount) {

        //TODO: ???????
        return deleteBase(getBankAccount(bankAccount.getNumber()));
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
        }
        return result;
    }

    public boolean deleteAll(){
        boolean result;
        beginTransaction();
        try {
            result = deleteAllBankAccounts();
            result &= deleteAllBankTransactions();
            result &= deleteAllCustomers();
        } catch (Exception e){
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            result = false;
        }

        return result;
    }

    private boolean deleteAllBankAccounts(){
        boolean result = false;
        beginTransaction();
        try {
            entityManager.createQuery("DELETE FROM BankAccount b").executeUpdate();
            entityManager.getTransaction().commit();
            result = true;
        } catch (Exception e){
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }

        return result;
    }

    private boolean deleteAllBankTransactions(){
        boolean result = false;
        beginTransaction();
        try {
            entityManager.createQuery("DELETE FROM BankTransaction b").executeUpdate();
            entityManager.getTransaction().commit();
            result = true;
        } catch (Exception e){
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }

        return result;
    }

    private boolean deleteAllCustomers(){
        boolean result = false;
        beginTransaction();
        try {
            entityManager.createQuery("DELETE FROM Customer c").executeUpdate();
            entityManager.getTransaction().commit();
            result = true;
        } catch (Exception e){
            e.printStackTrace();
            entityManager.getTransaction().rollback();
        }

        return result;
    }
}
